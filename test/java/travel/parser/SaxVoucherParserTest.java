package travel.parser;

class SaxVoucherParserTest extends AbstractVoucherParserTest {

    @Override
    protected VoucherParser parser() {
        return new SaxVoucherParser();
    }
}
