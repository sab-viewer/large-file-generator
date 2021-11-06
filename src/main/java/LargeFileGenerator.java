import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Random;

public class LargeFileGenerator {

    private static final String TARGET_FILE_PATH = System.getProperty("lfg.target.file.path", "largeFile.txt");
    private static final int TARGET_STRING_LENGTH = Integer.parseInt(System.getProperty("lfg.target.string.length", "1024"));
    private static final int LINES_COUNT = Integer.parseInt(System.getProperty("lfg.lines.count", "102400"));

    private static final int LINE_NUM_MARKER = Integer.parseInt(System.getProperty("lfg.line.num.marker", "10")); // marker every 10 lines. 0 to disable
    private static final int COLUMN_NUM_MARKER = Integer.parseInt(System.getProperty("lfg.column.num.marker", "50")); // marker every 50 columns. 0 to disable

    private static final boolean RANDOMIZE_LINE_LENGTH = Boolean.parseBoolean(System.getProperty("lfg.randomize.line.length", "false"));
    private static final int ENSURE_MAX_LINE_LENGTH_EVERY_NTH_LINE = Integer.parseInt(System.getProperty("lfg.ensure.max.line.length.every.nth.line", "31")); // used, when RANDOMIZE_LINE_LENGTH == true.

    private static final boolean ADD_NON_ASCII_CHARACTERS = Boolean.parseBoolean(System.getProperty("lfg.add.non.ascii.characters", "false"));

    private static final char[] ALPHABET;
    static {
        String stringOfCharacters = "abcdefghijklmnopqrstuvwxyz`1234567890-=[]\\;',./ ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+{}|:\"<>?";
        if (ADD_NON_ASCII_CHARACTERS) {
            stringOfCharacters += "äåčćèəéêēﬀößüŭ§×÷°ÄÖÜ";
        }
        ALPHABET = stringOfCharacters.toCharArray();
    }

    public static void main(String[] args) {
        final Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TARGET_FILE_PATH))) {
            for (int lineNum = 1; lineNum <= LINES_COUNT; ++lineNum) {
                final String line;
                if (LINE_NUM_MARKER != 0 && lineNum % LINE_NUM_MARKER == 0) {
                    line = "" + lineNum + ": =====================================\n";
                } else {
                    final int targetLineLength;
                    if (!RANDOMIZE_LINE_LENGTH || (ENSURE_MAX_LINE_LENGTH_EVERY_NTH_LINE != 0 && lineNum % ENSURE_MAX_LINE_LENGTH_EVERY_NTH_LINE == 0)) {
                        targetLineLength = TARGET_STRING_LENGTH;
                    } else {
                        targetLineLength = random.nextInt(TARGET_STRING_LENGTH + 1);
                    }
                    line = randomString(random, targetLineLength, COLUMN_NUM_MARKER) + "\n";
                }
                writer.write(line);
                if (lineNum > 0 && lineNum % 25000 == 0) {
                    System.out.println("Wrote " + lineNum + " lines");
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String randomString(final Random random, final int targetStringLength, final int columnNumMarker) {

        final StringBuilder stringBuilder = new StringBuilder();
        for (int lineLength = 1; lineLength <= targetStringLength; ++lineLength) {
            final char character = ALPHABET[random.nextInt(ALPHABET.length)];
            stringBuilder.append(character);
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
