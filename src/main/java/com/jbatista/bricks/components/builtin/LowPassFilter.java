package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.filter.LowPass;

public class LowPassFilter extends FilterModule {

    private final LowPass lowPass = new LowPass();

    public LowPassFilter() {
        name = "Low-pass Filter";

        filter = lowPass;

        controllers.add(new Controller(
                "Frequency", "Sets the frequency cutoff",
                1, 2000, 0.005, 1, Controller.Curve.LINEAR,
                lowPass::setCutoffFrequency));

        controllers.add(new Controller(
                "Resonance", "Sets the resonance",
                0, 20, 0.01, 0, Controller.Curve.LINEAR,
                lowPass::setResonance));
    }

}
