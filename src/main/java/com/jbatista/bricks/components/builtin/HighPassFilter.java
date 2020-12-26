package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.HighPass;

public class HighPassFilter extends FilterModule {

    private final HighPass highPass = new HighPass();
    private double currentFrequency;
    private boolean opening;

    public HighPassFilter(Instrument instrument) {
        super(instrument);

        name = "High-pass filter";

        inputs.add(new InputConnector("Gate", "Control gate for FM"));

        filter = highPass;

        controllers.add(new Controller(
                "Frequency", "Sets the frequency cutoff",
                20, 20000, 0.005, 1, Controller.Curve.LINEAR,
                this::setCurrentFrequency));

        controllers.add(new Controller(
                "Resonance", "Sets the resonance",
                1, 20, 0.01, 0, Controller.Curve.LINEAR,
                highPass::setResonance));

        controllers.add(new Controller(
                "Gate dir.", "Controls if gate is opening or closing",
                0, 1, 1, 0, Controller.Curve.ORIGINAL,
                value -> opening = (value == 0),
                0, 1));
    }

    @Override
    public void process() {
        if (inputs.get(1).isConnected())
            highPass.setCutoffFrequency(
                    Math.min(currentFrequency * (opening ? inputs.get(1).read() : (1 - inputs.get(1).read())), 20000));

        super.process();
    }

    private void setCurrentFrequency(double frequency) {
        this.currentFrequency = frequency;
        highPass.setCutoffFrequency(frequency);
    }

}
