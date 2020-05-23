package com.jbatista.bricks;

import com.jbatista.bricks.components.Module;
import com.jbatista.bricks.components.Patch;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {

    private static int SAMPLE_RATE = 44100;

    private static final Timer TIMER = new Timer("Bricks tick producer");
    private static TimerTask TIMER_TASK = new TickTask();

    static int MODULES_SIZE = 0;
    static final List<Module> MODULES = new ArrayList<>();

    static int PATCHES_SIZE = 0;
    static final List<Patch> PATCHES = new ArrayList<>();

    public static void start() {
        stop();

        TIMER_TASK = new TickTask();
        TIMER.scheduleAtFixedRate(TIMER_TASK, 100, SAMPLE_RATE / 1000);
    }

    public static void stop() {
        TIMER_TASK.cancel();
        TIMER.purge();
    }

    public static void setSampleRate(int sampleRate) {
        SAMPLE_RATE = sampleRate;
    }

    public void addModule(Module module) {
        MODULES.add(module);
        MODULES_SIZE = MODULES.size();
    }

    public void removeModule(Module module) {
        MODULES.remove(module);
        MODULES_SIZE = MODULES.size();
    }

    public void addPatch(Patch patch) {
        PATCHES.add(patch);
        PATCHES_SIZE = PATCHES.size();
    }

    public void removePatch(Patch patch) {
        PATCHES.remove(patch);
        PATCHES_SIZE = PATCHES.size();
    }

}
