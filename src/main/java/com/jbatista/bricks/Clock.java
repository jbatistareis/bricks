package com.jbatista.bricks;

import com.jbatista.bricks.components.Module;
import com.jbatista.bricks.components.Patch;

import java.util.ArrayList;
import java.util.List;

public class Clock {

    private static int SAMPLE_RATE = 44100;

    private static int index;

    static final List<Module> MODULES = new ArrayList<>();
    static int MODULES_SIZE = 0;

    static final List<Patch> PATCHES = new ArrayList<>();
    static int PATCHES_SIZE = 0;

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

    public static void addModule(Module module) {
        MODULES.add(module);
        MODULES_SIZE = MODULES.size();
    }

    public static void removeModule(Module module) {
        MODULES.remove(module);
        MODULES_SIZE = MODULES.size();
    }

    public static void addPatch(Patch patch) {
        PATCHES.add(patch);
        PATCHES_SIZE = PATCHES.size();
    }

    public static void removePatch(Patch patch) {
        PATCHES.remove(patch);
        PATCHES_SIZE = PATCHES.size();
    }

}
