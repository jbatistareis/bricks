package com.jbatista.bricks.components;

import java.util.Arrays;
import java.util.function.Consumer;

public class Controler {

    private final String name;
    private final String description;

    double value = 0;
    private final double min = 0;
    private final double max = 1;
    private final int[] validValues;
    private final Consumer<Double> bindMethod;

    public Controler(String name, String description, Consumer<Double> bindMethod, int... validValues) {
        this.name = name;
        this.description = description;
        this.validValues = validValues;
        this.bindMethod = bindMethod;
        Arrays.sort(this.validValues);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if ((validValues.length != 0) && (Arrays.binarySearch(validValues, (int) value) >= 0)) {
            this.value = value;
        } else {
            this.value = Math.max(min, Math.min(value, max));
        }

        bindMethod.accept(this.value);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int[] getValidValues() {
        return validValues;
    }

}
