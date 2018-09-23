package org.example.trongnguyen.newsreader;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.example.trongnguyen.newsreader.fragments.FavoritesFragment;
import org.example.trongnguyen.newsreader.fragments.MainFragment;
import org.example.trongnguyen.newsreader.fragments.SearchFragment;

public class SearchActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    Fragment searchFragment;
    private static final String TAG = "//------Search Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(themePref.getString("theme", "1"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Inflate the toolbar and set the title to NewsFeed. This will be our main page.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Custom Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        searchFragment = new SearchFragment();
        transaction = getSupportFragmentManager().beginTransaction();


        if (savedInstanceState == null) {
            transaction.replace(R.id.fragment_container, searchFragment, "search");
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
}