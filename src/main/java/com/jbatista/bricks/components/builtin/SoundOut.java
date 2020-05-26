package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Clock;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.Module;

public class SoundOut extends Module {

    private double sample;

    public SoundOut() {
        inputs.add(new InputConnector("In", "Receives a signal"));

        controllers.add(new Controller(
                "Vol", "Output volume",
                0, 1, 0.5, Controller.Curve.EXPONENTIAL,
                inputs.get(0)::setOutputScale));
    }

    @Override
    public void process() {
        sample = inputs.get(0).read();
    }

    public void getFrame(double[] buffer) {
        Clock.tick();

        // [L] - [R]
        buffer[0] = sample;
        buffer[1] = sample;
    }

}
