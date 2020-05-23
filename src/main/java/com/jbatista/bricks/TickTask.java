package com.jbatista.bricks;

import java.util.TimerTask;

class TickTask extends TimerTask {

    private int index;

    @Override
    public void run() {
        for (index = 0; index < Clock.MODULES_SIZE; index++) {
            Clock.MODULES.get(index).process();
        }

        for (index = 0; index < Clock.PATCHES_SIZE; index++) {
            Clock.PATCHES.get(index).passData();
        }
    }

}
