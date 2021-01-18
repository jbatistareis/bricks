package com.jbatista.bricks.util;

public class GeneralPurposeOscillator {

    private double sampleRate = 1;
    private double frequency = 1;
    private double period = 1;

    private int periodAccumulator = 0;

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
        period = this.frequency / sampleRate;
        reset();
    }

    public void reset() {
        periodAccumulator = 0;
    }

    public double sine(double modulation) {
        return Math.sin(phase() + modulation);
    }

    public double square(double modulation) {
        return Math.signum(sine(modulation));
    }

    public double triangle(double modulation) {
        return Math.abs(((phase() + modulation) % 6) - 3) * 0.6668 - 1;
    }

    public double sawUp(double modulation) {
        return (((phase() + modulation) % MathFunctions.TAU) / MathFunctions.PI) - 1;
    }

    public double sawDown(double modulation) {
        return -(((phase() + modulation) % MathFunctions.TAU) / MathFunctions.PI) + 1;
    }

    public double whiteNoise() {
        return 2 * MathFunctions.RANDOM.nextDouble() - 1;
    }

    private double phase() {
        return MathFunctions.TAU * period * periodAccumulator++;
    }

}
