package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.KeyboardNote;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.OutputConnector;

public class Keyboard extends CommonModule {

    private int indexPr;
    private int indexRl;
    private int polyphony;
    private KeyboardNote[] pressedNotes = new KeyboardNote[6];

    public Keyboard(Instrument instrument) {
        super(instrument);

        name = "Keyboard";
        for (int i = 0; i < 6; i++) {
            pressedNotes[i] = KeyboardNote.DUMMY;
            outputs.add(new OutputConnector("Poly " + (i + 1), "Frequency output"));
        }

        controllers.add(new Controller(
                "Polyphony", "Sets the polyphony level",
                1, 6, 1, 1, Controller.Curve.ORIGINAL,
                this::setPolyphony, 1, 2, 3, 4, 5, 6));
    }

    @Override
    public void process() {
        // not used
    }

    private void setPolyphony(double value) {
        polyphony = (int) (value - 1);

        for (indexRl = 0; indexRl < 6; indexRl++) {
            pressedNotes[indexRl] = KeyboardNote.DUMMY;
            outputs.get(indexPr).write(KeyboardNote.DUMMY.getFrequency());
        }
    }

    public synchronized void pressKey(KeyboardNote note) {
        for (indexPr = 0; indexPr <= polyphony; indexPr++) {
            if (pressedNotes[indexPr] == KeyboardNote.DUMMY) {
                pressedNotes[indexPr] = note;
                outputs.get(indexPr).write(note.getFrequency());
            }
        }
    }

    public synchronized void releaseKey(KeyboardNote note) {
        for (indexRl = 0; indexRl < 6; indexRl++) {
            if (pressedNotes[indexRl] == note) {
                pressedNotes[indexRl] = KeyboardNote.DUMMY;
                outputs.get(indexRl).write(0);
                break;
            }
        }
    }

}
