package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.KeyboardNote;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.OutputConnector;

public class Keyboard extends CommonModule {

    private int index;
    private int indexPr;
    private int indexRl;
    private int polyphony;
    private KeyboardNote[] pressedNotes = new KeyboardNote[6];

    private int freeSlot = 0;

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
        for (index = 0; index < 6; index++)
            outputs.get(index).write(pressedNotes[index].getFrequency());
    }

    private void setPolyphony(double value) {
        polyphony = (int) (value - 1);

        for (indexRl = 0; indexRl < 6; indexRl++)
            pressedNotes[indexRl] = KeyboardNote.DUMMY;
    }

    public synchronized void pressKey(KeyboardNote note) {
        if (polyphony == 0)
            pressedNotes[0] = note;
        else
            for (indexPr = 0; indexPr <= polyphony; indexPr++)
                if (pressedNotes[indexPr] == KeyboardNote.DUMMY) {
                    pressedNotes[indexPr] = note;
                    break;
                }
    }

    public synchronized void releaseKey(KeyboardNote note) {
        if (polyphony == 0)
            pressedNotes[0] = KeyboardNote.DUMMY;
        else
            for (indexRl = 0; indexRl <= polyphony; indexRl++)
                if (pressedNotes[indexRl] == note) {
                    pressedNotes[indexRl] = KeyboardNote.DUMMY;
                    break;
                }
    }

}
