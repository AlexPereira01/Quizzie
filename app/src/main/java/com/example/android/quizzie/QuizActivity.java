package com.example.android.quizzie;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private static final String KEY_QUESTION_ANSWERED = "questionAnswered";
    private static final String KEY_IS_CHEATER = "isCheater";
    private static final String KEY_CHEAT_TOKENS = "cheatTokens";
    private static final String KEY_QUESTIONS_CORRECT = "questionsCorrect";
    private static final String KEY_TOTAL_ANSWERED = "totalAnswered";


    private double mQuestionsCorrect = 0;
    private double mTotalAnswered = 0;

    TextView mQuestionTextView;
    TextView mCheatTokenTextView;
    Button mTrueButton;
    Button mFalseButton;
    ImageButton mPreviousButton;
    ImageButton mNextButton;
    Button mCheatButton;

    private final Question[] mQuestions = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_ocean, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private boolean[] mIsCheater = new boolean[mQuestions.length];
    private int mCheatsRemaining = 0;

    private boolean[] mQuestionAnswered = new boolean[mQuestions.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX, 0);
            mQuestionAnswered = savedInstanceState.getBooleanArray(KEY_QUESTION_ANSWERED);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_IS_CHEATER);
            mCheatsRemaining = savedInstanceState.getInt(KEY_CHEAT_TOKENS, 3);
            mQuestionsCorrect = savedInstanceState.getDouble(KEY_QUESTIONS_CORRECT, 0);
            mTotalAnswered = savedInstanceState.getDouble(KEY_TOTAL_ANSWERED, 0);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizActivity.this.onClick(v);
            }
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });


        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex == 0)
                    mCurrentIndex = mQuestions.length - 1;
                else
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestions.length;
                updateQuestion();
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestions.length;
                updateQuestion();

            }
        });

        ActivityResultLauncher<Intent> resultLauncher = getUserCheatedResult();

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestions[mCurrentIndex].isAnswerTrue();


                if (mIsCheater[mCurrentIndex])
                    mCheatsRemaining--;

                mCheatTokenTextView.setText(getString(R.string.cheat_token_text, mCheatsRemaining));

                if (mIsCheater[mCurrentIndex] || mCheatsRemaining <= 0) {
                    mCheatButton.setEnabled(false);
                }

                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                resultLauncher.launch(intent);

                if (mIsCheater[mCurrentIndex] || mCheatsRemaining <= 0) {
                    mCheatButton.setEnabled(false);
                }
            }
        });

        mCheatTokenTextView = findViewById(R.id.cheat_token_text_view);
        mCheatTokenTextView.setText(getString(R.string.cheat_token_text, mCheatsRemaining));

        updateQuestion();
    }

    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(KEY_CURRENT_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_QUESTION_ANSWERED, mQuestionAnswered);
        savedInstanceState.putBooleanArray(KEY_IS_CHEATER, mIsCheater);
        savedInstanceState.putInt(KEY_CHEAT_TOKENS, mCheatsRemaining);
        savedInstanceState.putDouble(KEY_QUESTIONS_CORRECT, mQuestionsCorrect);
        savedInstanceState.putDouble(KEY_TOTAL_ANSWERED, mTotalAnswered);
    }

    // Updates question when user hits previous or next buttons and
    // disables questions after they are answered

    private void updateQuestion() {
        mQuestionTextView.setText(mQuestions[mCurrentIndex].getTextResId());

//        mTrueButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
        mCheatButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);

        if (mIsCheater[mCurrentIndex] || mCheatsRemaining <= 0) {
            mCheatButton.setEnabled(false);
        }
    }

    /**
     * helper method that checks if user revealed answer on cheat screen
     *
     * @return launcher used to start CheatActivity
     */
    private ActivityResultLauncher<Intent> getUserCheatedResult() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result == null || result.getData() == null)
                        return;

                    Intent data = result.getData();

                    mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
                });
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestions[mCurrentIndex].isAnswerTrue();
        int toastMessage;


        if (userPressedTrue == answerIsTrue) {
            mQuestionsCorrect++;
            mTotalAnswered++;

            if (mIsCheater[mCurrentIndex])
                toastMessage = R.string.correct_cheated_toast;
            else
                toastMessage = R.string.incorrect_toast;
        } else {
            mTotalAnswered++;

            if (mIsCheater[mCurrentIndex])
                toastMessage = R.string.incorrect_cheated_toast;
            else
                toastMessage = R.string.correct_toast;
        }

        Toast resultToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        resultToast.setGravity(Gravity.TOP, 0, 0);
        resultToast.show();

        if (mTotalAnswered >= mQuestions.length) {
            String result = "Final Score: " + Math.round((mQuestionsCorrect / mTotalAnswered) * 100f) + "%";

            Toast gradeToast = Toast.makeText(this, result, Toast.LENGTH_SHORT);
            gradeToast.setGravity(Gravity.TOP, 0, 0);
            gradeToast.show();
        }

        mQuestionAnswered[mCurrentIndex] = true;

        mTrueButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
        mCheatButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
    }

    private void onClick(View v) {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
        updateQuestion();
    }
}
