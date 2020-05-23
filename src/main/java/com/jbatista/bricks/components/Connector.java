package com.jbatista.bricks.components;

import com.jbatista.bricks.util.MathFunctions;

public class Connector {

    public enum ScaleCurve {LINEAR, SMOOTH, EXP_DECREASE, EXP_INCREASE}

    private final String name;
    private final String description;

    private Patch outputPatch = null;

    private double input = 0;
    private double output = 0;

    private double inputClip = 0;
    private double outputClip = 0;
    private double outputRatio = 1;

    private double outputScale = 0;
    private boolean signedScaleOutput = true;
    private ScaleCurve scaleCurve = ScaleCurve.LINEAR;

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

    public Patch getOutputPatch() {
        return outputPatch;
    }

    public void setOutputPatch(Patch outputPatch) {
        this.outputPatch = outputPatch;
        this.outputPatch.setInputConnector(this);
    }

    public double getInputClip() {
        return inputClip;
    }

    public void setInputClip(double inputClip) {
        this.inputClip = Math.max(0, Math.min(inputClip, 64));
    }

    public double getOutputClip() {
        return outputClip;
    }

    public void setOutputClip(double outputClip) {
        this.outputClip = Math.max(0, Math.min(outputClip, 64));
    }

    public double getOutputRatio() {
        return outputRatio;
    }

    public void setOutputRatio(double outputRatio) {
        this.outputRatio = Math.max(0, Math.min(outputRatio, 64));
    }

    public double getOutputScale() {
        return outputScale;
    }

    public void setOutputScale(double outputScale) {
        this.outputScale = Math.max(-127, Math.min(outputScale, 127));
    }

    public boolean isSignedScaleOutput() {
        return signedScaleOutput;
    }

    public void setSignedScaleOutput(boolean signedScaleOutput) {
        this.signedScaleOutput = signedScaleOutput;
    }

    public ScaleCurve getScaleCurve() {
        return scaleCurve;
    }

    public void setScaleCurve(ScaleCurve scaleCurve) {
        this.scaleCurve = scaleCurve;
    }

    public void setInput(double input) {
        this.input = (inputClip == 0) ? input
                : (input > inputClip) ? inputClip
                : (input < -inputClip) ? -inputClip
                : input;

        output = this.input * outputRatio;

        if (outputScale != 0) {
            switch (scaleCurve) {
                case LINEAR:
                    output = MathFunctions.linearInterpolation(0, outputScale, output);
                    break;

                case SMOOTH:
                    output = MathFunctions.smoothInterpolation(0, outputScale, output);
                    break;

                case EXP_DECREASE:
                    output = MathFunctions.expDecreaseInterpolation(0, outputScale, output);
                    break;

                case EXP_INCREASE:
                    output = MathFunctions.expIncreaseInterpolation(0, outputScale, output);
                    break;
            }
        }

        output = ((outputClip == 0) ? output
                : (output > outputClip) ? outputClip
                : (output < -outputClip) ? -outputClip
                : output)
                + (signedScaleOutput ? 0 : outputScale);

        if (outputPatch != null) {
            outputPatch.passData();
        }
    }

    public double getOutput() {
        return output;
    }

}
