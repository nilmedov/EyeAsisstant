package com.hackathon.healthtech.eyeassistant.application;

import android.app.Application;

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
                .setDefaultFontPath("fonts/Seravek.ttc")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}