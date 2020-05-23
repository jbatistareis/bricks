package com.jbatista.bricks.components;

import java.util.LinkedList;

public abstract class Module {

    protected String name;
    protected String description;

    protected final LinkedList<Connector> inputs = new LinkedList<>();
    protected final LinkedList<Connector> outputs = new LinkedList<>();
    protected final LinkedList<Controler> controllers = new LinkedList<>();

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
