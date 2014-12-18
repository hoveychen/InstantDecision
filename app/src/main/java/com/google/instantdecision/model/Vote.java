package com.google.instantdecision.model;

import com.google.instantdecision.model.Option;
import com.google.instantdecision.model.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyuheng on 12/16/14.
 */
public class Vote {

    private String id;

    private boolean active;
    private String title;
    private boolean multiSelect;
    private int numTicket;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private ArrayList<Option> options = new ArrayList<>();
    private Identifier creator;

    public Vote(String id) {
        this.id = id;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public int getNumTicket() {
        return numTicket;
    }

    public void setNumTicket(int numTicket) {
        this.numTicket = numTicket;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public boolean isFinished() {
        return !isActive() || getNumTicket() <= getTickets().size();
    }

    public Map<Option, Integer> statisticTicket() {
        HashMap<Option, Integer> stat = new HashMap<>();
        for (Ticket ticket : getTickets()) {
            for (Option option : ticket.getSelection().getSelections()) {
                if (stat.containsKey(option)) {
                    stat.put(option, stat.get(option) + 1);
                } else {
                    stat.put(option, 1);
                }
            }
        }
        return stat;
    }

    public Identifier getCreator() {
        return creator;
    }

    public void setCreator(Identifier creator) {
        this.creator = creator;
    }
}
