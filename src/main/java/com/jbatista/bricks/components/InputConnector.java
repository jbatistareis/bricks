package com.jbatista.bricks.components;

public class InputConnector extends Connector {

    public InputConnector(String name, String description) {
        super(name, description);
    }

    @Override
    public void connectPatch(Patch patch) {
        this.outputPatch = patch;
        this.outputPatch.outputConnector = this;
        this.connected = true;
    }

    @Override
    public void disconnectPatch() {
        if (this.outputPatch != null) {
            this.outputPatch.outputConnector = null;
            this.outputPatch = null;
            this.connected = false;
        }
    }

}
