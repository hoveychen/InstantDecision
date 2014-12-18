package com.google.instantdecision;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import com.google.instantdecision.fragment.ConfigureVoteFragment;
import com.google.instantdecision.fragment.VoteStatusFragment;
import com.google.instantdecision.fragment.VotingFragment;
import com.google.instantdecision.model.Identifier;
import com.google.instantdecision.model.Option;
import com.google.instantdecision.model.ServerInteraction;
import com.google.instantdecision.model.Ticket;
import com.google.instantdecision.model.Vote;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by chenyuheng on 12/17/14.
 */
public class Utility {
    private static Utility ourInstance = new Utility();

    private ArrayList<Vote> votes;
    private FragmentActivity activity;

    private Utility() {
    }

    public static Utility getInstance() {
        return ourInstance;
    }


    public String getUserId() {
        String userName = getUserName();
        if (!"NotFound".equals(userName)) {
            return userName;
        }

        String defaultValue = activity.getResources().getString(R.string.user_id);
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        String userId = sharedPref.getString(activity.getString(R.string.user_id), defaultValue);

        if (userId.equals(defaultValue)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            TelephonyManager tm = (TelephonyManager) activity.getBaseContext().getSystemService(
                    Context.TELEPHONY_SERVICE);

            userId = tm.getDeviceId();

            editor.putString(activity.getString(R.string.user_id), userId);
            editor.commit();
        }

        return userId;
    }

    public String getUserName() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(activity.getBaseContext()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }
        return "NotFound";
    }

    public Identifier getOwnIdentifier() {
        Identifier identifier = new Identifier();
        identifier.setDeviceId(getUserId());
        identifier.setName(getUserName());
        return identifier;
    }


    public void refreshVotes() {
        if (votes == null) {
            votes = createMockVotes();
        }
    }

    public ArrayList<Vote> getVotes() {
        return ServerInteraction.getVotes();
    }

    public ArrayList<Vote> createMockVotes() {
        ArrayList<Vote> votes = new ArrayList<>();
        votes.add(createMockVote("today", true, true));
        votes.add(createMockVote("tomorrow", false, true));
        votes.add(createMockVote("yesterday", true, false));
        return votes;
    }

    public Vote createMockVote(String title, boolean multiSelect, boolean active) {
        Vote vote = new Vote(Long.toString(new Random().nextLong()));
        vote.setNumTicket(10);
        vote.setTitle(title);
        vote.setMultiSelect(multiSelect);
        vote.setActive(active);

        Option option1 = createOption("Eat dinner");
        Option option2 = createOption("Don't eat dinner");
        Option option3 = createOption("No idea");

        vote.getOptions().add(option1);
        vote.getOptions().add(option2);
        vote.getOptions().add(option3);

        vote.getTickets().add(createMockTicket("Yuheng", option1));
        vote.getTickets().add(createMockTicket("Xiaoqin", option1));
        vote.getTickets().add(createMockTicket("zZb", option2));
        vote.getTickets().add(createMockTicket("Zhiyuwang", option3));
        return vote;
    }

    public Option createOption(String title) {
        Option option = new Option();

        option.setTitle(title);
        return option;
    }

    public Ticket createMockTicket(String name, Option selected) {
        Ticket ticket = new Ticket(false);
        Identifier identifier = new Identifier();
        identifier.setName(name);
        identifier.setDeviceId(name);
        ticket.setIdentifier(identifier);
        ticket.getSelection().addSelection(selected);
        return ticket;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void navigateToFragmentWithBackStack(Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, fragment)
                .commit();
    }

    public void alertWarning(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    public Ticket getMyOwnTicket(Vote vote) {
        Identifier myId = getOwnIdentifier();
        for (Ticket ticket : vote.getTickets()) {
            if (ticket.getIdentifier().equals(myId)) {
                return ticket;
            }
        }
        return null;
    }

    public void navigateToVote(Vote vote) {
        if (vote.isFinished() || getMyOwnTicket(vote) != null) {
            Utility.getInstance().navigateToFragmentWithBackStack(
                    VoteStatusFragment.newInstance(vote));
        } else {
            Utility.getInstance().navigateToFragmentWithBackStack(
                    VotingFragment.newInstance(vote));
        }
    }

    public void startNewVote() {
        Vote newVote = new Vote(Long.toString(new Random().nextLong()));
        newVote.setTitle("");
        newVote.setNumTicket(3);
        newVote.setMultiSelect(false);
        newVote.setActive(true);
        Option defaultOption = new Option();
        defaultOption.setTitle("");
        newVote.getOptions().add(defaultOption);
        newVote.setCreator(getOwnIdentifier());
        votes.add(newVote);
        navigateToFragmentWithBackStack(ConfigureVoteFragment.newInstance(newVote));
    }
}
