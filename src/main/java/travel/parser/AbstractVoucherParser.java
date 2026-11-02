package travel.parser;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import travel.model.VoucherCatalog;

public abstract class AbstractVoucherParser implements VoucherParser {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Override
    public final VoucherCatalog parse(InputStream in) throws VoucherParseException {
        log.fine("Parsing started");
        VoucherAssembler assembler = new VoucherAssembler();
        try {
            process(in, assembler);
            VoucherCatalog catalog = assembler.catalog();
            log.log(Level.INFO, "Parsed {0} vouchers and {1} agencies",
                    new Object[] {catalog.vouchers().size(), catalog.agencies().size()});
            return catalog;
        } catch (Exception e) {
            throw new VoucherParseException(getClass().getSimpleName() + " failed: " + e.getMessage(), e);
        }
    }

    protected abstract void process(InputStream in, VoucherEventHandler handler) throws Exception;
}
