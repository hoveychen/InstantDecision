package com.google.instantdecision.model;

/**
 * Created by chenyuheng on 12/16/14.
 */
public class Ticket {
    private Identifier identifier = new Identifier();
    private Selection selection;

    public Ticket(boolean multiSelect) {
        if (multiSelect) {
            selection = new MultipleSelection();
        } else {
            selection = new SingleSelection();
        }
    }

    public Selection getSelection() {
        return selection;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }
}
