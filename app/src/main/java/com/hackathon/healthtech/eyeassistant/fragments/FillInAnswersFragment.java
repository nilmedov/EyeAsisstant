package com.hackathon.healthtech.eyeassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Answer;
import com.hackathon.healthtech.eyeassistant.entities.Question;

public class FillInAnswersFragment extends Fragment implements View.OnClickListener {
    private Question mQuestion;
    private EditText fAnswer1, fAnswer2, fAnswer3, fAnswer4;


    private OnFragmentInteractionListener mListener;

    public FillInAnswersFragment() {
        // Required empty public constructor
    }

    public static FillInAnswersFragment newInstance() {
        FillInAnswersFragment fragment = new FillInAnswersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestion = getArguments().getParcelable(Question.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fill_in_answers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fAnswer1 = (EditText) view.findViewById(R.id.f_answer_1);
        fAnswer2 = (EditText) view.findViewById(R.id.f_answer_2);
        fAnswer3 = (EditText) view.findViewById(R.id.f_answer_3);
        fAnswer4 = (EditText) view.findViewById(R.id.f_answer_4);

        view.findViewById(R.id.btn_next).setOnClickListener(this);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onNextPressed() {
        if (mListener != null) {
           Answer[] answers = new Answer[]{
                   new Answer(fAnswer1.getText().toString()),
                   new Answer(fAnswer2.getText().toString()),
                   new Answer(fAnswer3.getText().toString()),
                   new Answer(fAnswer4.getText().toString())
           };
            mListener.onAnswersAsked(answers);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        onNextPressed();
    }

    public interface OnFragmentInteractionListener {
        void onAnswersAsked(Answer[] answers);
    }
}
