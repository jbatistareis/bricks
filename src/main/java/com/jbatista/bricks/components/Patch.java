package com.jbatista.bricks.components;

public class Patch {

    Connector inputConnector = null;
    Connector outputConnector = null;

    public Connector getInputConnector() {
        return inputConnector;
    }

    public Connector getOutputConnector() {
        return outputConnector;
    }

    void passData(double inputData) {
        if ((inputConnector != null) && (outputConnector != null)) {
            outputConnector.write(inputData);
        }
    }

}
