package travel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static travel.TestFiles.SAMPLE;
import static travel.TestFiles.sampleWith;
import static travel.TestFiles.stream;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import travel.model.Agency;
import travel.model.Cost;
import travel.model.CurrencyCode;
import travel.model.Duration;
import travel.model.Hotel;
import travel.model.Meal;
import travel.model.TouristVoucher;
import travel.model.Transport;
import travel.model.VoucherCatalog;
import travel.model.VoucherType;

/** Спільні тести для всіх реалізацій парсера: підкласи лише повертають свій парсер. */
abstract class AbstractVoucherParserTest {

    protected abstract VoucherParser parser();

    @Test
    void parsesTitleAndAgencies() throws Exception {
        VoucherCatalog catalog = parser().parse(SAMPLE);
        assertEquals("Sample", catalog.title());
        assertEquals(List.of(new Agency("ag1", "Alpha Tours"), new Agency("ag2", "Beta Travel")),
                catalog.agencies());
    }

    @Test
    void parsesAllVouchers() throws Exception {
        assertEquals(3, parser().parse(SAMPLE).vouchers().size());
    }

    @Test
    void parsesVoucherFields() throws Exception {
        TouristVoucher expected = new TouristVoucher("pkg1", "ag1", VoucherType.HOLIDAY, "Greece",
                new Duration(8, 7), Transport.PLANE,
                new Cost(new BigDecimal("2100.50"), CurrencyCode.EUR, List.of("flight", "transfer")),
                new Hotel(4, Meal.AI, 2, true, false));
        assertEquals(expected, parser().parse(SAMPLE).vouchers().get(0));
    }

    @Test
    void unknownEnumValueFails() throws Exception {
        String xml = sampleWith("<transport>plane</transport>", "<transport>rocket</transport>");
        assertThrows(VoucherParseException.class, () -> parser().parse(stream(xml)));
    }

    @Test
    void malformedXmlFails() {
        assertThrows(VoucherParseException.class, () -> parser().parse(stream("<tourist_voucher><title>")));
    }

    @Test
    void missingFileFails() {
        assertThrows(VoucherParseException.class, () -> parser().parse(java.nio.file.Path.of("no-such-file.xml")));
    }
}
