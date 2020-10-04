package com.jbatista.bricks.components;

import com.jbatista.bricks.Instrument;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonModule {

    private final Instrument instrument;

    protected String name;
    protected String description;

    protected final List<InputConnector> inputs = new ArrayList<>();
    protected final List<OutputConnector> outputs = new ArrayList<>();
    protected final List<Controller> controllers = new ArrayList<>();

    public CommonModule(Instrument instrument) {
        this.instrument = instrument;
        this.instrument.addModule(this);
    }

    public void remove() {
        inputs.forEach(input -> input.disconnectPatch());
        outputs.forEach(output -> output.disconnectPatch());
        instrument.removeModule(this);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public InputConnector getInput(int index) {
        return inputs.get(index);
    }

    public List<InputConnector> getInputs() {
        return inputs;
    }

    public OutputConnector getOutput(int index) {
        return outputs.get(index);
    }

    public List<OutputConnector> getOutputs() {
        return outputs;
    }

    public Controller getController(int index) {
        return controllers.get(index);
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public abstract void process();

}
