package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.HighPass;

public class HighPassFilter extends FilterModule {

    private final HighPass highPass = new HighPass();
    private double offset;

    public HighPassFilter() {
        name = "High-pass filter";

        inputs.add(new InputConnector("Lin. FM", "Linear frequency modulation"));
        inputs.get(0).setOutputScaleCenter(1);

        filter = highPass;

        controllers.add(new Controller(
                "Frequency", "Sets the frequency cutoff",
                20, 20000, 0.005, 1, Controller.Curve.LINEAR,
                highPass::setCutoffFrequency));

        controllers.add(new Controller(
                "Resonance", "Sets the resonance",
                0, 20, 0.01, 0, Controller.Curve.LINEAR,
                highPass::setResonance));

        controllers.add(new Controller(
                "FM", "How much modulation is going to be applied",
                0.1, 10, 0.1, 0, Controller.Curve.ORIGINAL,
                inputs.get(1)::setOutputRatio));
    }

    @Override
    public void process() {
        highPass.setCutoffFrequency(controllers.get(0).getDisplayValue() * inputs.get(0).read());

        super.process();
    }

}
