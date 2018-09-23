package org.example.trongnguyen.newsreader;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;

    public SliderAdapter(Context context) {
        this.mContext = context;
    }

    // Arrays
    public int[] sliding_images = {
            R.drawable.tut_1,
            R.drawable.tut_2,
            R.drawable.tut_3,
            R.drawable.tut_4
    };

    public String[] sliding_titles = {
            "Welcome!",
            "Favorites for days",
            "Topics important to you",
            "Themes and Layouts"
    };

    public String[] sliding_description = {
            "News Reader is a concept app where you're able to customize your viewing experience to suit your own needs!",
            "Have an article you like or want to view again in the future? Favorite it! Your information is stored locally so you can view it offline too!",
            "Customize your results to your choosing. Your homepage will display only the information that you tell it to, from the sources you want from it. \nAdjust your settings to your liking and never miss another beat!",
            "Show your fun side or your moody side; Pick a theme to suit your needs in Settings.\n\nAt a glance? Blind and text too small? Adjust the layout from At-A-Glance view or detailed view."
    };

    public String[] sliding_colors = {
            "#002d92",
            "#557bd2",
            "#b04f4f",
            "#55327b"
    };

    @Override
    public int getCount() {
        return sliding_titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.sliding_layout, container, false);

        ImageView slidingImageView = (ImageView) view.findViewById(R.id.sliding_image);
        TextView slidingTitleView = (TextView) view.findViewById(R.id.sliding_title);
        TextView slidingDescription = (TextView) view.findViewById(R.id.sliding_description);
        ConstraintLayout slidingLayout = (ConstraintLayout) view.findViewById(R.id.sliding_constraint_layout);

        slidingImageView.setImageResource(sliding_images[position]);
        slidingTitleView.setText(sliding_titles[position]);
        slidingDescription.setText(sliding_description[position]);
        slidingLayout.setBackgroundColor(Color.parseColor(sliding_colors[position]));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
