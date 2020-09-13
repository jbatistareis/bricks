package com.jbatista.bricks.components;

public class OutputConnector extends Connector {

    public OutputConnector(String name, String description) {
        super(name, description);
    }

    @Override
    public void connectPatch(Patch patch) {
        this.inputPatch = patch;
        this.inputPatch.inputConnector = this;
        this.connected = true;
    }

    @Override
    public void disconnectPatch() {
        if (this.inputPatch != null) {
            this.inputPatch.inputConnector = null;
            this.inputPatch = null;
            this.connected = false;
        }
    }

}
