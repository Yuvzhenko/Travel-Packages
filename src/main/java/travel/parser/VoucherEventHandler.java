package travel.parser;

import java.util.Map;

/**
 * Єдиний набір подій, до яких зводяться SAX, StAX та DOM.
 * Це дозволяє не дублювати логіку побудови об'єктів у трьох парсерах.
 */
public interface VoucherEventHandler {

    void startElement(String name, Map<String, String> attributes);

    /** @param text текстовий вміст елемента (обрізаний); для контейнерних елементів не використовується */
    void endElement(String name, String text);
}
