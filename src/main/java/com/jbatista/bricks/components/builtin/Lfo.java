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
                this::setFrequency));

        controllers.add(new Controller(
                "Main Amp.", "Defines the amplitude for all outputs, from 0 to 2x their respective max.",
                0, 2, 0.01, 0.5, Controller.Curve.LINEAR,
                this::setMainLevel));

        controllers.add(new Controller(
                "Wave 1 Amp.", "Defines the output 1 max. amplitude",
                0, 1, 0.01, 1, Controller.Curve.LINEAR,
                this::setWave1Level));

        controllers.add(new Controller(
                "Wave 2 Amp.", "Defines the output 2 max. amplitude",
                0, 1, 0.01, 1, Controller.Curve.LINEAR,
                this::setWave2Level));

        controllers.add(new Controller(
                "Wave 3 Amp.", "Defines the output 3 max. amplitude",
                0.5, 1, 0.01, 1, Controller.Curve.LINEAR,
                this::setWave3Level));

        controllers.add(new Controller(
                "Wave 4 Amp.", "Defines the output 4 max. amplitude",
                0.5, 1, 0.01, 1, Controller.Curve.LINEAR,
                this::setWave4Level));
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
                    generalPurposeOscillator.square();
                    break;

                case TRIANGLE:
                    generalPurposeOscillator.triangle();
                    break;

                case SAWTOOTH_UP:
                    generalPurposeOscillator.sawUp();
                    break;

                case SAWTOOTH_DOWN:
                    generalPurposeOscillator.sawDown();
                    break;

                default:
                    generalPurposeOscillator.sine();
                    break;
            }

            generalPurposeOscillator.advancePeriod();
            periodValue = generalPurposeOscillator.getPeriodValue();

            outputs.get(0).write(periodValue * wave1Level * mainLevel);
            outputs.get(1).write(periodValue * wave2Level * mainLevel);
            outputs.get(2).write(periodValue * wave3Level * mainLevel);
            outputs.get(3).write(periodValue * wave4Level * mainLevel);
        } else {
            generalPurposeOscillator.reset();
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

    void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    void setMainLevel(double mainLevel) {
        this.mainLevel = mainLevel;
    }

    void setWave1Level(double wave1Level) {
        this.wave1Level = wave1Level;
    }

    void setWave2Level(double wave2Level) {
        this.wave2Level = wave2Level;
    }

    void setWave3Level(double wave3Level) {
        this.wave3Level = wave3Level;
    }

    void setWave4Level(double wave4Level) {
        this.wave4Level = wave4Level;
    }

}
