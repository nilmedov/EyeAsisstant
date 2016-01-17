package com.hackathon.healthtech.eyeassistant.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.application.EyeAssistantApp;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by nnet on 1/17/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_action_bar));
    }

    protected boolean isPatient() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(EyeAssistantApp.IS_PATIENT_KEY, false);
    }

}
