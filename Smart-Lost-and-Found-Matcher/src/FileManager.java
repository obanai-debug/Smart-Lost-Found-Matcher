import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FileManager {

    private static final Path DATA_DIR = Paths.get("data");

    private static final Path LOST_FILE =
            DATA_DIR.resolve("lost_items.txt");

    private static final Path FOUND_FILE =
            DATA_DIR.resolve("found_items.txt");

    private static void createStorage() throws IOException {
        Files.createDirectories(DATA_DIR);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(
                value.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    public static void saveLostItems(List<LostItem> items)
            throws IOException {

        createStorage();

        List<String> lines = new ArrayList<>();

        for (LostItem item : items) {
            lines.add(
                    encode(item.getName()) + "," +
                    encode(item.getCategory()) + "," +
                    encode(item.getLocation()) + "," +
                    encode(item.getDescription())
            );
        }

        Files.write(LOST_FILE, lines, StandardCharsets.UTF_8);
    }

    public static void saveFoundItems(List<FoundItem> items)
            throws IOException {

        createStorage();

        List<String> lines = new ArrayList<>();

        for (FoundItem item : items) {
            lines.add(
                    encode(item.getName()) + "," +
                    encode(item.getCategory()) + "," +
                    encode(item.getLocation()) + "," +
                    encode(item.getDescription())
            );
        }

        Files.write(FOUND_FILE, lines, StandardCharsets.UTF_8);
    }

    public static List<LostItem> loadLostItems()
            throws IOException {

        createStorage();

        List<LostItem> items = new ArrayList<>();

        if (!Files.exists(LOST_FILE)) {
            return items;
        }

        for (String line :
                Files.readAllLines(LOST_FILE, StandardCharsets.UTF_8)) {

            String[] data = line.split(",", -1);

            if (data.length == 4) {
                try {
                    items.add(new LostItem(
                            decode(data[0]),
                            decode(data[1]),
                            decode(data[2]),
                            decode(data[3])
                    ));
                } catch (IllegalArgumentException ex) {
                    System.err.println("Skipping invalid lost report.");
                }
            }
        }

        return items;
    }

    public static List<FoundItem> loadFoundItems()
            throws IOException {

        createStorage();

        List<FoundItem> items = new ArrayList<>();

        if (!Files.exists(FOUND_FILE)) {
            return items;
        }

        for (String line :
                Files.readAllLines(FOUND_FILE, StandardCharsets.UTF_8)) {

            String[] data = line.split(",", -1);

            if (data.length == 4) {
                try {
                    items.add(new FoundItem(
                            decode(data[0]),
                            decode(data[1]),
                            decode(data[2]),
                            decode(data[3])
                    ));
                } catch (IllegalArgumentException ex) {
                    System.err.println("Skipping invalid found report.");
                }
            }
        }

        return items;
    }
}