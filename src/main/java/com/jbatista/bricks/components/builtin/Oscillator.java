package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.GeneralPurposeOscillator;

public class Oscillator extends CommonModule {
    public enum Shape {SINE, SQUARE, TRIANGLE, SAWTOOTH_UP, SAWTOOTH_DOWN, WHITE_NOISE}

    private Shape shape;
    private final GeneralPurposeOscillator generalPurposeOscillator = new GeneralPurposeOscillator();

    private double frequency;
    private double previousFrequency = 0;

    public Oscillator(Instrument instrument) {
        super(instrument);
        generalPurposeOscillator.setSampleRate(Instrument.SAMPLE_RATE);

        name = "Oscillator";

        inputs.add(new InputConnector("Freq.", "Receives a frequency to play"));
        inputs.add(new InputConnector("Lin. FM", "Linear frequency modulation"));

        inputs.get(1).setScaleCenter(1);

        outputs.add(new OutputConnector("Freq.", "Returns the received frequency"));
        outputs.add(new OutputConnector("Wave", "Returns the resulting wave"));
        outputs.add(new OutputConnector("Active", "Indicates that this oscillator is producing a signal"));

        controllers.add(new Controller(
                "Shape", "Sets the wave shape",
                0, 5, 1, 0, Controller.Curve.ORIGINAL,
                this::setShape,
                0, 1, 2, 3, 4, 5));

        controllers.add(new Controller(
                "Freq. ratio", "Inc./dec. input frequency",
                0.1, 10, 0.1, 1, Controller.Curve.ORIGINAL,
                inputs.get(0)::setRatio));

        controllers.add(new Controller(
                "FM str.", "How much modulation is going to be applied",
                0, 1, 0.01, 1, Controller.Curve.ORIGINAL,
                inputs.get(1)::setRatio));

        controllers.add(new Controller(
                "Out Vol.", "Sets the output volume",
                0, 10, 0.01, 0.5, Controller.Curve.LINEAR,
                outputs.get(1)::setRatio));
    }

    @Override
    public void process() {
        frequency = inputs.get(0).read();
        outputs.get(0).write(frequency); // frequency passthrough

        if (frequency > 0) {
            outputs.get(2).write(1);

            if (frequency != previousFrequency) {
                generalPurposeOscillator.setFrequency(frequency);
                previousFrequency = frequency;
            }

            switch (shape) {
                case SQUARE:
                    outputs.get(1).write(generalPurposeOscillator.square(inputs.get(1).read()));
                    break;

                case TRIANGLE:
                    outputs.get(1).write(generalPurposeOscillator.triangle(inputs.get(1).read()));
                    break;

                case SAWTOOTH_UP:
                    outputs.get(1).write(generalPurposeOscillator.sawUp(inputs.get(1).read()));
                    break;

                case SAWTOOTH_DOWN:
                    outputs.get(1).write(generalPurposeOscillator.sawDown(inputs.get(1).read()));
                    break;

                case WHITE_NOISE:
                    outputs.get(1).write(generalPurposeOscillator.whiteNoise());
                    break;

                default:
                    outputs.get(1).write(generalPurposeOscillator.sine(inputs.get(1).read()));
                    break;
            }
        } else {
            outputs.get(1).write(0);
            outputs.get(2).write(0);
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

            case 5:
                this.shape = Shape.WHITE_NOISE;
                break;

            default:
                this.shape = Shape.SINE;
                break;
        }
    }

}
