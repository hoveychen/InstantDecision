package com.google.instantdecision.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.instantdecision.R;
import com.google.instantdecision.Utility;
import com.google.instantdecision.model.Identifier;
import com.google.instantdecision.model.Option;
import com.google.instantdecision.model.Ticket;
import com.google.instantdecision.model.Vote;


public class VotingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "voteId";

    private Vote vote;
    private Ticket ticket;

    public VotingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vote The vote to configure.
     * @return A new instance of fragment NewVote.
     */

    public static VotingFragment newInstance(Vote vote) {
        VotingFragment fragment = new VotingFragment();
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
        ticket = new Ticket(vote.isMultiSelect());
        ticket.setIdentifier(Utility.getInstance().getOwnIdentifier());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = (TextView) view.findViewById(R.id.titleTextView);
        titleView.setText(vote.getTitle());

        TextView multiSelectView = (TextView) view.findViewById(R.id.multiSelectTextView);
        multiSelectView.setText(vote.isMultiSelect() ? "MultiChoice" : "SingleChoice");

        final ListView optionListView = (ListView) view.findViewById(R.id.optionListView);
        if (vote.isMultiSelect()) {
            optionListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            optionListView.setAdapter(new ArrayAdapter<Option>(getActivity(),
                    android.R.layout.simple_list_item_multiple_choice, vote.getOptions()));
        } else {
            optionListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            optionListView.setAdapter(new ArrayAdapter<Option>(getActivity(),
                    android.R.layout.simple_list_item_single_choice, vote.getOptions()));
        }

        Button submitBtnView = (Button) view.findViewById(R.id.submitBtn);
        submitBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionListView.getCheckedItemCount() == 0) {
                    Utility.getInstance().alertWarning(
                            getActivity().getString(R.string.at_least_one_option_warning));
                } else {
                    SparseBooleanArray checkedItemPositions =
                            optionListView.getCheckedItemPositions();
                    for (int idx = 0; idx < checkedItemPositions.size(); idx++) {
                        if (checkedItemPositions.valueAt(idx)) {
                            ticket.getSelection().addSelection(vote.getOptions().get(idx));
                        }
                    }
                    vote.getTickets().add(ticket);
                    Utility.getInstance().navigateToFragment(VoteStatusFragment.newInstance(vote));
                }
            }
        });
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
