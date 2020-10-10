package com.jbatista.bricks;

import com.jbatista.bricks.components.CommonModule;

import java.util.ArrayList;
import java.util.List;

public class Instrument {

    public static final double SAMPLE_RATE = 44100;

    private int index;

    private final List<CommonModule> modules = new ArrayList<>();
    private int modulesSize = 0;

    public synchronized void runProcess() {
        for (index = 0; index < modulesSize; index++) modules.get(index).process();
    }

    public synchronized void addModule(CommonModule module) {
        if (!modules.contains(module)) {
            modules.add(module);
            modulesSize = modules.size();
        }
    }

    public synchronized void removeModule(CommonModule module) {
        if (modules.contains(module)) {
            modules.remove(module);
            modulesSize = modules.size();
        }
    }

}
