package com.hackathon.healthtech.eyeassistant.activities;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import com.hackathon.healthtech.eyeassistant.R;


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
		switch (v.getId()) {
			case R.id.btn_ask:
				startActivity(new Intent(this, ConnectionActivity.class));
				break;
			case R.id.btn_answer:
				break;
		}
	}
}
