package com.hackathon.healthtech.eyeassistant.application;

import android.app.Application;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;

import com.hackathon.healthtech.eyeassistant.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by nnet on 1/17/16.
 */
public class EyeAssistantApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Seravek.ttf")
                .addCustomStyle(AppCompatTextView.class, android.R.attr.textViewStyle)
                .addCustomStyle(AppCompatEditText.class, R.attr.editTextStyle)
                .addCustomStyle(AppCompatButton.class, R.attr.buttonStyle)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}