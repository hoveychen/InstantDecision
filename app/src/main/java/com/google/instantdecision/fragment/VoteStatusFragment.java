package com.google.instantdecision.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.instantdecision.R;
import com.google.instantdecision.Utility;
import com.google.instantdecision.model.Option;
import com.google.instantdecision.model.Ticket;
import com.google.instantdecision.model.Vote;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class VoteStatusFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "voteId";

    private Vote vote;
    private ArrayList<View> optionItemViews = new ArrayList<>();

    public VoteStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vote The vote to configure.
     * @return A new instance of fragment NewVote.
     */

    public static VoteStatusFragment newInstance(Vote vote) {
        VoteStatusFragment fragment = new VoteStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, vote.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String voteId = getArguments().getString(ARG_PARAM1);
            for (Vote iterVote : Utility.getInstance().getVotes()) {
                if (iterVote.getId() == voteId) {
                    vote = iterVote;
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vote_status, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button closeVoteBtn = (Button) view.findViewById(R.id.closeVoteBtn);
        if (Utility.getInstance().getOwnIdentifier().equals(vote.getCreator()) && vote.isActive()) {
            closeVoteBtn.setVisibility(View.VISIBLE);
            closeVoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vote.setActive(false);
                    Utility.getInstance().navigateToFragment(VoteStatusFragment.newInstance(vote));
                }
            });
        }

        TextView titleView = (TextView) view.findViewById(R.id.titleTextView);
        titleView.setText(vote.getTitle());

        TextView activeView = (TextView) view.findViewById(R.id.activeTextView);
        activeView.setText(vote.isActive() ? "Active" : "Closed");

        TextView progressTextView = (TextView) view.findViewById(R.id.progressTextView);
        progressTextView.setText(Integer.toString(vote.getTickets().size()) + "/"
                + Integer.toString(vote.getNumTicket()));

        ProgressBar numTicketProgressBar = (ProgressBar) view.findViewById(
                R.id.numTicketProgressBar);
        numTicketProgressBar.setMax(vote.getNumTicket());
        if (vote.getTickets().size() > vote.getNumTicket()) {
            numTicketProgressBar.setProgress(vote.getNumTicket());
        } else {
            numTicketProgressBar.setProgress(vote.getTickets().size());
        }

        LinearLayout selectedOptionContainer = (LinearLayout) view.findViewById(
                R.id.selectedOptionContainerView);
        Ticket myOwnTicket = Utility.getInstance().getMyOwnTicket(vote);
        if (myOwnTicket != null) {
            for (Option option : myOwnTicket.getSelection().getSelections()) {
                TextView optionView = new TextView(getActivity());
                optionView.setText(option.getTitle());
                optionView.setTextSize(20);
                selectedOptionContainer.addView(optionView);
            }
        } else {
            TextView yourChoiceTextView = (TextView) view.findViewById(R.id.yourChoiceTextView);
            yourChoiceTextView.setText("You haven't voted yet.");
        }

        LinearLayout voteStatistic = (LinearLayout) view.findViewById(R.id.voteStatisticContainer);
        Option bestOption = null;
        int mostNumVote = 0;
        for (Map.Entry<Option, Integer> stat : vote.statisticTicket().entrySet()) {
            TextView statView = new TextView(getActivity());
            statView.setText(stat.getKey().getTitle() + ": " + stat.getValue().toString());
            statView.setTextSize(20);
            voteStatistic.addView(statView);

            if (stat.getValue() > mostNumVote) {
                bestOption = stat.getKey();
                mostNumVote = stat.getValue();
            }
        }

        if (vote.isFinished() && bestOption != null) {
            TextView winnerView = (TextView) view.findViewById(R.id.winnerTextView);
            winnerView.setText("The winner is :" + bestOption.getTitle());
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
