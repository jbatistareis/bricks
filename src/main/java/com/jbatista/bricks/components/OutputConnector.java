package com.jbatista.bricks.components;

public class OutputConnector extends Connector {

    public OutputConnector(String name, String description) {
        super(name, description);
    }

    @Override
    public void connectPatch(Patch patch) {
        patch.inputConnector = this;
        this.inputPatch = patch;
        this.connected = true;
    }

    @Override
    public void disconnectPatch() {
        this.inputPatch.inputConnector = null;
        this.inputPatch = null;
        this.connected = false;
    }

}
