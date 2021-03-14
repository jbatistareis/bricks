package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.MathFunctions;

public class EnvelopeGenerator extends CommonModule {

    private enum State {
        ATTACK(0), DECAY(1), SUSTAIN(2), RELEASE(3), PRE_IDLE(4), HOLD(5), IDLE(6);

        private int id;

        State(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private static final double TABLE_STEP = 1d / 127;

    private double trigger;

    private State state = State.IDLE;
    private double startAmplitude;
    private double endAmplitude;
    private double currentAmplitude;
    private double position;
    private double progress;
    private final double[] factor = new double[5];
    private final int[] size = new int[5];

    private double currentInput;
    private double capturedInput;
    private boolean hold;
    private boolean released = true;

    private double attackLevel;
    private double decayLevel;
    private double sustainLevel;
    private double releaseLevel;

    public EnvelopeGenerator(Instrument instrument) {
        super(instrument);
        name = "Envelope generator";

        size[State.PRE_IDLE.getId()] = (int) (Instrument.SAMPLE_RATE / 120);
        factor[State.PRE_IDLE.getId()] = 1d / size[State.PRE_IDLE.getId()];

        inputs.add(new InputConnector("In", "The sound signal that will receive AM"));
        inputs.add(new InputConnector("Trigger", "Start/stop signal"));

        outputs.add(new OutputConnector("Out", "The modified signal"));

        controllers.add(new Controller(
                "Atk. Lvl.", "Attack amplitude level",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> attackLevel = calcLevel(value)));

        controllers.add(new Controller(
                "Dec. Lvl.", "Decay amplitude level",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> decayLevel = calcLevel(value)));

        controllers.add(new Controller(
                "Sus. Lvl.", "Sustain amplitude level",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> sustainLevel = calcLevel(value)));

        controllers.add(new Controller(
                "Rel. Lvl.", "Release amplitude level",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> releaseLevel = calcLevel(value)));


        controllers.add(new Controller(
                "Atk. Spd.", "Attack speed",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> changeParameters(State.ATTACK, value)));

        controllers.add(new Controller(
                "Dec. Spd.", "Decay speed",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> changeParameters(State.DECAY, value)));

        controllers.add(new Controller(
                "Sus. Spd.", "Sustain speed",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> changeParameters(State.SUSTAIN, value)));

        controllers.add(new Controller(
                "Rel. Spd.", "Release speed",
                0, 127, 0.01, 1, Controller.Curve.LINEAR,
                value -> changeParameters(State.RELEASE, value)));

        controllers.add(new Controller(
                "Hold input", "Holds input values that are different from 0",
                0, 1, 1, 0, Controller.Curve.ORIGINAL,
                value -> hold = (value == 1),
                0, 1));
    }

    @Override
    public void process() {
        capturedInput = inputs.get(0).read();
        trigger = inputs.get(1).read();

        if (released && (trigger > 0)) {
            released = false;
            initialize();
        } else if (!released && (trigger == 0)) {
            released = true;
        }

        if (!hold || (hold && (trigger > 0)))
            currentInput = capturedInput;

        outputs.get(0).write(currentAmplitude * currentInput);
        advanceEnvelope();
    }

    private static double calcLevel(double position) {
        return MathFunctions.smoothInterpolation(0, 1, TABLE_STEP * position);
    }

    private static double calcSpeed(double position) {
        return MathFunctions.smoothInterpolation(2, 0, TABLE_STEP * position);
    }

    private void changeParameters(State state, double speed) {
        size[state.getId()] = (int) Math.max(size[State.PRE_IDLE.getId()], calcSpeed(speed) * Instrument.SAMPLE_RATE);
        factor[state.getId()] = 1d / size[state.getId()];
    }

    private void advanceEnvelope() {
        switch (state) {
            case ATTACK:
                if (applyEnvelope(State.ATTACK)) {
                    position = 0;
                    progress = 0;

                    startAmplitude = attackLevel;
                    endAmplitude = decayLevel;

                    state = State.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(State.DECAY)) {
                    position = 0;
                    progress = 0;

                    startAmplitude = decayLevel;
                    endAmplitude = sustainLevel;

                    if (released)
                        release();
                    else
                        state = State.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (released) {
                    release();
                    return;
                } else if (applyEnvelope(State.SUSTAIN))
                    state = State.HOLD;
                break;

            case HOLD:
                if (released)
                    release();
                return;

            case RELEASE:
                if (applyEnvelope(State.RELEASE)) silence();
                break;

            case PRE_IDLE:
                if (applyEnvelope(State.PRE_IDLE)) reset();
                break;

            case IDLE:
                return; // do nothing
        }

        if (state.getId() <= 4) {
            position += 1;
            progress += factor[state.getId()];
        }
    }

    private boolean applyEnvelope(State state) {
        if (position < size[state.getId()]) {
            currentAmplitude = MathFunctions.linearInterpolation(startAmplitude, endAmplitude, progress);

            return false;
        }

        return true;
    }

    private void initialize() {
        position = 0;
        progress = 0;

        startAmplitude = 0;
        currentAmplitude = 0;
        endAmplitude = attackLevel;

        state = State.ATTACK;
    }

    private void release() {
        position = 0;
        progress = 0;

        startAmplitude = currentAmplitude;
        endAmplitude = releaseLevel;

        state = State.RELEASE;
    }

    private void silence() {
        position = 0;
        progress = 0;

        startAmplitude = currentAmplitude;
        endAmplitude = 0;

        state = State.PRE_IDLE;
    }

    private void reset() {
        position = 0;
        progress = 0;

        startAmplitude = 0;
        endAmplitude = 0;
        currentAmplitude = 0;

        state = State.IDLE;
    }

}
