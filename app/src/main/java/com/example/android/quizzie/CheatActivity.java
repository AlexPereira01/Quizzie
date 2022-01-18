package com.example.android.quizzie;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.android.quizzie.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.android.quizzie.answer_shown";

    private static final String KEY_ANSWER_SHOWN = "answerShown";


    TextView mAnswerTextView;
    TextView mBuildVersionTextView;
    Button mShowAnswerButton;

    private boolean mAnswerIsTrue;
    private boolean mAnswerShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            mAnswerShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN, false);
            setAnswerShownResult(mAnswerShown);
        }

        // Get answer from QuizActivity using key and store it
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mAnswerTextView = findViewById(R.id.answer_text_view);

        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAnswerIsTrue)
                    mAnswerTextView.setText(R.string.true_text);
                else
                    mAnswerTextView.setText(R.string.false_text);

                mAnswerShown = true;
                setAnswerShownResult(true);

                // Check if device's runtime version supports the animation
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // get center of the clipping circle
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;

                    // get initial radius of clipping circle
                    float radius = mShowAnswerButton.getWidth();

                    // create animation(final radius is 0)
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);

                    // make view invisible once the animation is complete
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });

                    // start the animation
                    anim.start();
                }
                else
                    // makes view invisible for devices lower than LOLLIPOP
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
            }
        });

        mBuildVersionTextView = findViewById(R.id.build_version_text_view);
        mBuildVersionTextView.setText(getString(R.string.api_level_text, Build.VERSION.SDK_INT));
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_ANSWER_SHOWN, mAnswerShown);

    }
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
}
