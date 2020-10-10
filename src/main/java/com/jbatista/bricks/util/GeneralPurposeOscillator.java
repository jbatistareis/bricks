package com.jbatista.bricks.util;

public class GeneralPurposeOscillator {

    private double sampleRate = 1;
    private double frequency = 1;
    private double frequencyPeriod;

    private int period;
    private int periodPhase;
    private int periodAccumulator = 0;
    private double periodValue;

    private double sawIncrement;
    private double triangleIncrement;

    public GeneralPurposeOscillator() {
        setFrequency(1);
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;

        frequencyPeriod = this.frequency / sampleRate;
        period = (int) (sampleRate / this.frequency);
        periodPhase = period / 2;

        sawIncrement = 2d / period;
        triangleIncrement = 2d / periodPhase;
    }

    public double getPeriodValue() {
        return periodValue;
    }

    public void reset() {
        periodAccumulator = 0;
    }

    public void sine() {
        periodValue = Math.sin(MathFunctions.TAU * frequencyPeriod * periodAccumulator);
    }

    public void square() {
        periodValue = (periodAccumulator < periodPhase) ? 1 : -1;
    }

    public void triangle() {
        periodValue = (periodAccumulator == 0) ? -1 : (periodAccumulator < periodPhase) ? (periodValue + triangleIncrement) : (periodValue - triangleIncrement);
    }

    public void sawUp() {
        periodValue = (periodAccumulator == 0) ? -1 : (periodValue + sawIncrement);
    }

    public void sawDown() {
        periodValue = (periodAccumulator == 0) ? 1 : (periodValue - sawIncrement);
    }

    public void whiteNoise() {
        periodValue = 2 * MathFunctions.RANDOM.nextDouble() - 1;
    }

    public void advancePeriod() {
        periodAccumulator = (periodAccumulator < period) ? (periodAccumulator + 1) : 0;
    }

}
