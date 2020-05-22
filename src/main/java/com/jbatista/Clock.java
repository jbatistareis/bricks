package com.jbatista;

import java.util.Timer;
import java.util.TimerTask;

public class Clock {

    private static int SAMPLE_RATE = 44100;

    private static final Timer TIMER = new Timer("Bricks tick producer");
    private static TimerTask TIMER_TASK = new TickTask();

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
        stop();
    }

    public static void setSampleRate(int sampleRate) {
        SAMPLE_RATE = sampleRate;
    }

}
