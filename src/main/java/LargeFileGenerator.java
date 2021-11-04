import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Random;

public class LargeFileGenerator {

    public static final String TARGET_FILE_PATH = System.getProperty("lfg.target.file.path", "largeFile.txt");
    public static final int TARGET_STRING_LENGTH = Integer.parseInt(System.getProperty("lfg.target.string.length", "1024"));
    public static final int LINES_COUNT = Integer.parseInt(System.getProperty("lfg.lines.count", "1024"));

    public static int LINE_NUM_MARKER = Integer.parseInt(System.getProperty("lfg.line.num.marker", "10")); // marker every 10 lines. 0 to disable
    public static int COLUMN_NUM_MARKER = Integer.parseInt(System.getProperty("lfg.column.num.marker", "50")); // marker every 50 columns. 0 to disable

    public static final boolean RANDOMIZE_LINE_LENGTH = Boolean.parseBoolean(System.getProperty("lfg.randomize.line.length", "false"));
    public static final int ENSURE_MAX_LINE_LENGTH_EVERY_NTH_LINE = Integer.parseInt(System.getProperty("lfg.ensure.max.line.length.every.nth.line", "31")); // used, when RANDOMIZE_LINE_LENGTH == true.

    public static void main(String[] args) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TARGET_FILE_PATH))) {
            for (int lineNum = 0; lineNum < LINES_COUNT; ++lineNum) {
                final String line;
                if (LINE_NUM_MARKER != 0 && lineNum % LINE_NUM_MARKER == 0) {
                    line = "" + lineNum + ": =====================================\n";
                } else {
                    final int targetLineLength;
                    if (!RANDOMIZE_LINE_LENGTH || (ENSURE_MAX_LINE_LENGTH_EVERY_NTH_LINE != 0 && lineNum % ENSURE_MAX_LINE_LENGTH_EVERY_NTH_LINE == 0)) {
                        targetLineLength = TARGET_STRING_LENGTH;
                    } else {
                        targetLineLength = new Random().nextInt(TARGET_STRING_LENGTH + 1);
                    }
                    line = randomString2(targetLineLength, COLUMN_NUM_MARKER) + "\n";
                }
                writer.write(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String randomString(final int targetStringLength, final int columnNumMarker) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomString2(final int targetStringLength, final int columnNumMarker) {
        final int leftLimit = 32; // range of printable characters
        final int rightLimit = 126;
        final Random random = new Random();

        final StringBuilder stringBuilder = new StringBuilder();
        for (int lineLength = 1; lineLength <= targetStringLength; ++lineLength) {
            final int codePoint = random.nextInt(rightLimit + 1 - leftLimit) + leftLimit;
            stringBuilder.appendCodePoint(codePoint);
            if (COLUMN_NUM_MARKER > 0 && (lineLength % columnNumMarker == 0)) {
                final String columnNumMarkerString = "|Column: " + lineLength + " >>>";
                if (columnNumMarkerString.length() < columnNumMarker) {
                    stringBuilder.replace(stringBuilder.length() - columnNumMarkerString.length(), stringBuilder.length(), columnNumMarkerString);
                } else {
                    System.err.println("Cannot put column marker. It is too long (cannot fit before the next one).");
                }
            }
        }
        return stringBuilder.toString();
    }

}
