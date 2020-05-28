package com.jbatista.bricks.components;

import com.jbatista.bricks.util.Filter;

public abstract class FilterModule extends CommonModule {

    protected Filter filter;

    public FilterModule() {
        inputs.add(new InputConnector("In", "Receives a signal"));
        outputs.add(new OutputConnector("Out", "Returns the altered signal"));
    }

    @Override
    public void process() {
        outputs.get(0).write(filter.apply(inputs.get(0).read()));
    }

}
