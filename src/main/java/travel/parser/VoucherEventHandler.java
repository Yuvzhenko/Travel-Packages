package travel.parser;

import java.util.Map;

public interface VoucherEventHandler {

    void startElement(String name, Map<String, String> attributes);

    void endElement(String name, String text);
}
