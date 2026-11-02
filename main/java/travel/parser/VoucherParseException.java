package travel.parser;

/** Помилка розбору XML-документа. */
public class VoucherParseException extends Exception {

    private static final long serialVersionUID = 1L;

    public VoucherParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
