package com.jbatista.bricks.components;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    protected String name;
    protected String description;

    protected final List<InputConnector> inputs = new ArrayList<>();
    protected final List<OutputConnector> outputs = new ArrayList<>();
    protected final List<Controller> controllers = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Connector getInput(int index) {
        return inputs.get(index);
    }

    public Connector getOutput(int index) {
        return outputs.get(index);
    }

    public Controller getController(int index) {
        return controllers.get(index);
    }

    public abstract void process();

}
