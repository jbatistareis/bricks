package com.jbatista.bricks.filter;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.util.MathFunctions;

/**
 * <p>Based on the <i>'Cookbook formulae for audio equalizer biquad filter coefficients'</i> by Robert Bristow-Johnson.</p>
 *
 * @see <a href="https://www.w3.org/2011/audio/audio-eq-cookbook.html">Audio EQ Cookbook</a>
 */
public class LowPass extends Biquad {

    public LowPass() {
        setCutoffFrequency(440);
        setResonance(1);
    }

    public void setCutoffFrequency(double cutoffFrequency) {
        this.frequency = cutoffFrequency;
        calculateCoefficients();
    }

    public double getCutoffFrequency() {
        return frequency;
    }

    public void setResonance(double resonance) {
        this.q = resonance;
        calculateCoefficients();
    }

    public double getResonance() {
        return q;
    }

    @Override
    protected void calculateCoefficients() {
        omega = MathFunctions.TAU * frequency / Instrument.SAMPLE_RATE;
        sin = Math.sin(omega);
        cos = Math.cos(omega);
        alpha = sin / (2 * q);

        cB0 = (1 - cos) / 2;
        cB1 = 1 - cos;
        cB2 = (1 - cos) / 2;
        cA0 = 1 + alpha;
        cA1 = -2 * cos;
        cA2 = 1 - alpha;

        normalize();
    }

}
