package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.GeneralPurposeOscillator;

public class Lfo extends CommonModule {
    public enum Shape {SINE, SQUARE, TRIANGLE, SAWTOOTH_UP, SAWTOOTH_DOWN}

    private Shape shape;
    private final GeneralPurposeOscillator generalPurposeOscillator = new GeneralPurposeOscillator();

    private double frequency;
    private double previousFrequency = 0;

    private double mainLevel;
    private double wave1Level;
    private double wave2Level;
    private double wave3Level;
    private double wave4Level;

    private double periodValue;

    public Lfo(Instrument instrument) {
        super(instrument);
        generalPurposeOscillator.setSampleRate(Instrument.SAMPLE_RATE);

        name = "LFO";

        outputs.add(new OutputConnector("Wave 1", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Wave 2", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Wave 3", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Wave 4", "Returns the resulting wave"));

        controllers.add(new Controller(
                "Shape", "Sets the wave shape",
                0, 5, 1, 0, Controller.Curve.ORIGINAL,
                this::setShape,
                0, 1, 2, 3, 4));

        controllers.add(new Controller(
                "Frequency", "Defines a fixed frequency",
                0, 100, 0.01, 0, Controller.Curve.LINEAR,
                value -> this.frequency = value));

        controllers.add(new Controller(
                "Main Amp.", "Defines the amplitude for all outputs, from 0 to 2x their respective max.",
                0, 2, 0.01, 0.5, Controller.Curve.LINEAR,
                value -> this.mainLevel = value));

        controllers.add(new Controller(
                "Wave 1 Amp.", "Defines the output 1 max. amplitude",
                0, 1, 0.01, 1, Controller.Curve.LINEAR,
                value -> this.wave1Level = value));

        controllers.add(new Controller(
                "Wave 2 Amp.", "Defines the output 2 max. amplitude",
                0, 1, 0.01, 1, Controller.Curve.LINEAR,
                value -> this.wave2Level = value));

        controllers.add(new Controller(
                "Wave 3 Amp.", "Defines the output 3 max. amplitude",
                0.5, 1, 0.01, 1, Controller.Curve.LINEAR,
                value -> this.wave3Level = value));

        controllers.add(new Controller(
                "Wave 4 Amp.", "Defines the output 4 max. amplitude",
                0.5, 1, 0.01, 1, Controller.Curve.LINEAR,
                value -> this.wave4Level = value));
    }

    @Override
    public void process() {
        if (frequency != 0) {
            if (frequency != previousFrequency) {
                generalPurposeOscillator.setFrequency(frequency);
                previousFrequency = frequency;
            }

            switch (shape) {
                case SQUARE:
                    periodValue = generalPurposeOscillator.square(0);
                    break;

                case TRIANGLE:
                    periodValue = generalPurposeOscillator.triangle(0);
                    break;

                case SAWTOOTH_UP:
                    periodValue = generalPurposeOscillator.sawUp(0);
                    break;

                case SAWTOOTH_DOWN:
                    periodValue = generalPurposeOscillator.sawDown(0);
                    break;

                default:
                    periodValue = generalPurposeOscillator.sine(0);
                    break;
            }

            outputs.get(0).write(periodValue * wave1Level * mainLevel);
            outputs.get(1).write(periodValue * wave2Level * mainLevel);
            outputs.get(2).write(periodValue * wave3Level * mainLevel);
            outputs.get(3).write(periodValue * wave4Level * mainLevel);
        } else {
            outputs.get(0).write(0);
            outputs.get(1).write(0);
            outputs.get(2).write(0);
            outputs.get(3).write(0);
        }
    }

    private void setShape(double shape) {
        switch ((int) shape) {
            case 1:
                this.shape = Shape.SQUARE;
                break;

            case 2:
                this.shape = Shape.TRIANGLE;
                break;

            case 3:
                this.shape = Shape.SAWTOOTH_UP;
                break;

            case 4:
                this.shape = Shape.SAWTOOTH_DOWN;
                break;

            default:
                this.shape = Shape.SINE;
                break;
        }
    }

}
