package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.BandPass;

public class BandPassFilter extends FilterModule {

    private final BandPass bandPass = new BandPass();
    private double currentFrequency;
    private boolean opening;

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
                "Gate dir.", "Controls if gate is opening or closing",
                0, 1, 1, 0, Controller.Curve.ORIGINAL,
                value -> opening = (value == 0),
                0, 1));
    }

    @Override
    public void process() {
        if (inputs.get(1).isConnected())
            bandPass.setCenterFrequency(
                    Math.min(currentFrequency * (opening ? inputs.get(1).read() : (1 - inputs.get(1).read())), 20000));

        super.process();
    }

    private void setCurrentFrequency(double frequency) {
        this.currentFrequency = frequency;
        bandPass.setCenterFrequency(frequency);
    }

}
