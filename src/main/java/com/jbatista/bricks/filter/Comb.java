package com.jbatista.bricks.filter;

import com.jbatista.bricks.Instrument;

/**
 * <p>Based on the <i>'Physical Audio Signal Processing: For Virtual Musical Instruments and Audio Effects'</i> by Julius Orion Smith.</p>
 *
 * @see <a href="https://ccrma.stanford.edu/~jos/pasp/Example_Tapped_Delay_Line.html">Example Tapped Delay Line</a>
 * @see <a href="https://ccrma.stanford.edu/~jos/pasp/Feedforward_Comb_Filters.html">Feedforward Comb Filters</a>
 */
public class Comb implements Filter {

    private double duration;
    private int taps;

    private double y;
    private int samples = 0;
    private final double[] bCoeffs = new double[20];
    private final double[] buffer = new double[882000];
    private final int cursors[] = new int[21];

    public Comb() {
        setDuration(0.01);
        setTaps(1);
    }

    public double getDuration() {
        return duration;
    }

    /**
     * Defines how long the delay is going to last
     *
     * @param duration Duration in percentage of a second, from 0.01 to 1.0
     */
    public void setDuration(double duration) {
        this.duration = Math.max(0.01, Math.min(duration, 1));
        this.samples = (int) (Instrument.SAMPLE_RATE * this.duration);
    }

    public int getTaps() {
        return taps;
    }

    /**
     * Defines how many "echoes" are going to be played, higher numbers produce more attenuated echoes
     *
     * @param taps The number of delay iterations are going to be performed, from 1 to 20
     */
    public void setTaps(double taps) {
        this.taps = (int) Math.max(1, Math.min(taps, 20));

        for (int i = 0; i < this.taps; i++) {
            bCoeffs[i] = 0.8 / (i + 0.3);
            cursors[i] = samples * (this.taps - i);
        }

        bCoeffs[0] = 0.8;
    }

    private void advanceCursor(int cursorIndex) {
        if (cursors[cursorIndex] >= 881999)
            cursors[cursorIndex] = 0;
        else
            cursors[cursorIndex] += 1;
    }

    @Override
    public double apply(double sample) {
        y = 0;
        buffer[cursors[0]] = sample;

        for (int i = 0; i < this.taps; i++) {
            y += buffer[cursors[i]] * bCoeffs[i];
            advanceCursor(i);
        }

        return y;
    }

}
