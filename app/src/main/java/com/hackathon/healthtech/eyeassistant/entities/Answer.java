package com.hackathon.healthtech.eyeassistant.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nnet on 1/16/16.
 */
public class Answer implements Parcelable {
    private String message;
    private boolean isCorrect;

    public Answer() {
    }

    public Answer(String message) {
        this(message, false);
    }

    public Answer(String message, boolean isCorrect) {
        this.message = message;
        this.isCorrect = isCorrect;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (isCorrect != answer.isCorrect) return false;
        return message.equals(answer.message);

    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + (isCorrect ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeByte(isCorrect ? (byte) 1 : (byte) 0);
    }

    protected Answer(Parcel in) {
        this.message = in.readString();
        this.isCorrect = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
