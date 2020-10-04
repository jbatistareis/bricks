package com.jbatista.bricks;

import com.jbatista.bricks.components.CommonModule;

import java.util.ArrayList;
import java.util.List;

public class Instrument {

    public static final double SAMPLE_RATE = 44100;

    private int index;

    private final List<CommonModule> MODULES = new ArrayList<>();
    private int MODULES_SIZE = 0;

    public synchronized void runProcess() {
        for (index = 0; index < MODULES_SIZE; index++) MODULES.get(index).process();
    }

    public synchronized void addModule(CommonModule module) {
        if (!MODULES.contains(module)) {
            MODULES.add(module);
            MODULES_SIZE = MODULES.size();
        }
    }

    public synchronized void removeModule(CommonModule module) {
        if (MODULES.contains(module)) {
            MODULES.remove(module);
            MODULES_SIZE = MODULES.size();
        }
    }

}
