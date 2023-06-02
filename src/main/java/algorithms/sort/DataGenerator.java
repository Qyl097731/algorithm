package algorithms.sort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * 数据文件生成
 * @author qyl
 */
public class DataGenerator {
    public static void main(String[] args) throws IOException {
        final int count = 1024 * 1024 * 256;
        File f = new File ("./input.txt");
        if (f.exists ()) {
            f.delete ();
        }
        try (BufferedWriter writer = new BufferedWriter (new FileWriter (f))) {
            for (int i = 0; i < count; ++i) {
                writer.write (getRandomString ());
                writer.newLine ();
                if (i % 1024 == 0){
                    writer.flush ();
                }
            }
        }
    }

    public static String getRandomString() {
        StringBuilder sb = new StringBuilder ();
        Random random = new Random ();
        for (int i = 0; i < 8; i++) {
            sb.append ((char) (random.nextInt (26) + 97));
        }
        return sb.toString ();
    }
}
