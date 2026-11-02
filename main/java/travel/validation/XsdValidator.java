package travel.validation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Перевіряє XML-документ на відповідність XSD-схемі. */
public class XsdValidator {

    private static final Logger LOG = Logger.getLogger(XsdValidator.class.getName());

    /**
     * @return список повідомлень про помилки; порожній список означає, що документ валідний
     * @throws SAXException якщо саму схему не вдалося завантажити
     */
    public List<String> validate(Path xml, Path xsd) throws IOException, SAXException {
        LOG.fine(() -> "Validating " + xml + " against " + xsd);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Schema schema = factory.newSchema(xsd.toFile());

        List<String> errors = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new CollectingErrorHandler(errors));

        try (InputStream in = Files.newInputStream(xml)) {
            validator.validate(new StreamSource(in));
        } catch (SAXParseException e) {
            // фатальна помилка (документ не well-formed): розбір далі неможливий
            errors.add(format(e));
        }
        return errors;
    }

    private static String format(SAXParseException e) {
        return "line " + e.getLineNumber() + ", column " + e.getColumnNumber() + ": " + e.getMessage();
    }

    /** Збирає помилки валідації, не зупиняючи перевірку на першій з них. */
    private static final class CollectingErrorHandler implements ErrorHandler {
        private final List<String> errors;

        CollectingErrorHandler(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void warning(SAXParseException e) {
            LOG.warning(format(e));
        }

        @Override
        public void error(SAXParseException e) {
            errors.add(format(e));
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    }
}
