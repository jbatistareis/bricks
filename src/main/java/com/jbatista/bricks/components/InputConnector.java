package com.jbatista.bricks.components;

public class InputConnector extends Connector {

    public InputConnector(String name, String description) {
        super(name, description);
    }

    @Override
    public void connectPatch(Patch patch) {
        patch.outputConnector = this;
        this.outputPatch = patch;
        this.connected = true;
    }

    @Override
    public void disconnectPatch() {
        this.outputPatch.outputConnector = null;
        this.outputPatch = null;
        this.connected = false;
    }

}
