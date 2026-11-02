package travel.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import travel.util.EnumParser;

public class VoucherAssembler implements VoucherEventHandler {

    private String title;
    private final List<Agency> agencies = new ArrayList<>();
    private final List<TouristVoucher> vouchers = new ArrayList<>();

    // поточна агенція
    private String agencyId;

    // поточна путівка
    private String packageId;
    private String agencyRef;
    private VoucherType type;
    private String country;
    private Duration duration;
    private Transport transport;
    private Cost cost;
    private Hotel hotel;

    // проміжні значення вкладених елементів
    private Integer days;
    private Integer nights;
    private BigDecimal amount;
    private CurrencyCode currency;
    private List<String> included;
    private Integer stars;
    private Meal meal;
    private Integer roomCapacity;
    private Boolean tv;
    private Boolean airConditioning;

    @Override
    public void startElement(String name, Map<String, String> attributes) {
        switch (name) {
            case "agency" -> agencyId = attributes.get("agency_id");
            case "package" -> {
                packageId = attributes.get("package_id");
                agencyRef = attributes.get("agency_ref");
            }
            case "cost" -> {
                currency = EnumParser.parse(CurrencyCode.class, attributes.get("currency"));
                included = new ArrayList<>();
            }
            default -> { }
        }
    }

    @Override
    public void endElement(String name, String text) {
        switch (name) {
            case "title" -> title = text;
            case "agency" -> agencies.add(new Agency(agencyId, text));

            case "type" -> type = EnumParser.parse(VoucherType.class, text);
            case "country" -> country = text;
            case "transport" -> transport = EnumParser.parse(Transport.class, text);

            case "days" -> days = Integer.valueOf(text);
            case "nights" -> nights = Integer.valueOf(text);
            case "duration" -> duration = new Duration(days, nights);

            case "amount" -> amount = new BigDecimal(text);
            case "item" -> included.add(text);
            case "cost" -> cost = new Cost(amount, currency, List.copyOf(included));

            case "stars" -> stars = Integer.valueOf(text);
            case "meal" -> meal = EnumParser.parse(Meal.class, text);
            case "room_capacity" -> roomCapacity = Integer.valueOf(text);
            case "tv" -> tv = parseBoolean(text);
            case "air_conditioning" -> airConditioning = parseBoolean(text);
            case "hotel" -> hotel = new Hotel(stars, meal, roomCapacity, tv, airConditioning);

            case "package" -> vouchers.add(
                    new TouristVoucher(packageId, agencyRef, type, country, duration, transport, cost, hotel));
            default -> { }
        }
    }

    public VoucherCatalog catalog() {
        return new VoucherCatalog(title, List.copyOf(agencies), List.copyOf(vouchers));
    }

    private static boolean parseBoolean(String text) {
        return "true".equals(text) || "1".equals(text);
    }
}
