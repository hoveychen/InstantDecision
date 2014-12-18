package com.google.instantdecision.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.google.instantdecision.R;
import com.google.instantdecision.Utility;
import com.google.instantdecision.model.Option;
import com.google.instantdecision.model.Vote;

import java.util.ArrayList;
import java.util.Random;


public class ConfigureVoteFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "voteId";

    private Vote vote;
    private ArrayList<View> optionItemViews = new ArrayList<>();

    public ConfigureVoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vote The vote to configure.
     * @return A new instance of fragment NewVote.
     */

    public static ConfigureVoteFragment newInstance(Vote vote) {
        ConfigureVoteFragment fragment = new ConfigureVoteFragment();
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
                if (iterVote.getId().equals(voteId)) {
                    vote = iterVote;
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configure_vote, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.optionListContainer);
        EditText titleView = (EditText) view.findViewById(R.id.titleEditText);
        titleView.setText(vote.getTitle());
        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        NumberPicker numTicketView = (NumberPicker) view.findViewById(R.id.numTicketPicker);
        numTicketView.setMinValue(0);
        numTicketView.setMaxValue(100);
        numTicketView.setValue(vote.getNumTicket());
        numTicketView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                vote.setNumTicket(newVal);
            }
        });
        Button newOptionView = (Button) view.findViewById(R.id.newOptionBtn);
        newOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Option newOption = new Option();
                newOption.setTitle("");
                newOption.setId(Long.toString(new Random().nextLong()));
                vote.getOptions().add(newOption);
                createOptionItemView(container, newOption);
            }
        });

        Button submitView = (Button) view.findViewById(R.id.submitBtn);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.getInstance().navigateToVote(vote);
            }
        });

        for (Option option : vote.getOptions()) {
            createOptionItemView(container, option);
        }

        Switch multiSelectSwitch = (Switch) view.findViewById(R.id.multiSelectSwitch);
        multiSelectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vote.setMultiSelect(isChecked);
            }
        });

    }

    private void createOptionItemView(final LinearLayout container, final Option option) {

        final LinearLayout itemView = new LinearLayout(getActivity());
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(inflater);
        vi.inflate(R.layout.listviewitem_configure_option, itemView, true);

        EditText titleView = (EditText) itemView.findViewById(R.id.optionTitle);
        titleView.setText(option.getTitle());
        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                option.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button removeBtnView = (Button) itemView.findViewById(R.id.removeBtn);
        removeBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeView(itemView);
            }
        });

        container.addView(itemView);
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
