package travel.model;

import java.util.List;

public record VoucherCatalog(String title, List<Agency> agencies, List<TouristVoucher> vouchers) { }
