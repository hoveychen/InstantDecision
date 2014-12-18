package com.google.instantdecision.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.instantdecision.R;
import com.google.instantdecision.Utility;
import com.google.instantdecision.model.Vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListVotesFragment extends Fragment {

    public static final String FILTER_ALL = "all";
    public static final String FILTER_ACTIVE = "active";
    public static final String FILTER_CLOSED = "closed";
    private static final String KEY_FILTER = "filter";
    private static String KEY_VOTE_TITLE = "titleTextView";
    private static String KEY_VOTE_NUM_TICKET = "numTicketTextView";
    private static String KEY_VOTE_ACTIVE = "activeTextView";
    private ListView voteListView;
    private TextView welcomeView;
    private String filter;

    public ListVotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListVotesFragment.
     */
    public static ListVotesFragment newInstance(String filter) {
        ListVotesFragment fragment = new ListVotesFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = getArguments().getString(KEY_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_votes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voteListView = (ListView) (view.findViewById(R.id.vote_list_view));
        String[] keys = new String[]{
                KEY_VOTE_TITLE, KEY_VOTE_ACTIVE, KEY_VOTE_NUM_TICKET
        };
        int[] resIds = new int[]{
                R.id.titleTextView, R.id.activeTextView, R.id.numTicketTextView
        };

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getVoteListMap(),
                R.layout.listviewitem_vote, keys, resIds);
        voteListView.setAdapter(adapter);
        voteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utility.getInstance().navigateToVote(Utility.getInstance().getVotes().get(position));
            }
        });

        welcomeView = (TextView) (view.findViewById(R.id.welcomeUserTextView));
        welcomeView.setText(Utility.getInstance().getOwnIdentifier().getName());

        Button newVoteBtn = (Button) view.findViewById(R.id.newVoteBtn);
        newVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.getInstance().startNewVote();
            }
        });

    }

    private ArrayList<Map<String, String>> getVoteListMap() {
        ArrayList<Map<String, String>> binds = new ArrayList<>();
        for (Vote vote : Utility.getInstance().getVotes()) {
            if (filter.equals(FILTER_ACTIVE) && vote.isFinished()) {
                continue;
            } else if (filter.equals(FILTER_CLOSED) && !vote.isFinished()) {
                continue;
            }

            HashMap<String, String> viewBind = new HashMap<>();
            viewBind.put(KEY_VOTE_ACTIVE, vote.isActive() ? "Active" : "Closed");
            viewBind.put(KEY_VOTE_NUM_TICKET,
                    Integer.toString(vote.getTickets().size()) + "/"
                            + Integer.toString(vote.getNumTicket()));
            viewBind.put(KEY_VOTE_TITLE, vote.getTitle());
            binds.add(viewBind);
        }
        return binds;
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
