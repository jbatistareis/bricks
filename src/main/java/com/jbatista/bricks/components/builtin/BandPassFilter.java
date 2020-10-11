package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.BandPass;

public class BandPassFilter extends FilterModule {

    private final BandPass bandPass = new BandPass();
    private double currentFrequency;

    public BandPassFilter(Instrument instrument) {
        super(instrument);

        name = "Band-pass filter";

        inputs.add(new InputConnector("Gate", "Control gate for FM"));

        filter = bandPass;

        controllers.add(new Controller(
                "Center Freq.", "Sets the center frequency",
                20, 20000, 0.005, 1, Controller.Curve.LINEAR,
                this::setCurrentFrequency));

        controllers.add(new Controller(
                "Quality", "Sets the filter quality factor",
                1, 20, 0.01, 0, Controller.Curve.LINEAR,
                bandPass::setQ));

        controllers.add(new Controller(
                "FM", "How much modulation is going to be applied",
                0, 1, 0.01, 1, Controller.Curve.ORIGINAL,
                inputs.get(1)::setOutputRatio));
    }

    @Override
    public void process() {
        if (inputs.get(1).isConnected())
            bandPass.setCenterFrequency(currentFrequency * inputs.get(1).read());

        super.process();
    }

    private void setCurrentFrequency(double frequency) {
        this.currentFrequency = frequency;
        bandPass.setCenterFrequency(frequency);
    }

}
