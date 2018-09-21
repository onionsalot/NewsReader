package org.example.trongnguyen.newsreader;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

/**
 * Used activity here instead of another fragment because I wanted to prevent the user from
 * being able to
 */

public class DetailsActivity extends AppCompatActivity{
    private static final String TAG = "Details Activity:~";
    News currentNews;
    ImageView detailImage;
    TextView detailTitle;
    TextView detailAuthor;
    TextView detailDescription;
    TextView detailUrl;
    TextView detailTags;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get intent() extras from the previous activity/fragment (MainFragment)
        currentNews = (News) getIntent().getSerializableExtra("news");
        // Find the views for the details.
        detailImage = (ImageView) findViewById(R.id.detail_image);
        detailTitle = (TextView) findViewById(R.id.detail_title);
        detailAuthor = (TextView) findViewById(R.id.detail_author);
        detailUrl = (TextView) findViewById(R.id.detail_url);
        detailDescription = (TextView) findViewById(R.id.detail_description);
        detailTags = (TextView) findViewById(R.id.detail_tags);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        printData();

        setSupportActionBar(toolbar);
    }

    private void printData() {
        // Print image to the toolbar
        Glide.with(this)
                .load(currentNews.getPicture())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher))
                .into(detailImage);

        // Print Source in the toolbar then grab the passed in info and print to the textFields.
        toolbar.setTitle(currentNews.getSource());
        detailTitle.setText(currentNews.getTitle());
        detailAuthor.setText(currentNews.getAuthor());

        detailUrl.setClickable(true);
        detailUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='" + currentNews.getUrl() + "'> Link to full article </a>";
        detailUrl.setText(Html.fromHtml(text));

        String descriptionString = (currentNews.getDate() + "\n" + currentNews.getDescription()).replace("\n","\n\n\t");
        detailDescription.setText(descriptionString);
        detailTags.setText("TAGS " + currentNews.getTags());

    }
}
