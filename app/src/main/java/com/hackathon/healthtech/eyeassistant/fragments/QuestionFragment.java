package com.hackathon.healthtech.eyeassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Question;

public class QuestionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = QuestionFragment.class.getSimpleName();
    private Question mQuestion;
    private TextView txtMessage;
    private TextView txtAnswer1, txtAnswer2, txtAnswer3, txtAnswer4;
    private ArcProgress pbAnswer1, pbAnswer2, pbAnswer3, pbAnswer4;


    private OnFragmentInteractionListener mListener;
    private RelativeLayout containerAnswers;
    private CountDownTimer countDownTimer;

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
        containerAnswers = (RelativeLayout) view.findViewById(R.id.container_answers);
        ViewGroup.LayoutParams layoutParams = containerAnswers.getLayoutParams();
        layoutParams.height = layoutParams.width;
        containerAnswers.setLayoutParams(layoutParams);

        txtMessage = (TextView) view.findViewById(R.id.txt_message);
        txtMessage = (TextView) view.findViewById(R.id.txt_message);
        (txtAnswer1 = (TextView) view.findViewById(R.id.txt_answer_1)).setOnClickListener(this);
        (txtAnswer2 = (TextView) view.findViewById(R.id.txt_answer_2)).setOnClickListener(this);
        (txtAnswer3 = (TextView) view.findViewById(R.id.txt_answer_3)).setOnClickListener(this);
        (txtAnswer4 = (TextView) view.findViewById(R.id.txt_answer_4)).setOnClickListener(this);


        pbAnswer1 = (ArcProgress) view.findViewById(R.id.pb_answer_1);
        pbAnswer2 = (ArcProgress) view.findViewById(R.id.pb_answer_2);
        pbAnswer3 = (ArcProgress) view.findViewById(R.id.pb_answer_3);
        pbAnswer4 = (ArcProgress) view.findViewById(R.id.pb_answer_4);
        if (mQuestion == null) {
            return;
        }
        setUpText(txtMessage, mQuestion.getQuestion());
        setUpText(txtAnswer1, mQuestion.getAnswerFirst().getMessage());
        setUpText(txtAnswer2, mQuestion.getAnswerSecond().getMessage());
        setUpText(txtAnswer3, mQuestion.getAnswerThird().getMessage());
        setUpText(txtAnswer4, mQuestion.getAnswerFourth().getMessage());
    }

    private void setUpText(TextView textView, String message) {
        if (!TextUtils.isEmpty(message))
            textView.setText(message);
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
        stopTimer();
        switch (v.getId()) {
            case R.id.txt_answer_1:
                showProgressBar(pbAnswer1, 1);
                pbAnswer2.setProgress(0);
                pbAnswer3.setProgress(0);
                pbAnswer4.setProgress(0);
                break;
            case R.id.txt_answer_2:
                showProgressBar(pbAnswer2, 2);
                pbAnswer1.setProgress(0);
                pbAnswer3.setProgress(0);
                pbAnswer4.setProgress(0);
                break;
            case R.id.txt_answer_3:
                showProgressBar(pbAnswer3, 3);
                pbAnswer1.setProgress(0);
                pbAnswer2.setProgress(0);
                pbAnswer4.setProgress(0);
                break;
            case R.id.txt_answer_4:
                showProgressBar(pbAnswer4, 4);
                pbAnswer1.setProgress(0);
                pbAnswer2.setProgress(0);
                pbAnswer3.setProgress(0);
                break;
        }
    }

    private void showProgressBar(final ArcProgress arcProgress, final int position) {

        final long length_in_milliseconds = 10000;
        final long period_in_milliseconds = 1000;

        countDownTimer = new CountDownTimer(length_in_milliseconds, period_in_milliseconds) {

            @Override
            public void onTick(long millisUntilFinished_) {
                float f = ((float) (length_in_milliseconds - millisUntilFinished_)) / length_in_milliseconds * 100;
                arcProgress.setProgress(Math.round(f));
            }

            @Override
            public void onFinish() {
                // do whatever when the bar is full
                int max = arcProgress.getMax();
                arcProgress.setProgress(max);
                if (mListener != null) {
                    switch (position) {
                        case 1:
                        default:
                            mQuestion.getAnswerFirst().setCorrect(true);
                            break;
                        case 2:
                            mQuestion.getAnswerSecond().setCorrect(true);
                            break;
                        case 3:
                            mQuestion.getAnswerThird().setCorrect(true);
                            break;
                        case 4:
                            mQuestion.getAnswerFourth().setCorrect(true);
                            break;
                    }
                    mListener.onAnswerSelected(mQuestion);
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface OnFragmentInteractionListener {
        void onAnswerSelected(Question question);
    }

}
