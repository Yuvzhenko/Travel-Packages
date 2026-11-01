package travel.model;

import java.util.List;

/** Результат розбору XML-файлу: заголовок, агентства та путівки (корінь {@code tourist_voucher}). */
public record VoucherCatalog(String title, List<Agency> agencies, List<TouristVoucher> vouchers) { }
