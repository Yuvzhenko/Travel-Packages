package travel.model;

/** Характеристика готелю: зірки, харчування, місткість номера, TV, кондиціонер. */
public record Hotel(int stars, Meal meal, int roomCapacity, boolean tv, boolean airConditioning) { }
