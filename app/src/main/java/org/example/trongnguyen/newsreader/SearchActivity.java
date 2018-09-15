package org.example.trongnguyen.newsreader;

import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.example.trongnguyen.newsreader.fragments.FavoritesFragment;
import org.example.trongnguyen.newsreader.fragments.MainFragment;
import org.example.trongnguyen.newsreader.fragments.SearchFragment;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "//------Search Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Inflate the toolbar and set the title to NewsFeed. This will be our main page.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Custom Search");


        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: reloading the main fragment due to savedInstanceState being null");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SearchFragment(), "search").commit();
        }
    }
}
