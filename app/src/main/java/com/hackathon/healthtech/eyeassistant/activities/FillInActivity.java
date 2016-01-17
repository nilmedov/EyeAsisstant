package com.hackathon.healthtech.eyeassistant.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.entities.Answer;
import com.hackathon.healthtech.eyeassistant.entities.Question;
import com.hackathon.healthtech.eyeassistant.fragments.FillInAnswersFragment;
import com.hackathon.healthtech.eyeassistant.fragments.FillInQuestionFragment;

public class FillInActivity extends BaseActivity implements FillInQuestionFragment.OnFragmentInteractionListener,
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
    public void onAnswersAsked(Answer[] answers) {
        getQuestion().setAnswerFirst(answers[0]);
        getQuestion().setAnswerSecond(answers[1]);
        getQuestion().setAnswerThird(answers[2]);
        getQuestion().setAnswerFourth(answers[3]);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onQuestionAsked(String question) {
        getQuestion().setQuestion(question);
        int currentItem = mViewPager.getCurrentItem();
        if (currentItem < mSectionsPagerAdapter.getCount()) {
            mViewPager.setCurrentItem(currentItem + 1);
        }

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
