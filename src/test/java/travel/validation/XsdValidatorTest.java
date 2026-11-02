package travel.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static travel.TestFiles.REAL_XML;
import static travel.TestFiles.SAMPLE;
import static travel.TestFiles.XSD;
import static travel.TestFiles.sampleWith;
import static travel.TestFiles.tempFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class XsdValidatorTest {

    private final XsdValidator validator = new XsdValidator();

    @Test
    void sampleIsValid() throws Exception {
        assertEquals(List.of(), validator.validate(SAMPLE, XSD));
    }

    @Test
    void realDataIsValid() throws Exception {
        assertEquals(List.of(), validator.validate(REAL_XML, XSD));
    }

    @Test
    void roomCapacityAboveLimitIsRejected() throws Exception {
        assertInvalid(sampleWith("<room_capacity>2</room_capacity>", "<room_capacity>4</room_capacity>"),
                "room_capacity");
    }

    @Test
    void unknownEnumValueIsRejected() throws Exception {
        assertInvalid(sampleWith("<transport>plane</transport>", "<transport>rocket</transport>"), "transport");
    }

    @Test
    void danglingAgencyReferenceIsRejected() throws Exception {
        assertInvalid(sampleWith("agency_ref=\"ag2\"", "agency_ref=\"ag99\""), "ag99");
    }

    @Test
    void duplicateIdIsRejected() throws Exception {
        assertInvalid(sampleWith("package_id=\"pkg2\"", "package_id=\"pkg1\""), "pkg1");
    }

    @Test
    void missingRequiredAttributeIsRejected() throws Exception {
        assertInvalid(sampleWith("<cost currency=\"USD\">", "<cost>"), "currency");
    }

    @Test
    void notWellFormedXmlIsReported() throws Exception {
        List<String> errors = validator.validate(tempFile("<tourist_voucher><title>"), XSD);
        assertFalse(errors.isEmpty());
    }

    @Test
    void missingSchemaFails() {
        assertThrows(SAXException.class, () -> validator.validate(SAMPLE, Path.of("no-such.xsd")));
    }

    @Test
    void missingXmlFails() {
        assertThrows(IOException.class, () -> validator.validate(Path.of("no-such.xml"), XSD));
    }

    private void assertInvalid(String xml, String expectedFragment) throws Exception {
        List<String> errors = validator.validate(tempFile(xml), XSD);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(message -> message.contains(expectedFragment)),
                "No error mentions '" + expectedFragment + "': " + errors);
    }
}
