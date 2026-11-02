package travel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

class VoucherAssemblerTest {

    @Test
    void buildsCatalogFromEvents() {
        VoucherAssembler assembler = new VoucherAssembler();
        assembler.endElement("title", "Demo");

        assembler.startElement("agency", Map.of("agency_id", "ag3"));
        assembler.endElement("agency", "Gamma");

        assembler.startElement("package", Map.of("package_id", "pkg9", "agency_ref", "ag3"));
        assembler.endElement("type", "cruise");
        assembler.endElement("country", "Norway");
        assembler.endElement("days", "9");
        assembler.endElement("nights", "8");
        assembler.endElement("duration", "");
        assembler.endElement("transport", "SHIP");
        assembler.startElement("cost", Map.of("currency", "usd"));
        assembler.endElement("amount", "100.25");
        assembler.endElement("item", "meals");
        assembler.endElement("item", "excursions");
        assembler.endElement("cost", "");
        assembler.endElement("stars", "5");
        assembler.endElement("meal", "fb");
        assembler.endElement("room_capacity", "3");
        assembler.endElement("tv", "1");
        assembler.endElement("air_conditioning", "false");
        assembler.endElement("hotel", "");
        assembler.endElement("package", "");

        VoucherCatalog catalog = assembler.catalog();

        assertEquals("Demo", catalog.title());
        assertEquals(List.of(new Agency("ag3", "Gamma")), catalog.agencies());
        assertEquals(List.of(new TouristVoucher("pkg9", "ag3", VoucherType.CRUISE, "Norway",
                new Duration(9, 8), Transport.SHIP,
                new Cost(new BigDecimal("100.25"), CurrencyCode.USD, List.of("meals", "excursions")),
                new Hotel(5, Meal.FB, 3, true, false))), catalog.vouchers());
    }

    @Test
    void itemsDoNotLeakBetweenVouchers() {
        VoucherAssembler assembler = new VoucherAssembler();
        for (String item : List.of("first", "second")) {
            assembler.startElement("cost", Map.of("currency", "EUR"));
            assembler.endElement("amount", "1");
            assembler.endElement("item", item);
            assembler.endElement("cost", "");
        }
        // друга вартість містить лише власний елемент
        assembler.endElement("days", "1");
        assembler.endElement("nights", "0");
        assembler.endElement("duration", "");
        assembler.endElement("package", "");
        assertEquals(List.of("second"), assembler.catalog().vouchers().get(0).cost().included());
    }

    @Test
    void unknownCurrencyFails() {
        VoucherAssembler assembler = new VoucherAssembler();
        assertThrows(IllegalArgumentException.class,
                () -> assembler.startElement("cost", Map.of("currency", "XXX")));
    }
}
