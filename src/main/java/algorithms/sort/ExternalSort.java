package algorithms.sort;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @description 多路归并排序
 * @date 2023/6/2 21:10
 * @author: qyl
 */
public class ExternalSort {
    /**
     * 输入文件
     */
    private File input;
    /**
     * 输出目录
     */
    private File outputDir;


    /**
     * reduce目录
     */
    private File reduceDir;

    private final Integer hashLength = 2048;

    private final String defaultString = "######## -1";

    private final ExecutorService threadPool = new ThreadPoolExecutor (10,
            200,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue (1048576),
            r -> {
                Thread thread = new Thread (r);
                thread.setName ("sort-thread-%d");
                return thread;
            },
            new ThreadPoolExecutor.AbortPolicy ());


    public ExternalSort(File input, File outputDir) {
        this.input = input;
        this.outputDir = outputDir;
    }

    /**
     * 问题：有一个1G大小的一个文件，里面每一行是一个词，
     * 词的大小不超过16字节，内存限制大小是1M。返回频数最高的100个词.
     * <p>
     * 分析：1     MB   = 2^20  字节
     * 1     GB   = 2^30  字节
     * 内存中每次可以读入  2^20 / 2^4 = 2^16 个单词
     * 文件有            2^30 / 2^4 >= 2^26 个单词
     * 至少要分成         2^10个文件
     * 这里考虑 2048 个文件，为了统计词频使用hash
     * <p>
     * 算法实现：先单词hash之后划分到文件，之后每个文件单独进行词频统计，并且完成排序。
     * 之后直接2000个文件第一行进行读取，最后用置换算法读入心如据。
     * <p>
     * 问题：  会出现分区不均，极致情况下还是容易爆掉内存
     **/
    public List<String> top100() throws IOException {
        distributeWordsToDifferentFileByHash ();
        File[] filesToBeMerges = outputDir.listFiles ();
        if (filesToBeMerges != null) {
            countWordsForDifferentFiles (filesToBeMerges);
            return mergeFiles (filesToBeMerges);
        }
        return null;
    }

    /**
     * 分发数据
     *
     * @throws IOException
     */
    private void distributeWordsToDifferentFileByHash() throws IOException {
        if (!outputDir.exists ()) {
            outputDir.mkdirs ();
        }
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader (new FileReader (input));
            String line;
            while ((line = reader.readLine ()) != null) {
                String outputFileName = String.valueOf (hashCode (line));
                if (outputDir != null) {
                    File outputFile = new File (outputDir, outputFileName);
                    if (!outputFile.exists ()) {
                        outputFile.createNewFile ();
                    }
                    writer = new BufferedWriter (new FileWriter (outputFile,true));
                    writer.write (line);
                    writer.newLine();
                    writer.flush ();
                }
            }
        } finally {
            closeGracefully (reader, writer);
            System.out.println ("finish shuffle tasks");
        }
    }

    /**
     * 对文件数据统计
     *
     * @param files
     */
    private void countWordsForDifferentFiles(File[] files) {
        CompletableFuture<?>[] futures = Arrays.stream (files)
                .map (file -> CompletableFuture.runAsync (new ReduceTask (file),threadPool))
                .toArray (CompletableFuture[]::new);

        CompletableFuture.allOf (futures).whenComplete ((result, throwable) -> {
            System.out.println ("finish reduce tasks");
        }).join ();
    }

    /**
     * 归并
     *
     * @param files
     */
    private List<String> mergeFiles(File[] files) throws IOException {
        BufferedReader[] readers = Arrays.stream (files).map (file -> {
            try {
                return new BufferedReader (new FileReader (file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException (e);
            }
        }).toArray (BufferedReader[]::new);

        List<String> res = new ArrayList<> ();
        Map<BufferedReader, String> map = new HashMap<> ();
        String value;
        for (BufferedReader reader : readers) {
            if ((value = reader.readLine ()) != null) {
                map.put (reader, value);
            } else {
                map.put (reader, defaultString);
            }
        }

        /**
         * 开始归并
         */
        int total = 0;
        BufferedReader maxReader;
        int maxValue;
        while (total < 100) {
            maxReader = null;
            maxValue = -1;
            for (Map.Entry<BufferedReader, String> entry : map.entrySet ()) {
                BufferedReader reader = entry.getKey ();
                int count = Integer.parseInt (entry.getValue ().split (" ")[1]);
                if (count > maxValue) {
                    maxReader = reader;
                    maxValue = count;
                }
            }
            /**
             * 文件还有内容
             */
            if (maxValue != -1) {
                total++;
                if ((value = maxReader.readLine ()) != null) {
                    map.put (maxReader, value);
                } else {
                    map.put (maxReader, defaultString);
                }
            } else {
                return res;
            }
        }
        return res;
    }

    private int hashCode(String word) {
        return (word.hashCode () & Integer.MAX_VALUE) % hashLength;
    }

    private void closeGracefully(Reader reader, Writer writer) {
        if (reader != null) {
            try {
                reader.close ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
        if (writer != null) {
            try {
                writer.close ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

    class ReduceTask implements Runnable {
        private final File file;

        ReduceTask(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            Map<String, Integer> map = new HashMap<> (1024 * 128);
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                /**
                 * 读取所有文件内容
                 */
                reader = new BufferedReader (new FileReader (file));
                String key;
                while ((key = reader.readLine ()) != null) {
                    map.put (key, map.getOrDefault (key, 0) + 1);
                }
                /**
                 * 清空文件
                 */
                writer = new BufferedWriter (new FileWriter (file, false));
                writer.write ("");
                writer.flush ();

                /**
                 * reduce
                 */
                writer = new BufferedWriter (new FileWriter (file, true));
                List<Map.Entry<String, Integer>> entries = new ArrayList<> (map.entrySet ());
                entries.sort ((e1, e2) -> {
                    if (e1.getValue ().compareTo (e2.getValue ()) == 0) {
                        return e1.getKey ().compareTo (e2.getKey ());
                    } else {
                        return e1.getValue ().compareTo (e2.getValue ());
                    }
                });
                for (Map.Entry<String, Integer> entry : entries) {
                    writer.write (entry.getKey () + " " + entry.getValue ());
                    writer.newLine();
                }
                writer.flush ();
            } catch (IOException e) {
                e.printStackTrace ();
            } finally {
                closeGracefully (reader, writer);
            }
        }
    }
}
