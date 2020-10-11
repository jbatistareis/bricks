package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.filter.LowPass;

public class LowPassFilter extends FilterModule {

    private final LowPass lowPass = new LowPass();
    private double currentFrequency;

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
                "FM", "How much modulation is going to be applied",
                0, 1, 0.01, 1, Controller.Curve.ORIGINAL,
                inputs.get(1)::setOutputRatio));
    }

    @Override
    public void process() {
        if (inputs.get(1).isConnected())
            lowPass.setCutoffFrequency(currentFrequency * inputs.get(1).read());

        super.process();
    }

    private void setCurrentFrequency(double frequency) {
        this.currentFrequency = frequency;
        lowPass.setCutoffFrequency(frequency);
    }

}
