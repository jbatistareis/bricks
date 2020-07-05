package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Clock;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.MathFunctions;

public class Lfo extends CommonModule {
    public enum Shape {SINE, SQUARE, TRIANGLE, SAWTOOTH_UP, SAWTOOTH_DOWN}

    private Shape shape;

    private double frequency;
    private double frequencyPeriod;
    private double previousFrequency = 0;

    private double mainLevel;
    private double wave1Level;
    private double wave2Level;
    private double wave3Level;
    private double wave4Level;

    private int period;
    private int periodPhase;
    private int periodAccumulator;
    private double periodValue;

    private double sawIncrement;
    private double triangleIncrement;

    public Lfo() {
        name = "LFO";

        outputs.add(new OutputConnector("Wave 1", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Wave 2", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Wave 3", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Wave 4", "Returns the resulting wave"));

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

        controllers.add(new Controller(
                "Shape", "Sets the wave shape",
                0, 5, 1, 0, Controller.Curve.ORIGINAL,
                this::setShape,
                0, 1, 2, 3, 4));
    }

    @Override
    public void process() {
        if (frequency != 0) {
            if (frequency != previousFrequency) {
                frequencyPeriod = frequency / Clock.getSampleRate();
                period = (int) (Clock.getSampleRate() / frequency);
                periodPhase = period / 2;

                sawIncrement = 2d / period;
                triangleIncrement = 2d / periodPhase;

                previousFrequency = frequency;
            }

            switch (shape) {
                case SQUARE:
                    square();
                    break;

                case TRIANGLE:
                    triangle();
                    break;

                case SAWTOOTH_UP:
                    sawUp();
                    break;

                case SAWTOOTH_DOWN:
                    sawDown();
                    break;

                default:
                    sine();
                    break;
            }

            periodTimer();
            outputs.get(0).write(periodValue * wave1Level * mainLevel);
            outputs.get(1).write(periodValue * wave2Level * mainLevel);
            outputs.get(2).write(periodValue * wave3Level * mainLevel);
            outputs.get(3).write(periodValue * wave4Level * mainLevel);
        } else {
            periodAccumulator = 0;
            outputs.get(0).write(0);
            outputs.get(1).write(0);
            outputs.get(2).write(0);
            outputs.get(3).write(0);
        }
    }

    private void sine() {
        periodValue = Math.sin(MathFunctions.TAU * frequencyPeriod * periodAccumulator);
    }

    private void square() {
        periodValue = (periodAccumulator < periodPhase) ? 1 : -1;
    }

    private void triangle() {
        periodValue = (periodAccumulator == 0) ? -1 : (periodAccumulator < periodPhase) ? (periodValue + triangleIncrement) : (periodValue - triangleIncrement);
    }

    private void sawUp() {
        periodValue = (periodAccumulator == 0) ? -1 : (periodValue + sawIncrement);
    }

    private void sawDown() {
        periodValue = (periodAccumulator == 0) ? 1 : (periodValue - sawIncrement);
    }

    private void whiteNoise() {
        periodValue = 2 * MathFunctions.RANDOM.nextDouble() - 1;
    }

    private void periodTimer() {
        periodAccumulator = (periodAccumulator < period) ? (periodAccumulator + 1) : 0;
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

    public double getFrequency() {
        return frequency;
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
