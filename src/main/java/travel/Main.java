package travel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import travel.model.TouristVoucher;
import travel.model.VoucherCatalog;
import travel.model.VoucherComparators;
import travel.parser.DomVoucherParser;
import travel.parser.SaxVoucherParser;
import travel.parser.StaxVoucherParser;
import travel.parser.VoucherParseException;
import travel.parser.VoucherParser;

/** Розбирає Travel.xml трьома парсерами, порівнює результати та виводить відсортовані колекції. */
public final class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private Main() { }

    public static void main(String[] args) {
        configureLogging();
        Path file = Path.of(args.length > 0 ? args[0] : "data/Travel.xml");

        Map<String, VoucherParser> parsers = new LinkedHashMap<>();
        parsers.put("SAX", new SaxVoucherParser());
        parsers.put("DOM", new DomVoucherParser());
        parsers.put("StAX", new StaxVoucherParser());

        VoucherCatalog reference = null;
        for (Map.Entry<String, VoucherParser> entry : parsers.entrySet()) {
            try {
                LOG.log(Level.INFO, "Parsing {0} with {1}", new Object[] {file, entry.getKey()});
                VoucherCatalog catalog = entry.getValue().parse(file);
                if (reference == null) {
                    reference = catalog;
                } else if (reference.equals(catalog)) {
                    LOG.log(Level.INFO, "{0} result equals the first result", entry.getKey());
                } else {
                    LOG.log(Level.WARNING, "{0} result differs from the first result", entry.getKey());
                }
            } catch (VoucherParseException e) {
                LOG.log(Level.SEVERE, "Cannot parse " + file + " with " + entry.getKey(), e);
                return;
            }
        }

        print("За вартістю (зростання)", reference.vouchers(), VoucherComparators.BY_COST);
        print("За країною, потім за вартістю",
                reference.vouchers(), VoucherComparators.BY_COUNTRY.thenComparing(VoucherComparators.BY_COST));
        print("За зірками готелю (спадання), потім за тривалістю",
                reference.vouchers(), VoucherComparators.BY_STARS_DESC.thenComparing(VoucherComparators.BY_DAYS));
    }

    private static void print(String title, List<TouristVoucher> vouchers, Comparator<TouristVoucher> order) {
        List<TouristVoucher> sorted = new ArrayList<>(vouchers);
        sorted.sort(order);
        System.out.println("\n== " + title + " ==");
        for (TouristVoucher v : sorted) {
            System.out.printf("%-6s %-10s %-10s %2d d/%2d n  %-6s %2d*  %-2s  %8s %s%n",
                    v.id(), v.type(), v.country(),
                    v.duration().days(), v.duration().nights(),
                    v.transport(), v.hotel().stars(), v.hotel().meal(),
                    v.cost().amount(), v.cost().currency());
        }
    }

    private static void configureLogging() {
        try (InputStream in = Main.class.getResourceAsStream("/logging.properties")) {
            if (in != null) {
                LogManager.getLogManager().readConfiguration(in);
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Cannot load logging.properties, using defaults", e);
        }
    }
}
