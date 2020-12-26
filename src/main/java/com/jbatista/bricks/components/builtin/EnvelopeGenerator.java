package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.MathFunctions;

public class EnvelopeGenerator extends CommonModule {

    private enum State {ATTACK, DECAY, SUSTAIN, RELEASE, PRE_IDLE, HOLD, IDLE}

    private static final double[] LEVEL_TABLE = new double[200];
    private static final double[] SPEED_TABLE = new double[200];

    private double currentTrigger;
    private double previousTrigger;

    private State state = State.IDLE;
    private double startAmplitude;
    private double endAmplitude;
    private double currentAmplitude;
    private double position;
    private double progress;
    private final double[] factor = new double[5];
    private final int[] size = new int[5];

    private double input;

    private int attackLevel;
    private int decayLevel;
    private int sustainLevel;
    private int releaseLevel;

    private int attackSpeed;
    private int decaySpeed;
    private int sustainSpeed;
    private int releaseSpeed;

    private int currentAttackSpeed = -1;
    private int currentDecaySpeed = -1;
    private int currentSustainSpeed = -1;
    private int currentReleaseSpeed = -1;

    static {
        double index = 0;
        for (int i = 0; i < 200; i++) {
            LEVEL_TABLE[i] = MathFunctions.linearInterpolation(0, 1, index);
            SPEED_TABLE[i] = MathFunctions.linearInterpolation(2, 0.0001, index);
            index += 0.005;
        }
    }

    public EnvelopeGenerator(Instrument instrument) {
        super(instrument);
        name = "Envelope generator";

        inputs.add(new InputConnector("In", "The sound signal that will receive AM"));
        inputs.add(new InputConnector("Trigger", "Start/stop signal"));

        outputs.add(new OutputConnector("Out", "The modified signal"));

        controllers.add(new Controller(
                "Atk. Lvl.", "Attack amplitude level",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setAttackLevel));

        controllers.add(new Controller(
                "Dec. Lvl.", "Decay amplitude level",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setDecayLevel));

        controllers.add(new Controller(
                "Sus. Lvl.", "Sustain amplitude level",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setSustainLevel));

        controllers.add(new Controller(
                "Rel. Lvl.", "Release amplitude level",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setReleaseLevel));


        controllers.add(new Controller(
                "Atk. Spd.", "Attack speed",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setAttackSpeed));

        controllers.add(new Controller(
                "Dec. Spd.", "Decay speed",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setDecaySpeed));

        controllers.add(new Controller(
                "Sus. Spd.", "Sustain speed",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setSustainSpeed));

        controllers.add(new Controller(
                "Rel. Spd.", "Release speed",
                0, 199, 0.01, 1, Controller.Curve.LINEAR,
                this::setReleaseSpeed));

        final int stateId = stateId(State.PRE_IDLE);
        size[stateId] = (int) (Instrument.SAMPLE_RATE / 15);
        factor[stateId] = 1d / size[stateId];
    }

    @Override
    public void process() {
        currentTrigger = inputs.get(1).read();

        if ((currentTrigger > 0) && (currentTrigger != previousTrigger)) {
            previousTrigger = currentTrigger;

            initialize();
        } else if ((currentTrigger == 0) && (currentTrigger != previousTrigger)) {
            previousTrigger = currentTrigger;
            release();
        }

        advanceEnvelope();

        input = inputs.get(0).read();

        if (input != 0)
            outputs.get(0).write(currentAmplitude * input);
    }

    private void checkParameters() {
        if (currentAttackSpeed != attackSpeed) {
            changeParameters(State.ATTACK, attackSpeed);
            currentAttackSpeed = attackSpeed;
        }

        if (currentDecaySpeed != decaySpeed) {
            changeParameters(State.DECAY, decaySpeed);
            currentDecaySpeed = decaySpeed;
        }

        if (currentSustainSpeed != sustainSpeed) {
            changeParameters(State.SUSTAIN, sustainSpeed);
            currentSustainSpeed = sustainSpeed;
        }

        if (currentReleaseSpeed != releaseSpeed) {
            changeParameters(State.RELEASE, releaseSpeed);
            currentReleaseSpeed = releaseSpeed;
        }
    }

