package travel.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import travel.model.VoucherCatalog;

/** Розбирає XML з путівками в колекцію об'єктів. */
public interface VoucherParser {

    VoucherCatalog parse(InputStream in) throws VoucherParseException;

    default VoucherCatalog parse(Path file) throws VoucherParseException {
        try (InputStream in = Files.newInputStream(file)) {
            return parse(in);
        } catch (IOException e) {
            throw new VoucherParseException("Cannot read file " + file, e);
        }
    }
}
