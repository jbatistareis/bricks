package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.BandPass;

public class BandPassFilter extends FilterModule {

    private final BandPass bandPass = new BandPass();
    private double offset;

    public BandPassFilter() {
        name = "Band-pass filter";

        inputs.add(new InputConnector("Lin. FM", "Linear frequency modulation"));
        inputs.get(1).setOutputScaleCenter(1);

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

    @Override
    public void process() {
        offset = inputs.get(1).read();
        if (offset > 0) {
            bandPass.setCenterFrequency(controllers.get(0).getDisplayValue() * offset);
        }

        super.process();
    }

}
