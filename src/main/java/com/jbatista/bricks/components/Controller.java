package com.jbatista.bricks.components;

import com.jbatista.bricks.util.MathFunctions;

import java.util.Arrays;
import java.util.function.Consumer;

public class Controller {

    public enum Curve {ORIGINAL, LINEAR, EXPONENTIAL}

    private final String name;
    private final String description;

    private double value;
    private double displayValue;
    private final double min;
    private final double max;
    private final int[] validValues;
    private final Curve curve;
    private final Consumer<Double> callback;

    public Controller(String name, String description, double min, double max, double value, Curve curve, Consumer<Double> callback, int... validValues) {
        this.name = name;
        this.description = description;
        this.min = min;
        this.max = max;
        this.validValues = validValues;
        this.curve = curve;
        this.callback = callback;
        Arrays.sort(this.validValues);

        setValue(value);
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
            if (curve == Curve.ORIGINAL) {
                this.value = Math.max(min, Math.min(value, max));
            } else {
                this.value = Math.max(0, Math.min(value, 1));
            }
        }

        switch (curve) {
            case ORIGINAL:
                displayValue = this.value;
                break;

            case LINEAR:
                displayValue = MathFunctions.linearInterpolation(min, max, value);
                break;

            case EXPONENTIAL:
                displayValue = MathFunctions.expIncreaseInterpolation(min, max, value, 5);
                break;
        }

        callback.accept(displayValue);
    }

    public double getDisplayValue() {
        return displayValue;
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

    public Curve getCurve() {
        return curve;
    }

}
