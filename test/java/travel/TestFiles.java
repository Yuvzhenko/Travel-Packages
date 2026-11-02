package travel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Допоміжні методи для тестів: шляхи до файлів і створення змінених копій XML. */
public final class TestFiles {

    public static final Path SAMPLE = Path.of("src/test/resources/sample.xml");
    public static final Path XSD = Path.of("data/Travel.xsd");
    public static final Path XSL = Path.of("data/group-by-type.xsl");
    public static final Path REAL_XML = Path.of("data/Travel.xml");

    private TestFiles() { }

    /** Вміст sample.xml із заміною одного фрагмента (для створення некоректних документів). */
    public static String sampleWith(String target, String replacement) throws IOException {
        String xml = Files.readString(SAMPLE, StandardCharsets.UTF_8);
        if (!xml.contains(target)) {
            throw new IllegalArgumentException("Fragment not found in sample.xml: " + target);
        }
        return xml.replace(target, replacement);
    }

    public static InputStream stream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    public static Path tempFile(String content) throws IOException {
        Path file = Files.createTempFile("travel-test", ".xml");
        file.toFile().deleteOnExit();
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