    private void changeParameters(State state, int speed) {
        final int stateId = stateId(state);
        size[stateId] = (int) (SPEED_TABLE[speed] * Instrument.SAMPLE_RATE);
        factor[stateId] = 1d / size[stateId];
    }

    private void advanceEnvelope() {
        switch (state) {
            case ATTACK:
                if (applyEnvelope(State.ATTACK)) {
                    position = 0;
                    progress = 0;

                    startAmplitude = LEVEL_TABLE[attackLevel];
                    endAmplitude = LEVEL_TABLE[decayLevel];

                    state = State.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(State.DECAY)) {
                    position = 0;
                    progress = 0;

                    startAmplitude = LEVEL_TABLE[decayLevel];
                    endAmplitude = LEVEL_TABLE[sustainLevel];

                    state = State.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (applyEnvelope(State.SUSTAIN)) {
                    state = State.HOLD;
                }
                break;

            case HOLD:
                // do noting
                break;

            case RELEASE:
                if (applyEnvelope(State.RELEASE)) {
                    silence();
                }
                break;

            case PRE_IDLE:
                if (applyEnvelope(State.PRE_IDLE)) {
                    reset();
                }
                break;

            case IDLE:
                return; // do nothing
        }

        int stateId = stateId(state);
        if (stateId <= 4) {
            position += 1;
            progress += factor[stateId];
        }
    }

    private boolean applyEnvelope(State state) {
        if (position < size[stateId(state)]) {
            currentAmplitude = MathFunctions.linearInterpolation(startAmplitude, endAmplitude, progress);

            return false;
        }

        return true;
    }

    private void release() {
        if (state != State.RELEASE) {
            position = 0;
            progress = 0;

            startAmplitude = currentAmplitude;
            endAmplitude = LEVEL_TABLE[releaseLevel];

            state = State.RELEASE;
        }
    }

    private void reset() {
        if (state != State.IDLE) {
            position = 0;
            progress = 0;

            startAmplitude = 0;
            endAmplitude = 0;
            currentAmplitude = 0;

            state = State.IDLE;
        }
    }

    private void silence() {
        if (state != State.PRE_IDLE) {
            position = 0;
            progress = 0;

            startAmplitude = currentAmplitude;
            endAmplitude = 0;

            state = State.PRE_IDLE;
        }
    }

    private void initialize() {
        checkParameters();

        position = 0;
        progress = 0;

        startAmplitude = 0;
        currentAmplitude = 0;
        endAmplitude = LEVEL_TABLE[attackLevel];

        state = State.ATTACK;
    }

    private int stateId(State state) {
        switch (state) {
            case ATTACK:
                return 0;
            case DECAY:
                return 1;
            case SUSTAIN:
                return 2;
            case RELEASE:
                return 3;
            case PRE_IDLE:
                return 4;
            case HOLD:
                return 5;
            case IDLE:
                return 6;
            default:
                return -1;
        }
    }


    public void setAttackLevel(double attackLevel) {
        this.attackLevel = (int) attackLevel;
    }

    public void setDecayLevel(double decayLevel) {
        this.decayLevel = (int) decayLevel;
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = (int) sustainLevel;
    }

    public void setReleaseLevel(double releaseLevel) {
        this.releaseLevel = (int) releaseLevel;
    }


    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = (int) attackSpeed;
    }

    public void setDecaySpeed(double decaySpeed) {
        this.decaySpeed = (int) decaySpeed;
    }

    public void setSustainSpeed(double sustainSpeed) {
        this.sustainSpeed = (int) sustainSpeed;
    }

    public void setReleaseSpeed(double releaseSpeed) {
        this.releaseSpeed = (int) releaseSpeed;
    }

}
