package com.jbatista.bricks.filter;

public interface Filter {

    double sampleRate = 44100;

    double apply(double sample);

}
