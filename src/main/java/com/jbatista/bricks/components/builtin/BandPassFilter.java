package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.filter.BandPass;

public class BandPassFilter extends FilterModule {

    private final BandPass bandPass = new BandPass();

    public BandPassFilter() {
        filter = bandPass;

        controllers.add(new Controller(
                "Center Freq.", "Sets the center frequency",
                1, 2000, 0, Controller.Curve.LINEAR,
                bandPass::setCenterFrequency));

        controllers.add(new Controller(
                "Quality", "Sets the filter quality factor",
                0, 20, 0, Controller.Curve.LINEAR,
                bandPass::setQ));
    }

}
