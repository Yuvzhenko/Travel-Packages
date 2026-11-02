package travel.model;

public record TouristVoucher(
        String id,
        String agencyId,
        VoucherType type,
        String country,
        Duration duration,
        Transport transport,
        Cost cost,
        Hotel hotel) { }
