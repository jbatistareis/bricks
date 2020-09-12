package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;

public class Passthrough extends CommonModule {

    private int index = 0;
    private double input;

    public Passthrough() {
        name = "Passthrough";

        inputs.add(new InputConnector("In", "Receives a signal, and passes it to all outputs, unchanged"));

        for (int i = 0; i < 6; i++) {
            outputs.add(new OutputConnector("Out " + (i + 1), "Returns the original input signal"));
        }
    }

    @Override
    public void process() {
        input = inputs.get(0).read();

        for (index = 0; index < 6; index++) outputs.get(index).write(input);
    }

}
