package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;

public class Mixer extends CommonModule {

    private double output;

    public Mixer() {
        name = "Mixer";

        for (int i = 0; i < 6; i++) {
            inputs.add(new InputConnector("In " + (i + 1), "Receives a signal"));
            controllers.add(new Controller(
                    "Vol. " + (i + 1), "Sets the individual output volume",
                    0, 2, 0.01, 1, Controller.Curve.EXPONENTIAL,
                    inputs.get(i)::setOutputRatio));
        }

        outputs.add(new OutputConnector("Out", "Returns every received signal"));
    }

    @Override
    public void process() {
        output = 0;

        for (int i = 0; i < 6; i++) {
            if (inputs.get(i).isConnected()) output += inputs.get(i).read();
        }

        outputs.get(0).write(output);
    }

}
