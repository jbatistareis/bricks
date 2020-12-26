package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;

public class MidiPlayer extends CommonModule {

    public MidiPlayer(Instrument instrument) {
        super(instrument);

        name = "MIDI Player";

        controllers.add(0, new Controller(
                "Control", "Stop / Play / Pause",
                0, 2, 1, 0, Controller.Curve.ORIGINAL,
                this::control, 0, 1, 2));

        controllers.add(1, new Controller(
                "Position", "Time position",
                0, 1, 0.001, 0, Controller.Curve.LINEAR,
                this::position));
    }

    @Override
    public void process() {

    }

    private void control(double value) {
        switch ((int) value) {
            case 0: // stop
                break;

            case 1: // play
                break;

            case 2: // pause
                break;
        }
    }

    private void position(double value) {

    }

}
