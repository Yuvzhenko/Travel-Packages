package travel.parser;

class StaxVoucherParserTest extends AbstractVoucherParserTest {

    @Override
    protected VoucherParser parser() {
        return new StaxVoucherParser();
    }
}
