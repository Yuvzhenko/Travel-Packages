package travel.model;

import java.math.BigDecimal;
import java.util.List;

/** Вартість путівки та перелік того, що в неї входить. */
public record Cost(BigDecimal amount, CurrencyCode currency, List<String> included) { }
