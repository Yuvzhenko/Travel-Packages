package travel.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// Розбір за допомогою DOM: будується дерево, потім обходиться рекурсивно.
public class DomVoucherParser extends AbstractVoucherParser {

    @Override
    protected void process(InputStream in, VoucherEventHandler handler) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document document = factory.newDocumentBuilder().parse(in);
        walk(document.getDocumentElement(), handler);
    }

    private void walk(Element element, VoucherEventHandler handler) {
        handler.startElement(element.getTagName(), attributes(element));
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element childElement) {
                walk(childElement, handler);
            }
        }
        handler.endElement(element.getTagName(), element.getTextContent().trim());
    }

    private static Map<String, String> attributes(Element element) {
        NamedNodeMap nodes = element.getAttributes();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            map.put(nodes.item(i).getNodeName(), nodes.item(i).getNodeValue());
        }
        return map;
    }
}
