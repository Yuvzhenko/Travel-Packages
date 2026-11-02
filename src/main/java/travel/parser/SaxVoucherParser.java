package travel.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/** Розбір за допомогою SAX. */
public class SaxVoucherParser extends AbstractVoucherParser {

    @Override
    protected void process(InputStream in, VoucherEventHandler handler) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.newSAXParser().parse(in, new Adapter(handler));
    }

    /** Перетворює SAX-колбеки на події {@link VoucherEventHandler}. */
    private static final class Adapter extends DefaultHandler {
        private final VoucherEventHandler handler;
        private final StringBuilder text = new StringBuilder();

        Adapter(VoucherEventHandler handler) {
            this.handler = handler;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) {
            text.setLength(0);
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < attrs.getLength(); i++) {
                map.put(attrs.getQName(i), attrs.getValue(i));
            }
            handler.startElement(qName, map);
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            text.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            handler.endElement(qName, text.toString().trim());
            text.setLength(0);
        }
    }
}
