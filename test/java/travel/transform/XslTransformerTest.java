package travel.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static travel.TestFiles.REAL_XML;
import static travel.TestFiles.SAMPLE;
import static travel.TestFiles.XSL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class XslTransformerTest {

    @Test
    void groupsPackagesByType() throws Exception {
        Element packages = child(transform(SAMPLE).getDocumentElement(), "packages");
        List<Element> groups = children(packages);

        assertEquals(List.of("holiday", "weekend"), groups.stream().map(Element::getTagName).toList());
        assertEquals(2, children(groups.get(0)).size());
        assertEquals(1, children(groups.get(1)).size());
    }

    @Test
    void keepsTitleAndAgencies() throws Exception {
        Element root = transform(SAMPLE).getDocumentElement();
        assertEquals("tourist_voucher_by_type", root.getTagName());
        assertEquals("Sample", child(root, "title").getTextContent());
        assertEquals(2, children(child(root, "traveling_agencies")).size());
    }

    @Test
    void everyPackageEndsUpInExactlyOneGroup() throws Exception {
        Element packages = child(transform(REAL_XML).getDocumentElement(), "packages");
        int total = 0;
        for (Element group : children(packages)) {
            for (Element pkg : children(group)) {
                assertEquals(group.getTagName(), child(pkg, "type").getTextContent());
                total++;
            }
        }
        assertEquals(11, total);
    }

    @Test
    void missingStylesheetFails() {
        assertThrows(TransformerException.class, () -> transformWith(SAMPLE, Path.of("no-such.xsl")));
    }

    private Document transform(Path xml) throws Exception {
        return transformWith(xml, XSL);
    }

    private Document transformWith(Path xml, Path xsl) throws IOException, TransformerException, Exception {
        Path output = Files.createTempFile("travel-by-type", ".xml");
        output.toFile().deleteOnExit();
        new XslTransformer().transform(xml, xsl, output);
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(output.toFile());
    }

    private static Element child(Element parent, String name) {
        return children(parent).stream().filter(e -> e.getTagName().equals(name)).findFirst().orElseThrow();
    }

    private static List<Element> children(Element parent) {
        List<Element> result = new ArrayList<>();
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element e) {
                result.add(e);
            }
        }
        return result;
    }
}
