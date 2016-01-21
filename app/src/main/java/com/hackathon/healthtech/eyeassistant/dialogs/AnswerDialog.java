package com.hackathon.healthtech.eyeassistant.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Question;

/**
 * Created by Nazar Ilmedov on 1/21/16.
 */
public class AnswerDialog extends DialogFragment implements View.OnClickListener {
	private String question;
	private String answer;

	public static AnswerDialog newInstance(Question question) {
		AnswerDialog dialog = new AnswerDialog();
		dialog.setQuestion(question.getQuestion());

		if (question.getAnswerFirst().isCorrect()) {
			dialog.setAnswer(question.getAnswerFirst().getMessage());
		} else if (question.getAnswerSecond().isCorrect()) {
			dialog.setAnswer(question.getAnswerSecond().getMessage());
		} else if (question.getAnswerThird().isCorrect()) {
			dialog.setAnswer(question.getAnswerThird().getMessage());
		} else {
			dialog.setAnswer(question.getAnswerFourth().getMessage());
		}

		return dialog;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_answer, null);
		((TextView) view.findViewById(R.id.txt_question)).setText(question);
		((TextView) view.findViewById(R.id.txt_answer)).setText(answer);
		view.findViewById(R.id.btn_ok).setOnClickListener(this);

		Dialog dialog =  super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		return dialog;
	}

	private void setQuestion(String question) {
		this.question = question;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}
}
