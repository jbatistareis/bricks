package com.jbatista.bricks;

import com.jbatista.bricks.components.CommonModule;

import java.util.ArrayList;
import java.util.List;

public class Clock {

    private static int SAMPLE_RATE = 44100;

    private static int index;

    static final List<CommonModule> MODULES = new ArrayList<>();
    static int MODULES_SIZE = 0;

    public static void tick() {
        for (index = 0; index < MODULES_SIZE; index++) {
            MODULES.get(index).process();
        }
    }

    public static int getSampleRate() {
        return SAMPLE_RATE;
    }

    public static void setSampleRate(int sampleRate) {
        SAMPLE_RATE = sampleRate;
    }

    public static void addModule(CommonModule module) {
        if (!MODULES.contains(module)) {
            MODULES.add(module);
            MODULES_SIZE = MODULES.size();
        }
    }

    public static void removeModule(CommonModule module) {
        if (MODULES.contains(module)) {
            MODULES.remove(module);
            MODULES_SIZE = MODULES.size();
        }
    }

}
