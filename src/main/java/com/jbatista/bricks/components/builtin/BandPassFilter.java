package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.filter.BandPass;

public class BandPassFilter extends FilterModule {

    private final BandPass bandPass = new BandPass();

    public BandPassFilter() {
        name = "Band-pass filter";

        filter = bandPass;

        controllers.add(new Controller(
                "Center Freq.", "Sets the center frequency",
                20, 20000, 0.005, 1, Controller.Curve.EXPONENTIAL,
                bandPass::setCenterFrequency));

        controllers.add(new Controller(
                "Quality", "Sets the filter quality factor",
                0, 20, 0.01, 0, Controller.Curve.LINEAR,
                bandPass::setQ));
    }

}
