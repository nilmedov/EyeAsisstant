package com.hackathon.healthtech.eyeassistant.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by nnet on 1/16/16.
 */
public class Question implements Parcelable {
    private String question;
    private Answer answerFirst;
    private Answer answerSecond;
    private Answer answerThird;
    private Answer answerFourth;

    public Question() {
    }

    public Question(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Answer getAnswerFirst() {
        return answerFirst;
    }

    public void setAnswerFirst(Answer answerFirst) {
        this.answerFirst = answerFirst;
    }

    public Answer getAnswerSecond() {
        return answerSecond;
    }

    public void setAnswerSecond(Answer answerSecond) {
        this.answerSecond = answerSecond;
    }

    public Answer getAnswerThird() {
        return answerThird;
    }

    public void setAnswerThird(Answer answerThird) {
        this.answerThird = answerThird;
    }

    public Answer getAnswerFourth() {
        return answerFourth;
    }

    public void setAnswerFourth(Answer answerFourth) {
        this.answerFourth = answerFourth;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.question);
        dest.writeParcelable(this.answerFirst, 0);
        dest.writeParcelable(this.answerSecond, 0);
        dest.writeParcelable(this.answerThird, 0);
        dest.writeParcelable(this.answerFourth, 0);
    }

    protected Question(Parcel in) {
        this.question = in.readString();
        this.answerFirst = in.readParcelable(Answer.class.getClassLoader());
        this.answerSecond = in.readParcelable(Answer.class.getClassLoader());
        this.answerThird = in.readParcelable(Answer.class.getClassLoader());
        this.answerFourth = in.readParcelable(Answer.class.getClassLoader());
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
