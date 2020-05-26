package com.jbatista.bricks.components;

import com.jbatista.bricks.util.MathFunctions;

public abstract class Connector {

    public enum Curve {LINEAR, SMOOTH, EXP_INCREASE, EXP_DECREASE}

    private final String name;
    private final String description;

    protected Patch inputPatch = null;
    protected Patch outputPatch = null;

    private double inputData = 0;
    private double outputData = 0;

    private double inputClip = 0;
    private double outputClip = 0;
    private double outputRatio = 1;

    private double outputScale = 0;
    private double outputScaleCenter = 0;
    private Curve scaleCurve = Curve.LINEAR;

    boolean connected = false;

    public Connector(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Patch getPatch() {
        return (inputPatch != null) ? inputPatch : outputPatch;
    }

    public abstract void connectPatch(Patch patch);

    public abstract void disconnectPatch();

    public void write(double data) {
        inputData = (inputClip == 0) ? data
                : (data > inputClip) ? inputClip
                : (data < -inputClip) ? -inputClip
                : data;

        if (inputPatch != null) {
            inputPatch.passData();
        }
    }

    public double read() {
        if (outputScale != 0) {
            switch (scaleCurve) {
                case LINEAR:
                    outputData = MathFunctions.linearInterpolation(0, outputScale, inputData);
                    break;

                case SMOOTH:
                    outputData = MathFunctions.smoothInterpolation(0, outputScale, inputData);
                    break;

                case EXP_DECREASE:
                    outputData = MathFunctions.expDecreaseInterpolation(0, outputScale, inputData);
                    break;

                case EXP_INCREASE:
                    outputData = MathFunctions.expIncreaseInterpolation(0, outputScale, inputData);
                    break;
            }
        } else {
            outputData = inputData;
        }

        return (((outputClip == 0) ? outputData
                : (outputData > outputClip) ? outputClip
                : (outputData < -outputClip) ? -outputClip
                : outputData)
                + outputScaleCenter)
                * outputRatio;
    }

    public double getInputClip() {
        return inputClip;
    }

    public void setInputClip(double inputClip) {
        this.inputClip = Math.max(0, Math.min(inputClip, 127));
    }

    public double getOutputClip() {
        return outputClip;
    }

    public void setOutputClip(double outputClip) {
        this.outputClip = Math.max(0, Math.min(outputClip, 127));
    }

    public double getOutputRatio() {
        return outputRatio;
    }

    public void setOutputRatio(double outputRatio) {
        this.outputRatio = Math.max(0, Math.min(outputRatio, 127));
    }

    public double getOutputScale() {
        return outputScale;
    }

    public void setOutputScale(double outputScale) {
        this.outputScale = Math.max(0, Math.min(outputScale, 127));
    }

    public double getOutputScaleCenter() {
        return outputScaleCenter;
    }

    public void setOutputScaleCenter(double outputScaleCenter) {
        this.outputScaleCenter = Math.max(0, Math.min(outputScaleCenter, 127));
    }

    public Curve getScaleCurve() {
        return scaleCurve;
    }

    public void setScaleCurve(Curve scaleCurve) {
        this.scaleCurve = scaleCurve;
    }

    public void setScaleCurve(int scaleCurve) {
        switch (scaleCurve) {
            case 1:
                this.scaleCurve = Curve.SMOOTH;
                break;

            case 2:
                this.scaleCurve = Curve.EXP_INCREASE;
                break;

            case 3:
                this.scaleCurve = Curve.EXP_DECREASE;
                break;

            default: // 0
                this.scaleCurve = Curve.LINEAR;
                break;
        }
    }

    public boolean isConnected() {
        return connected;
    }

}
