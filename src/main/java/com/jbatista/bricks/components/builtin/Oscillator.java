package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Clock;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.MathFunctions;

public class Oscillator extends CommonModule {
    public enum Shape {SINE, SQUARE, TRIANGLE, SAWTOOTH_UP, SAWTOOTH_DOWN, WHITE_NOISE}

    private Shape shape;

    private double frequency;
    private double frequencyPeriod;
    private double inputFrequency;
    private double previousFrequency = 0;

    private int period;
    private int periodPhase;
    private int periodAccumulator;
    private double periodValue;

    private double sawIncrement;
    private double triangleIncrement;

    public Oscillator() {
        name = "Oscillator";

        inputs.add(new InputConnector("Freq.", "Receives a frequency to play"));
        inputs.add(new InputConnector("Lin. FM", "Linear frequency modulation"));
        inputs.add(new InputConnector("Pitch", "Sets small a pitch bend"));

        inputs.get(1).setOutputScaleCenter(1);
        inputs.get(2).setOutputScaleCenter(1);

        outputs.add(new OutputConnector("Freq.", "Returns the received frequency"));
        outputs.add(new OutputConnector("Wave", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Active", "Indicates that this oscillator is producing a signal"));

        controllers.add(new Controller(
                "Frequency", "Defines a fixed frequency",
                1, 2000, 0.005, 0.5, Controller.Curve.LINEAR,
                this::setInputFrequency));

        controllers.add(new Controller(
                "Freq. ratio", "Inc./dec. input frequency",
                0.5, 10, 0.1, 1, Controller.Curve.ORIGINAL,
                inputs.get(0)::setOutputRatio));

        controllers.add(new Controller(
                "FM strength", "How much modulation will applied",
                0, 1, 0.01, 0.5, Controller.Curve.ORIGINAL,
                inputs.get(2)::setOutputScale));

        controllers.add(new Controller(
                "Shape", "Sets the wave shape",
                0, 5, 1, 0, Controller.Curve.ORIGINAL,
                this::setShape,
                0, 1, 2, 3, 4, 5));
    }

    @Override
    public void process() {
        if (inputs.get(0).isConnected()) {
            inputFrequency = inputs.get(0).read();
        }

        if (inputFrequency > 0) {
            outputs.get(2).write(1);
        } else {
            outputs.get(2).write(0);
        }

        // FM
        if (inputs.get(1).isConnected()) {
            frequency = inputFrequency * inputs.get(1).read();
        } else {
            frequency = inputFrequency;
        }

        frequency *= Math.max(0.5, Math.min(inputs.get(1).read(), 1.5));

        outputs.get(0).write(inputFrequency); // frequency passthrough

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

                case WHITE_NOISE:
                    whiteNoise();
                    break;

                default:
                    sine();
                    break;
            }

            periodTimer();
            outputs.get(1).write(periodValue);
        } else {
            periodAccumulator = 0;
            outputs.get(0).write(0);
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

            case 5:
                this.shape = Shape.WHITE_NOISE;
                break;

            default:
                this.shape = Shape.SINE;
                break;
        }
    }

    void setInputFrequency(double inputFrequency) {
        this.inputFrequency = inputFrequency;
    }

    public double getFrequency() {
        return frequency;
    }

}
