package com.jbatista.bricks;

import com.jbatista.bricks.components.Module;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {

    private static int SAMPLE_RATE = 44100;

    private static final Timer TIMER = new Timer("Bricks tick producer");
    private static TimerTask TIMER_TASK = new TickTask();

    static int MODULES_SIZE = 0;
    static final LinkedList<Module> MODULES = new LinkedList<>();

    public static void start() {
        TIMER_TASK.cancel();
        TIMER.purge();

        TIMER_TASK = new TickTask();
        TIMER.scheduleAtFixedRate(TIMER_TASK, 100, SAMPLE_RATE / 1000);
    }

    public static void stop() {
        TIMER_TASK.cancel();
        TIMER.purge();
    }

    public static void reset() {
        stop();
        start();
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

}
