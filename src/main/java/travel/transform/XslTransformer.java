package travel.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Застосовує XSL-таблицю стилів до XML-файлу та записує результат у файл
public class XslTransformer {

    private static final Logger LOG = Logger.getLogger(XslTransformer.class.getName());

    public void transform(Path xml, Path xsl, Path output) throws IOException, TransformerException {
        LOG.fine(() -> "Transforming " + xml + " with " + xsl);
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        Transformer transformer = factory.newTransformer(new StreamSource(xsl.toFile()));

        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (OutputStream out = Files.newOutputStream(output)) {
            transformer.transform(new StreamSource(xml.toFile()), new StreamResult(out));
        }
        LOG.info(() -> "Transformation result written to " + output);
    }
}
