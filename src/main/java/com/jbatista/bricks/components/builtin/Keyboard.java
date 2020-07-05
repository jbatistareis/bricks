package com.jbatista.bricks.components.builtin;

import com.jbatista.bricks.KeyboardNote;
import com.jbatista.bricks.components.CommonModule;
import com.jbatista.bricks.components.Controller;
import com.jbatista.bricks.components.OutputConnector;

public class Keyboard extends CommonModule {

    private int indexPr;
    private int indexRl;
    private int polyphony;
    private KeyboardNote[] pressedNotes = new KeyboardNote[]{null, null, null, null, null, null};

    public Keyboard() {
        name = "Keyboard";

        outputs.add(new OutputConnector("Poly 1", "Frequency output"));
        outputs.add(new OutputConnector("Poly 2", "Frequency output"));
        outputs.add(new OutputConnector("Poly 3", "Frequency output"));
        outputs.add(new OutputConnector("Poly 4", "Frequency output"));
        outputs.add(new OutputConnector("Poly 5", "Frequency output"));
        outputs.add(new OutputConnector("Poly 6", "Frequency output"));

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
    }

    public void pressKey(KeyboardNote note) {
        for (indexPr = 0; indexPr <= polyphony; indexPr++) {
            if (pressedNotes[indexPr] == null) {
                pressedNotes[indexPr] = note;
                outputs.get(indexPr).write(note.getFrequency());
            }
        }
    }

    public void releaseKey(KeyboardNote note) {
        for (indexRl = 0; indexRl < 6; indexRl++) {
            if (pressedNotes[indexRl].getId() == note.getId()) {
                pressedNotes[indexRl] = null;
                outputs.get(indexPr).write(0);
                break;
            }
        }
    }

}
