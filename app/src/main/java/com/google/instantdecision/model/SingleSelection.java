package com.google.instantdecision.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyuheng on 12/16/14.
 */
public class SingleSelection implements Selection {
    private Option option = null;

    @Override
    public void addSelection(Option option) {
        this.option = option;
    }

    @Override
    public List<Option> getSelections() {
        ArrayList<Option> ret = new ArrayList<>();
        ret.add(this.option);
        return ret;
    }
}
