package algorithms.sort;

import java.io.*;
import java.util.*;

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

    private final Integer HASH_LENGTH = 2000;

    public ExternalSort(File input, File outputDir) {
        this.input = input;
        this.outputDir = outputDir;
    }


    public String[] top100() throws IOException {
        distributeWordsToDifferentFileByHash ();
        File[] filesToBeMerges = outputDir.listFiles ();
        countWordsForDifferentFiles(filesToBeMerges);
        return mergeFiles(filesToBeMerges);
    }

    /**
     * 问题：有一个1G大小的一个文件，里面每一行是一个词，
     * 词的大小不超过16字节，内存限制大小是1M。返回频数最高的100个词.
     *
     * 分析：1     MB   = 2^20  字节
     *      1     GB   = 2^30  字节
     *      内存中每次可以读入  2^20 / 2^4 = 2^16 个单词
     *      文件有            2^30 / 2^4 >= 2^26 个单词
     *      至少要分成         2^10个文件
     *      这里考虑 2000个文件，为了统计词频使用hash
     *
     * 算法实现：先单词hash之后划分到文件，之后每个文件单独进行词频统计，并且完成排序。
     *          之后直接2000个文件第一行进行读取，最后用置换算法读入心如据。
     *
     * 问题：  会出现分区不均，极致情况下还是容易爆掉内存
     **/
    private void distributeWordsToDifferentFileByHash() throws IOException {
        if (!outputDir.exists ()){
            outputDir.mkdirs();
        }

        FileReader reader = null;
        FileWriter writer = null;
        try {
            reader = new FileReader (input);
            char[] buff = new char[8];
            int len;
            while ((len = reader.read (buff)) != -1) {
                String outputFileName = String.valueOf (hashCode (buff));
                if (outputDir != null) {
                    File outputFile = new File(outputDir, outputFileName);
                    if (!outputFile.exists ()){
                        outputFile.createNewFile();
                    }
                    writer = new FileWriter(outputFile);
                    writer.write(buff, 0, len);
                }
            }
        } finally {
            if (reader != null) {
                reader.close ();
            }
            if (writer != null){
                writer.close ();
            }
        }
    }

    private void countWordsForDifferentFiles(File[] filesToBeMerges){
    }

    private String[] mergeFiles(File[] filesToBeMerges) {
        return new String[0];
    }

    private int hashCode(char[] word) {
        return Arrays.hashCode (word) % HASH_LENGTH;
    }
}
