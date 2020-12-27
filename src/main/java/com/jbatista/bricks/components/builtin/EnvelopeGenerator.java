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
            LEVEL_TABLE[i] = MathFunctions.linearInterpolation(0.0025, 1, index);
            SPEED_TABLE[i] = MathFunctions.linearInterpolation(2, 0.0025, index);
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

        size[State.PRE_IDLE.getId()] = (int) (Instrument.SAMPLE_RATE / 120);
        factor[State.PRE_IDLE.getId()] = 1d / size[State.PRE_IDLE.getId()];
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
        size[state.getId()] = (int) Math.max(
                size[State.PRE_IDLE.getId()],
                SPEED_TABLE[speed] * Instrument.SAMPLE_RATE);
        factor[state.getId()] = 1d / size[state.getId()];
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
