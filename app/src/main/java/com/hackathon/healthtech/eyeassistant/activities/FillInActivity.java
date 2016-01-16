package com.hackathon.healthtech.eyeassistant.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Answer;
import com.hackathon.healthtech.eyeassistant.entities.Question;
import com.hackathon.healthtech.eyeassistant.fragments.FillInAnswersFragment;
import com.hackathon.healthtech.eyeassistant.fragments.FillInQuestionFragment;

import java.util.List;

public class FillInActivity extends AppCompatActivity implements FillInQuestionFragment.OnFragmentInteractionListener,
FillInAnswersFragment.OnFragmentInteractionListener{

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Question mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onAnswersAsked(List<Answer> answers) {
        getQuestion().setAnswers(answers);
    }

    @Override
    public void onQuestionAsked(String question) {
        getQuestion().setQuestion(question);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                default:
                    return FillInQuestionFragment.newInstance();
                case 1:
                    return FillInAnswersFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public Question getQuestion() {
        if (mQuestion == null) {
            mQuestion = new Question();
        }
        return mQuestion;
    }
}
