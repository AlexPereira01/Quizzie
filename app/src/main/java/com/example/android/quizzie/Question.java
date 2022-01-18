package com.example.android.quizzie;

public class Question {
    private int mTextResId;
    private boolean answerTrue;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        this.answerTrue = answerTrue;
    }

    public int getTextResId() {
        return 0;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }
}
