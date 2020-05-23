package com.jbatista.bricks.components;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    protected String name;
    protected String description;

    protected final List<Connector> inputs = new ArrayList<>();
    protected final List<Connector> outputs = new ArrayList<>();
    protected final List<Controler> controllers = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Connector getInputs(int index) {
        return inputs.get(index);
    }

    public Connector getOutputs(int index) {
        return outputs.get(index);
    }

    public Controler getControllers(int index) {
        return controllers.get(index);
    }

    public abstract void process();

}
