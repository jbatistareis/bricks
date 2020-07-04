package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.filter.HighPass;

public class HighPassFilter extends FilterModule {

    private final HighPass highPass = new HighPass();

    public HighPassFilter() {
        name = "High-pass filter";

        filter = highPass;

        controllers.add(new Controller(
                "Frequency", "Sets the frequency cutoff",
                1, 2000, 0, Controller.Curve.LINEAR,
                highPass::setCutoffFrequency));

        controllers.add(new Controller(
                "Resonance", "Sets the resonance",
                0, 20, 0, Controller.Curve.LINEAR,
                highPass::setResonance));
    }

}
