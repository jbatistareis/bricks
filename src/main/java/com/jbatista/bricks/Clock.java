package com.jbatista.bricks;

import com.jbatista.bricks.components.CommonModule;

import java.util.ArrayList;
import java.util.List;

public class Clock {

    private static int index;

    private static final List<CommonModule> MODULES = new ArrayList<>();
    private static int MODULES_SIZE = 0;

    public synchronized static void tick() {
        for (index = 0; index < MODULES_SIZE; index++) MODULES.get(index).process();
    }

    public synchronized static void addModule(CommonModule module) {
        if (!MODULES.contains(module)) {
            MODULES.add(module);
            MODULES_SIZE = MODULES.size();
        }
    }

    public synchronized static void removeModule(CommonModule module) {
        if (MODULES.contains(module)) {
            MODULES.remove(module);
            MODULES_SIZE = MODULES.size();
        }
    }

}
