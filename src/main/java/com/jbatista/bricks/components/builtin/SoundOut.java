package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Clock;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.util.MathFunctions;

public class SoundOut extends CommonModule {

    private double sample;

    public SoundOut() {
        name = "Sound Output";

        inputs.add(new InputConnector("In", "Receives a signal"));

        controllers.add(new Controller(
                "Vol.", "Output volume",
                0, 1, 0.01, 1, Controller.Curve.EXPONENTIAL,
                inputs.get(0)::setOutputRatio));
    }

    @Override
    public void process() {
        sample = inputs.get(0).read();
    }

    public void get16bitFrame(boolean bigEndian, byte[] buffer) {
        Clock.tick();

        // [L] - [R]
        MathFunctions.primitiveTo16bit(bigEndian, buffer, 0, (int) (sample * MathFunctions.SIGNED_16_BIT_MAX));
        MathFunctions.primitiveTo16bit(bigEndian, buffer, 2, (int) (sample * MathFunctions.SIGNED_16_BIT_MAX));
    }

    public void getDoubleFrame(double[] buffer) {
        Clock.tick();

        // [L] - [R]
        buffer[0] = sample;
        buffer[1] = sample;
    }

    public void getShortFrame(short[] buffer) {
        Clock.tick();

        // [L] - [R]
        buffer[0] = (short) sample;
        buffer[1] = (short) sample;
    }

    public void getFloatFrame(float[] buffer) {
        Clock.tick();

        // [L] - [R]
        buffer[0] = (float) sample;
        buffer[1] = (float) sample;
    }

}
