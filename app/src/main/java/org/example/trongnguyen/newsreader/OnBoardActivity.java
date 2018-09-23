package org.example.trongnguyen.newsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoardActivity extends AppCompatActivity{
    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private TextView[] mDots;
    private SliderAdapter mSliderAdapter;
    private Button mNextButton;
    private Button mPrevButton;
    private int mCurrentPage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        mViewPager = (ViewPager) findViewById(R.id.sliding_view_pager);
        mLinearLayout = (LinearLayout) findViewById(R.id.position_dots);
        mSliderAdapter = new SliderAdapter(this);

        mNextButton = (Button) findViewById(R.id.next);
        mPrevButton = (Button) findViewById(R.id.previous);
        mViewPager.setAdapter(mSliderAdapter);

        addDotsIndicator(0);
        mViewPager.addOnPageChangeListener(viewListener);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNextButton.getText() == "Finish") {
                    onFinishedOnBoarding();
                } else {
                    mViewPager.setCurrentItem(mCurrentPage + 1);
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    public void onFinishedOnBoarding() {
        // Calls the shared preference and change to true to disable new user OnBoarding
        finish();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = SP.edit();
        editor.putBoolean("seenOnBoard", true);
        editor.apply();
        startActivity(new Intent(this,MainActivity.class));
    }
    public void addDotsIndicator(int position) {
        mDots = new TextView[4];
        mLinearLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.onBoardDots));

            mLinearLayout.addView(mDots[i]);
        }

        if(mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.onBoardDotsColored));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            if(position == 0) {
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPrevButton.setVisibility(View.INVISIBLE);

                mNextButton.setText("Next");
                mPrevButton.setText("");
            } else if (position == mDots.length -1) {
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(true);
                mPrevButton.setVisibility(View.VISIBLE);

                mNextButton.setText("Finish");
                mPrevButton.setText("Back");
            } else {
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(true);
                mPrevButton.setVisibility(View.VISIBLE);

                mNextButton.setText("Next");
                mPrevButton.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        // Method called to disable the back button. Users should not be able to back out of onboarding.


    }
}
