package com.jbatista.bricks.components;

public abstract class Connector {

    private final String name;
    private final String description;

    protected Patch inputPatch = null;
    protected Patch outputPatch = null;

    private double inputData = 0;

    private double outputRatio = 1;

    private double outputScaleCenter = 0;

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
        inputData = data;

        if (inputPatch != null) inputPatch.passData();
    }

    public double read() {
        return (inputData + outputScaleCenter) * outputRatio;
    }

    public double getOutputRatio() {
        return outputRatio;
    }

    public void setOutputRatio(double outputRatio) {
        this.outputRatio = outputRatio;
    }

    public double getOutputScaleCenter() {
        return outputScaleCenter;
    }

    public void setOutputScaleCenter(double outputScaleCenter) {
        this.outputScaleCenter = Math.max(-2, Math.min(outputScaleCenter, 2));
    }

    public boolean isConnected() {
        return connected;
    }

}
