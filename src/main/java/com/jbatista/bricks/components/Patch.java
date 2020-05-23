package com.jbatista.bricks.components;

public class Patch {

    private Connector inputConnector;
    private Connector outputConnector;

    public Connector getInputConnector() {
        return inputConnector;
    }

    public void setInputConnector(Connector inputConnector) {
        this.inputConnector = inputConnector;
    }

    public Connector getOutputConnector() {
        return outputConnector;
    }

    public void setOutputConnector(Connector outputConnector) {
        this.outputConnector = outputConnector;
    }

    void passData(){
        outputConnector.setInput(inputConnector.getOutput());
    }

}
