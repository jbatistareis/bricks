package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.LowPass;

public class LowPassFilter extends FilterModule {

    private final LowPass lowPass = new LowPass();
    private double currentFrequency;
    private boolean opening;

    public LowPassFilter(Instrument instrument) {
        super(instrument);

        name = "Low-pass Filter";

        inputs.add(new InputConnector("Gate", "Control gate for FM"));

        filter = lowPass;

        controllers.add(new Controller(
                "Frequency", "Sets the frequency cutoff",
                20, 20000, 0.005, 1, Controller.Curve.LINEAR,
                this::setCurrentFrequency));

        controllers.add(new Controller(
                "Resonance", "Sets the resonance",
                1, 20, 0.01, 0, Controller.Curve.LINEAR,
                lowPass::setResonance));

        controllers.add(new Controller(
                "Gate dir.", "Controls if gate is opening or closing",
                0, 1, 1, 0, Controller.Curve.ORIGINAL,
                value -> opening = (value == 0),
                0, 1));
    }

    @Override
    public void process() {
        if (inputs.get(1).isConnected())
            lowPass.setCutoffFrequency(
                    Math.min(currentFrequency * (opening ? inputs.get(1).read() : (1 - inputs.get(1).read())), 20000));

        super.process();
    }

    private void setCurrentFrequency(double frequency) {
        this.currentFrequency = frequency;
        lowPass.setCutoffFrequency(frequency);
    }

}
