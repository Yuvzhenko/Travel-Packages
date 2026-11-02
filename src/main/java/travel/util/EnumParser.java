package travel.util;

// Перетворення текстових значень з XML на константи enum 
public final class EnumParser {

    private EnumParser() { }

    public static <E extends Enum<E>> E parse(Class<E> type, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing value for " + type.getSimpleName());
        }
        String v = value.trim();
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(v)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown " + type.getSimpleName() + ": '" + value + "'");
    }
}
