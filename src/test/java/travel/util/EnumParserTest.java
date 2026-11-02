package travel.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import travel.model.Meal;
import travel.model.Transport;

class EnumParserTest {

    @Test
    void ignoresCase() {
        assertEquals(Transport.PLANE, EnumParser.parse(Transport.class, "plane"));
        assertEquals(Meal.AI, EnumParser.parse(Meal.class, "Ai"));
    }

    @Test
    void trimsWhitespace() {
        assertEquals(Transport.TRAIN, EnumParser.parse(Transport.class, "  train \n"));
    }

    @Test
    void unknownValueFails() {
        assertThrows(IllegalArgumentException.class, () -> EnumParser.parse(Transport.class, "rocket"));
    }

    @Test
    void nullValueFails() {
        assertThrows(IllegalArgumentException.class, () -> EnumParser.parse(Meal.class, null));
    }
}
