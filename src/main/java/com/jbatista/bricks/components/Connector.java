package com.jbatista.bricks.components;

import java.util.ArrayList;
import java.util.List;

class Connector {

    private final String name;
    private final String description;

    protected final List<Patch> patches = new ArrayList<>(6);
    protected int patchesCount;

    private double ratio = 1;

    private double scaleCenter = 0;

    boolean connected;

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
            patches.get(i).data = data * ratio + scaleCenter;
    }

    public double read() {
        double value = patches.get(0).data;

        for (int i = 1; i < patchesCount; i++)
            value += patches.get(i).data;

        return value * ratio + scaleCenter;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getScaleCenter() {
        return scaleCenter;
    }

    public void setScaleCenter(double scaleCenter) {
        this.scaleCenter = Math.max(-2, Math.min(scaleCenter, 2));
    }

    public boolean isConnected() {
        return connected;
    }

}
