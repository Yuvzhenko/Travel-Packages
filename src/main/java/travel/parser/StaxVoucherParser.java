package travel.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class StaxVoucherParser extends AbstractVoucherParser {

    @Override
    protected void process(InputStream in, VoucherEventHandler handler) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        XMLStreamReader reader = factory.createXMLStreamReader(in);
        try {
            StringBuilder text = new StringBuilder();
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        text.setLength(0);
                        handler.startElement(reader.getLocalName(), attributes(reader));
                    }
                    case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA -> text.append(reader.getText());
                    case XMLStreamConstants.END_ELEMENT -> {
                        handler.endElement(reader.getLocalName(), text.toString().trim());
                        text.setLength(0);
                    }
                    default -> { }
                }
            }
        } finally {
            reader.close();
        }
    }

    private static Map<String, String> attributes(XMLStreamReader reader) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            map.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
        }
        return map;
    }
}
