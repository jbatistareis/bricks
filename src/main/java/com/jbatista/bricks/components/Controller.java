package com.jbatista.bricks.components;

import com.jbatista.bricks.util.MathFunctions;

import java.util.Arrays;
import java.util.function.Consumer;

public class Controller {

    public enum Curve {ORIGINAL, LINEAR, EXP_INCREASE, EXP_DECREASE, SMOOTH}

    private final String name;
    private final String description;

    private double value;
    private double displayValue;
    private final double min;
    private final double max;
    private final double step;
    private final int[] validValues;
    private final Curve curve;
    private final Consumer<Double> callback;

    public Controller(String name, String description, double min, double max, double step, double value, Curve curve, Consumer<Double> callback, int... validValues) {
        this.name = name;
        this.description = description;
        this.min = min;
        this.max = max;
        this.step = step;
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

            case EXP_INCREASE:
                displayValue = MathFunctions.expIncreaseInterpolation(min, max, value, 3);
                break;

            case EXP_DECREASE:
                displayValue = MathFunctions.expDecreaseInterpolation(min, max, value, 3);
                break;

            case SMOOTH:
                displayValue = MathFunctions.smoothInterpolation(min, max, value);
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

    public double getStep() {
        return step;
    }

    public int[] getValidValues() {
        return validValues;
    }

    public Curve getCurve() {
        return curve;
    }

}
