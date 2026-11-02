package travel.model;

import java.math.BigDecimal;
import java.util.List;

public record Cost(BigDecimal amount, CurrencyCode currency, List<String> included) { }
