package travel.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class VoucherComparatorsTest {

    private final TouristVoucher a = voucher("a", "Spain", 10, 5, "1500");
    private final TouristVoucher b = voucher("b", "Austria", 7, 3, "900");
    private final TouristVoucher c = voucher("c", "Spain", 3, 5, "700");

    @Test
    void sortsByCostAscending() {
        assertEquals(List.of("c", "b", "a"), ids(VoucherComparators.BY_COST));
    }

    @Test
    void sortsByDays() {
        assertEquals(List.of("c", "b", "a"), ids(VoucherComparators.BY_DAYS));
    }

    @Test
    void sortsByCountry() {
        assertEquals("b", ids(VoucherComparators.BY_COUNTRY).get(0));
    }

    @Test
    void sortsByStarsDescendingThenDays() {
        assertEquals(List.of("c", "a", "b"),
                ids(VoucherComparators.BY_STARS_DESC.thenComparing(VoucherComparators.BY_DAYS)));
    }

    @Test
    void combinesComparators() {
        assertEquals(List.of("b", "c", "a"),
                ids(VoucherComparators.BY_COUNTRY.thenComparing(VoucherComparators.BY_COST)));
    }

    private List<String> ids(Comparator<TouristVoucher> order) {
        List<TouristVoucher> list = new ArrayList<>(List.of(a, b, c));
        list.sort(order);
        return list.stream().map(TouristVoucher::id).toList();
    }

    private static TouristVoucher voucher(String id, String country, int days, int stars, String amount) {
        return new TouristVoucher(id, "ag1", VoucherType.HOLIDAY, country,
                new Duration(days, days - 1), Transport.PLANE,
                new Cost(new BigDecimal(amount), CurrencyCode.EUR, List.of("flight")),
                new Hotel(stars, Meal.BB, 2, true, true));
    }
}
