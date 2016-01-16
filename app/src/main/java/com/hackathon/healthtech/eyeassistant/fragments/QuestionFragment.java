package com.hackathon.healthtech.eyeassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Answer;
import com.hackathon.healthtech.eyeassistant.entities.Question;

public class QuestionFragment extends Fragment implements View.OnClickListener {
    private Question mQuestion;
    private TextView txtMessage;
    private TextView txtAnswer1, txtAnswer2, txtAnswer3, txtAnswer4;


    private OnFragmentInteractionListener mListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(Question question) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable(Question.class.getSimpleName(), question);
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
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtMessage = (TextView) view.findViewById(R.id.txt_message);
        (txtAnswer1 = (TextView) view.findViewById(R.id.txt_answer_1)).setOnClickListener(this);
        (txtAnswer2 = (TextView) view.findViewById(R.id.txt_answer_2)).setOnClickListener(this);
        (txtAnswer3 = (TextView) view.findViewById(R.id.txt_answer_3)).setOnClickListener(this);
        (txtAnswer4 = (TextView) view.findViewById(R.id.txt_answer_4)).setOnClickListener(this);
        if (mQuestion != null && !TextUtils.isEmpty(mQuestion.getQuestion()))
            txtMessage.setText(mQuestion.getQuestion());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int position) {
        if (mListener != null) {
            mListener.onAnswerSelected(
                    mQuestion.getAnswers().get(position));
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
        switch (v.getId()) {
            case R.id.txt_answer_1:
                onButtonPressed(0);
                break;
            case R.id.txt_answer_2:
                onButtonPressed(1);
                break;
            case R.id.txt_answer_3:
                onButtonPressed(2);
                break;
            case R.id.txt_answer_4:
                onButtonPressed(3);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAnswerSelected(Answer answer);
    }
}
