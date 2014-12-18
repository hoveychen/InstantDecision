package com.google.instantdecision.model;

import com.google.instantdecision.model.Option;

import java.util.List;

/**
 * Created by chenyuheng on 12/16/14.
 */
public interface Selection {
    public void addSelection(Option candidate);

    public List<Option> getSelections();
}
