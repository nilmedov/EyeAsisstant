package com.hackathon.healthtech.eyeassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Question;

public class FillInQuestionFragment extends Fragment implements View.OnClickListener {
    private Question mQuestion;
    private EditText fMessage;

    private OnFragmentInteractionListener mListener;

    public FillInQuestionFragment() {
        // Required empty public constructor
    }

    public static FillInQuestionFragment newInstance() {
        FillInQuestionFragment fragment = new FillInQuestionFragment();
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
        return inflater.inflate(R.layout.fragment_fill_in_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_next).setOnClickListener(this);

        fMessage = (EditText) view.findViewById(R.id.f_message);
        if (mQuestion != null && !TextUtils.isEmpty(mQuestion.getQuestion()))
            fMessage.setText(mQuestion.getQuestion());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onNextPressed() {
        if (mListener != null) {
            mListener.onQuestionAsked(fMessage.getText().toString());
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
        // TODO: Update argument type and name
        void onQuestionAsked(String question);
    }
}
