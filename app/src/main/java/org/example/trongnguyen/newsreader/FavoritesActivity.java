package org.example.trongnguyen.newsreader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.example.trongnguyen.newsreader.fragments.FavoritesFragment;

public class FavoritesActivity extends AppCompatActivity {
    private static final String TAG = "/----FavoritesActivity";
    FragmentTransaction transaction;
    Fragment favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        // Inflate the toolbar and set the title to NewsFeed. This will be our main page.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        favoritesFragment = new FavoritesFragment();
        transaction = getSupportFragmentManager().beginTransaction();


        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: reloading the main fragment due to savedInstanceState being null");
            transaction.replace(R.id.fragment_container, favoritesFragment, "favorites");
            transaction.commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
