package org.example.trongnguyen.newsreader;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.example.trongnguyen.newsreader.data.NewsContract;
import org.example.trongnguyen.newsreader.fragments.FavoritesFragment;

public class FavoritesActivity extends AppCompatActivity {
    private static final String TAG = "/----FavoritesActivity";
    FragmentTransaction transaction;
    Fragment favoritesFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(themePref.getString("theme", "1"));
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
            case R.id.delete_all:
                deleteAll();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    public void deleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to remove ALL articles?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                int rowsDeleted = getApplicationContext().getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI,null,null);
                Toast.makeText(getApplicationContext(), "All articles you saved have been removed!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
