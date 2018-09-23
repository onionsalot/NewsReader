package org.example.trongnguyen.newsreader;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.example.trongnguyen.newsreader.data.NewsContract;

import java.util.ArrayList;

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
    LikeButton detailLike;
    ArrayList<String> mUids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(themePref.getString("theme", "1"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // Create cursor to compare the Uids to see if there is a favorites match
        mUids = compareFavorites();
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
        detailLike = (LikeButton) findViewById(R.id.heart_image);
        printData();

        setSupportActionBar(toolbar);
    }

    private void printData() {
        // Print image to the toolbar
        Glide.with(this)
                .load(currentNews.getPicture())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.no_image_found))
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

        String uuid = currentNews.getUuid();
        if (mUids.contains(uuid)) {
            detailLike.setLiked(true);
        } else {
            detailLike.setLiked(false);
        }
        detailLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addFavorites(currentNews);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                deleteFavorites(currentNews);
            }
        });

    }

    public void themeChooser(String theme) {
        switch (theme) {
            case "1":
                setTheme(R.style.AppTheme);
                break;
            case "2":
                setTheme(R.style.Midnight);
                break;
            case "3":
                setTheme(R.style.cottonCandy);
                break;
            case "4":
                setTheme(R.style.rockRoses);
                break;
            case "5":
                setTheme(R.style.limeContrast);
                break;
            case "6":
                setTheme(R.style.moodyRain);
                break;
        }
    }

    private ArrayList<String> compareFavorites() {
        String [] projection = {
                NewsContract.NewsEntry.COLUMN_NEWS_UID
        };

        Cursor cursor = this.getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null,
                null
        );
        ArrayList<String> uids = new ArrayList<String>();
        try {
            cursor.moveToFirst();
            while(!(cursor.isAfterLast())){
                uids.add(cursor.getString(cursor.getColumnIndex("uid")));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return uids;
    }

    private void addFavorites(News position) {
        ContentValues values = new ContentValues();
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_UID, position.getUuid());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_NAME, position.getTitle());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_AUTHOR, position.getAuthor());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_DATE, position.getDate());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION, position.getDescription());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_SOURCE, position.getSource());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_TAGS, position.getTags());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_LINK, position.getUrl());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_PICTURE, position.getPicture());

        // Insert a new row into the provider using the ContentResolver. Use the CONTENT_URI to indicate
        // that we want to insert into the news DB table.
        // Receive the new content URI that will allow us to access data in the future.
        Uri newUri = this.getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI,values);
        Toast.makeText(this, "Favorites has been added!",
                Toast.LENGTH_SHORT).show();
        mUids = compareFavorites();
    }

    private void deleteFavorites(News position) {
        String uuid = position.getUuid();
        String selection = NewsContract.NewsEntry.COLUMN_NEWS_UID + " = ?";
        int rowsDeleted = this.getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI,selection,new String[]{uuid});
        Toast.makeText(this, rowsDeleted + " row deleted successfully!",
                Toast.LENGTH_SHORT).show();
        mUids = compareFavorites();
    }
}
