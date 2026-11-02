package travel.model;

import java.util.Comparator;

public final class VoucherComparators {

    public static final Comparator<TouristVoucher> BY_COST =
            Comparator.comparing((TouristVoucher v) -> v.cost().amount());

    public static final Comparator<TouristVoucher> BY_DAYS =
            Comparator.comparingInt((TouristVoucher v) -> v.duration().days());

    public static final Comparator<TouristVoucher> BY_COUNTRY =
            Comparator.comparing(TouristVoucher::country);

    public static final Comparator<TouristVoucher> BY_STARS_DESC =
            Comparator.comparingInt((TouristVoucher v) -> v.hotel().stars()).reversed();

    private VoucherComparators() { }
}
