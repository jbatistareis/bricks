package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.FilterModule;
import com.jbatista.bricks.filter.Comb;

public class Delay extends FilterModule {

    private final Comb comb = new Comb();

    public Delay(Instrument instrument) {
        super(instrument);

        name = "Delay";

        filter = comb;

        controllers.add(new Controller(
                "Duration", "Sets the duration for the effect",
                0.01, 1, 0.01, 0.01, Controller.Curve.ORIGINAL,
                comb::setDuration));

        controllers.add(new Controller(
                "Lines", "Sets amount of delay lines",
                1, 20, 1, 1, Controller.Curve.ORIGINAL,
                comb::setTaps,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
    }

}
