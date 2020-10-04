package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.BandPass;

public class BandPassFilter extends FilterModule {

    private final BandPass bandPass = new BandPass();
    private double offset;

    public BandPassFilter(Instrument instrument) {
        super(instrument);

        name = "Band-pass filter";

        inputs.add(new InputConnector("Lin. FM", "Linear frequency modulation"));
        inputs.get(0).setOutputScaleCenter(1);

        filter = bandPass;

        controllers.add(new Controller(
                "Center Freq.", "Sets the center frequency",
                20, 20000, 0.005, 1, Controller.Curve.LINEAR,
                bandPass::setCenterFrequency));

        controllers.add(new Controller(
                "Quality", "Sets the filter quality factor",
                1, 20, 0.01, 0, Controller.Curve.LINEAR,
                bandPass::setQ));

        controllers.add(new Controller(
                "FM", "How much modulation is going to be applied",
                0.1, 10, 0.1, 0, Controller.Curve.ORIGINAL,
                inputs.get(1)::setOutputRatio));
    }

    @Override
    public void process() {
        if (inputs.get(1).isConnected())
            bandPass.setCenterFrequency(bandPass.getCenterFrequency() * inputs.get(1).read());

        super.process();
    }

}
