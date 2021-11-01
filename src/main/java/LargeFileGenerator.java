import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Random;

public class LargeFileGenerator {
    public static void main(String[] args) {
        final String filePath = "C:\\d\\tmp\\largeFile.txt";
        final int linesCount = 20000000;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int lineNum = 0; lineNum < linesCount; ++lineNum) {
                final String line;
                if (lineNum % 10 == 0) {
                    line = "" + lineNum + ": =====================================\n";
                } else {
                    line = randomString(100) + "\n";
                }
                writer.write(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String randomString(final int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
