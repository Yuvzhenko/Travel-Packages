package travel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static travel.TestFiles.REAL_XML;

import org.junit.jupiter.api.Test;

/** SAX, DOM і StAX мають давати однаковий результат на реальному файлі. */
class ParsersAgreementTest {

    @Test
    void allParsersReturnEqualCatalogs() throws Exception {
        var sax = new SaxVoucherParser().parse(REAL_XML);
        assertEquals(sax, new DomVoucherParser().parse(REAL_XML));
        assertEquals(sax, new StaxVoucherParser().parse(REAL_XML));
        assertEquals(11, sax.vouchers().size());
    }
}
