package com.jbatista.bricks.components;

import java.util.ArrayList;
import java.util.List;

class Connector {

    private final String name;
    private final String description;

    protected final List<Patch> patches = new ArrayList<>();
    protected int patchesCount;

    private double outputRatio = 1;

    private double outputScaleCenter = 0;

    boolean connected = false;

    public Connector(String name, String description) {
        this.name = name;
        this.description = description;

        disconnectAllPatches();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void connectPatch(Patch patch) {
        if (!patches.contains(patch)) {
            patches.add(patch);
            connected = true;
            patchesCount++;
        }
    }

    public void disconnectPatch(Patch patch) {
        if (patches.remove(patch)) {
            patchesCount--;
            connected = !patches.isEmpty();

            if (!connected)
                patches.add(new Patch());
        }
    }

    public void disconnectAllPatches() {
        connected = false;
        patches.clear();
        patches.add(new Patch());
        patchesCount = 1;
    }

    public void write(double data) {
        for (int i = 0; i < patchesCount; i++)
            patches.get(i).data = data * outputRatio + outputScaleCenter;
    }

    public double read() {
        double value = 0;

        if (patchesCount == 1)
            value = patches.get(0).data * outputRatio + outputScaleCenter;
        else
            for (int i = 1; i < patchesCount; i++)
                value += patches.get(i).data * outputRatio + outputScaleCenter;

        return value;
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
