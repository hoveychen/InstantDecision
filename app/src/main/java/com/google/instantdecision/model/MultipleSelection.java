package com.google.instantdecision.model;

import com.google.instantdecision.model.Option;
import com.google.instantdecision.model.Selection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyuheng on 12/16/14.
 */
public class MultipleSelection implements Selection {

    private ArrayList<Option> options;

    public MultipleSelection() {
        this.options = new ArrayList<Option>();
    }

    @Override
    public void addSelection(Option option) {
        options.add(option);
    }

    @Override
    public List<Option> getSelections() {
        return this.options;
    }
}
