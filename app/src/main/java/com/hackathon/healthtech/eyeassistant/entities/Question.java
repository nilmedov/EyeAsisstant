package com.hackathon.healthtech.eyeassistant.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by nnet on 1/16/16.
 */
public class Question implements Parcelable {
    private String question;
    private List<Answer> answers;

    public Question() {
    }

    public Question(String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.question);
        dest.writeTypedList(answers);
    }

    protected Question(Parcel in) {
        this.question = in.readString();
        this.answers = in.createTypedArrayList(Answer.CREATOR);
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
