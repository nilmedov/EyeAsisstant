package com.hackathon.healthtech.eyeassistant.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.application.EyeAssistantApp;


public class MainActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_ask).setOnClickListener(this);
		findViewById(R.id.btn_answer).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean result;
		switch (v.getId()) {
			case R.id.btn_ask:
			default:
				result = false;
				break;
			case R.id.btn_answer:
				result = true;
				break;
		}
		prefs.edit().putBoolean(EyeAssistantApp.IS_PATIENT_KEY, result).commit();
		startActivity(new Intent(this, ConnectionActivity.class));
	}
}
