package travel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import travel.model.TouristVoucher;
import travel.model.VoucherCatalog;
import travel.model.VoucherComparators;
import travel.parser.DomVoucherParser;
import travel.parser.SaxVoucherParser;
import travel.parser.StaxVoucherParser;
import travel.parser.VoucherParseException;
import travel.parser.VoucherParser;
import travel.transform.XslTransformer;
import travel.validation.XsdValidator;

public final class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private Main() { }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        configureLogging();

        Path xml = path(args, 0, "XML/Travel.xml");
        Path xsd = path(args, 1, "XML/Travel.xsd");
        Path xsl = path(args, 2, "XML/group-by-type.xsl");
        Path output = path(args, 3, "output/Travel_by_type.xml");

        if (!validate(xml, xsd)) {
            return;
        }
        VoucherCatalog catalog = parseAll(xml);
        if (catalog == null) {
            return;
        }
        print("За вартістю (зростання)", catalog.vouchers(), VoucherComparators.BY_COST);
        print("За країною, потім за вартістю",
                catalog.vouchers(), VoucherComparators.BY_COUNTRY.thenComparing(VoucherComparators.BY_COST));
        print("За зірками готелю (спадання), потім за тривалістю",
                catalog.vouchers(), VoucherComparators.BY_STARS_DESC.thenComparing(VoucherComparators.BY_DAYS));

        transform(xml, xsl, output);
    }

    private static boolean validate(Path xml, Path xsd) {
        try {
            List<String> errors = new XsdValidator().validate(xml, xsd);
            if (errors.isEmpty()) {
                LOG.log(Level.INFO, "{0} is valid against {1}", new Object[] {xml, xsd});
                return true;
            }
            LOG.log(Level.SEVERE, "{0} is NOT valid, {1} error(s):", new Object[] {xml, errors.size()});
            errors.forEach(error -> LOG.severe("  " + error));
        } catch (IOException | SAXException e) {
            LOG.log(Level.SEVERE, "Validation could not be performed", e);
        }
        return false;
    }

    private static VoucherCatalog parseAll(Path xml) {
        Map<String, VoucherParser> parsers = new LinkedHashMap<>();
        parsers.put("SAX", new SaxVoucherParser());
        parsers.put("DOM", new DomVoucherParser());
        parsers.put("StAX", new StaxVoucherParser());

        VoucherCatalog reference = null;
        for (Map.Entry<String, VoucherParser> entry : parsers.entrySet()) {
            try {
                LOG.log(Level.INFO, "Parsing {0} with {1}", new Object[] {xml, entry.getKey()});
                VoucherCatalog catalog = entry.getValue().parse(xml);
                if (reference == null) {
                    reference = catalog;
                } else if (reference.equals(catalog)) {
                    LOG.log(Level.INFO, "{0} result equals the first result", entry.getKey());
                } else {
                    LOG.log(Level.WARNING, "{0} result differs from the first result", entry.getKey());
                }
            } catch (VoucherParseException e) {
                LOG.log(Level.SEVERE, "Cannot parse " + xml + " with " + entry.getKey(), e);
                return null;
            }
        }
        return reference;
    }

    private static void transform(Path xml, Path xsl, Path output) {
        try {
            new XslTransformer().transform(xml, xsl, output);
        } catch (IOException | TransformerException e) {
            LOG.log(Level.SEVERE, "XSL transformation failed", e);
        }
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

    private static Path path(String[] args, int index, String defaultValue) {
        return Path.of(args.length > index ? args[index] : defaultValue);
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
