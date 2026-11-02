package travel.parser;

class DomVoucherParserTest extends AbstractVoucherParserTest {

    @Override
    protected VoucherParser parser() {
        return new DomVoucherParser();
    }
}
