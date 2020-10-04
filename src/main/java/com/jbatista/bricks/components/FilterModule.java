package com.jbatista.bricks.components;

import com.jbatista.bricks.Instrument;
import com.jbatista.bricks.filter.Filter;

public abstract class FilterModule extends CommonModule {

    protected Filter filter;

    public FilterModule(Instrument instrument) {
        super(instrument);

        inputs.add(new InputConnector("In", "Receives a signal"));
        outputs.add(new OutputConnector("Out", "Returns the altered signal"));
    }

    @Override
    public void process() {
        outputs.get(0).write(filter.apply(inputs.get(0).read()));
    }

}
