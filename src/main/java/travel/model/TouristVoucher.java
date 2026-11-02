package travel.model;

/** Туристична путівка (елемент {@code <package>}). */
public record TouristVoucher(
        String id,
        String agencyId,
        VoucherType type,
        String country,
        Duration duration,
        Transport transport,
        Cost cost,
        Hotel hotel) { }
