package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Clock;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.InputConnector;
import com.jbatista.bricks.components.OutputConnector;
import com.jbatista.bricks.util.MathFunctions;

public class EnvelopeGenerator extends CommonModule {

    private enum State {ATTACK, DECAY, SUSTAIN, RELEASE, PRE_IDLE, HOLD, IDLE}

    private int sampleRate;

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

    private static double[] LEVEL_TABLE = new double[128];
    private static double[] SPEED_TABLE = new double[128];
    private static int[] CONTROL_LEVELS = new int[128];

    static {
        double index = 0;
        for (int i = 0; i < 128; i++) {
            LEVEL_TABLE[i] = MathFunctions.expIncreaseInterpolation(0, 1, index, 5);
            SPEED_TABLE[i] = MathFunctions.expDecreaseInterpolation(30, 0.0001, index, 5);
            CONTROL_LEVELS[i] = i;
            index += 0.0078125;
        }
    }

    public EnvelopeGenerator() {
        inputs.add(new InputConnector("In", "The sound signal that will receive AM"));
        inputs.add(new InputConnector("Trigger", "Start/stop signal"));

        outputs.add(new OutputConnector("Out", "The modified signal"));

        controllers.add(new Controller(
                "Atk. Lvl.", "Attack amplitude level",
                0, 127, 127, Controller.Curve.ORIGINAL,
                this::setAttackLevel, CONTROL_LEVELS));

        controllers.add(new Controller(
                "Dec. Lvl.", "Decay amplitude level",
                0, 127, 64, Controller.Curve.ORIGINAL,
                this::setDecayLevel, CONTROL_LEVELS));

        controllers.add(new Controller(
                "Sus. Lvl.", "Sustain amplitude level",
                0, 127, 64, Controller.Curve.ORIGINAL,
                this::setSustainLevel, CONTROL_LEVELS));

        controllers.add(new Controller(
                "Rel. Lvl.", "Release amplitude level",
                0, 127, 0, Controller.Curve.ORIGINAL,
                this::setReleaseLevel, CONTROL_LEVELS));


        controllers.add(new Controller(
                "Atk. Spd.", "Attack speed",
                0, 127, 64, Controller.Curve.ORIGINAL,
                this::setAttackSpeed, CONTROL_LEVELS));

        controllers.add(new Controller(
                "Dec. Spd.", "Decay speed",
                0, 127, 64, Controller.Curve.ORIGINAL,
                this::setDecaySpeed, CONTROL_LEVELS));

        controllers.add(new Controller(
                "Sus. Spd.", "Sustain speed",
                0, 127, 64, Controller.Curve.ORIGINAL,
                this::setSustainSpeed, CONTROL_LEVELS));

        controllers.add(new Controller(
                "Rel. Spd.", "Release speed",
                0, 127, 64, Controller.Curve.ORIGINAL,
                this::setReleaseSpeed, CONTROL_LEVELS));
    }

    @Override
    public void process() {
        currentTrigger = inputs.get(0).read();

        if ((currentTrigger > 0) && (currentTrigger != previousTrigger)) {
            previousTrigger = currentTrigger;

            checkParameters();
            initialize();
        } else if ((currentTrigger > 0) && (currentTrigger == previousTrigger)) {
            advanceEnvelope();
        } else if ((currentTrigger == 0) && (currentTrigger != previousTrigger)) {
            previousTrigger = currentTrigger;
            stop();
        }

        getOutput(0).write(currentAmplitude * getInput(0).read());
    }

    private void checkParameters() {
        if (Clock.getSampleRate() != sampleRate) {
            sampleRate = Clock.getSampleRate();

            int stateId = stateId(State.PRE_IDLE);
            size[stateId] = sampleRate / 3;
            factor[stateId] = 1d / size[stateId];
        }

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
        int stateId = stateId(state);
        size[stateId] = (int) (SPEED_TABLE[speed] * sampleRate);
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
                // do nothing
                break;
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
        } else {
            return true;
        }
    }

    private void stop() {
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
