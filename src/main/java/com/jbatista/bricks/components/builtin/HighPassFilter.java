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
        inputs.get(1).setOutputScaleCenter(1);

        filter = highPass;

        controllers.add(new Controller(
                "Frequency", "Sets the frequency cutoff",
                20, 20000, 0.005, 1, Controller.Curve.EXPONENTIAL,
                highPass::setCutoffFrequency));

        controllers.add(new Controller(
                "Resonance", "Sets the resonance",
                0, 20, 0.01, 0, Controller.Curve.LINEAR,
                highPass::setResonance));

        controllers.add(new Controller(
                "FM str.", "How much modulation is going to be applied",
                0, 2, 0.01, 0.5, Controller.Curve.LINEAR,
                inputs.get(1)::setOutputRatio));
    }

    @Override
    public void process() {
        offset = inputs.get(1).read();
        if (offset > 0) {
            highPass.setCutoffFrequency(controllers.get(0).getDisplayValue() * offset);
        }

        super.process();
    }

}
