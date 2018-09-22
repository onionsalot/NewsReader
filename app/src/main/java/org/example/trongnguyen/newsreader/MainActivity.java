package org.example.trongnguyen.newsreader;


import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.example.trongnguyen.newsreader.fragments.FavoritesFragment;
import org.example.trongnguyen.newsreader.fragments.MainFragment;
import org.example.trongnguyen.newsreader.fragments.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "//---MainActivity---//";
    private DrawerLayout drawer;
    MenuItem searchItem;
    int fragmentSelected; // 0 = Main; 1 = Settings; 2 = Favorites
    Fragment mainFragment;
    Fragment settingsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences themePref = PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(themePref.getString("theme", "1"));
        super.onCreate(savedInstanceState);
        // For first time runs, the preferences will not load quick enough and cause the app
        // to crash due to not having the right preferences to create a URL. This time
        // makes sure that default values are implemented first and then never read from again.
        PreferenceManager.setDefaultValues(this, R.xml.settings_main, false);

        setContentView(R.layout.activity_main_fragments);
        // Inflate the toolbar and set the title to NewsFeed. This will be our main page.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("News Feed");

        /*
         * Activity_main_fragments.xml consists of:
         * DrawerLayout with name drawer_layout as parent to all the fields
         * Toolbar with id toolbar
         * Fragment container w/ id fragment_container to hold the main fragment that is active. Will be used later
         * NavigationView w/ id nav_view to display the side menu.
         *
         * Here we find the drawer_layout, then find the NavigationView, and activate them with a lister
         * so that it will respond to pulling out.
         */
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);


        /*
         * Instantiate the fragments
         */
        mainFragment = new MainFragment();
        settingsFragment = new SettingsFragment();

        /* SavedInstanceState usually only is not null if the activity had never been fired up.
         * Cold loading would result in the SIS being null, which would then call for the
         * MainFragment to be loaded. If the activity is destroyed but not cold loaded, the MainFragment
         * will not randomly display.
         */
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: reloading the main fragment due to savedInstanceState being null");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mainFragment, "main");
            transaction.commit();
        }
        toggle.syncState();




        /*
         *
         * The NavigationView's listener.
         * This will house the choices and functions for each of the Navigation objects that are clickable.
         * Each navigation will function in a similar way.
         *
         * Checks which fragment is currently active. If user clicks on the same fragment again, the shade
         * will close and do nothing. If a new fragment is clicked, either pop it off the backstack if
         * it is the main fragment or set it to the new one if it is settings.
         *
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment settingsTest = (Fragment) getSupportFragmentManager().findFragmentByTag("settings");
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (settingsTest != null && settingsTest.isVisible()) {
                            getSupportFragmentManager().popBackStackImmediate();
                        }
                        else {
                            // Do nothing. Main is visible
                        }
                        searchItem.setVisible(true);
                        break;
                    case R.id.nav_settings:
                        if (settingsTest != null && settingsTest.isVisible()) {
                            // Do nothing, Settings is visible
                        }
                        else {
                            transaction.addToBackStack("main");
                            transaction.replace(R.id.fragment_container, settingsFragment, "settings");
                            transaction.commit();
                        }
                        searchItem.setVisible(false);
                        break;
                    case R.id.nav_favorites:
                        Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(aboutIntent);
                    default:
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {
        // If drawer is open, close the drawer on back pressed.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment main = (Fragment) getSupportFragmentManager().findFragmentByTag("main");
            if (main != null && main.isVisible()) {
                // Main fragment is visible; Prompt user if they want to close.
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Exit News Reader?");
                builder1.setCancelable(true);


                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                MainActivity.super.onBackPressed();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            else {
                // Settings is visible; hide and open the main fragment again.
                getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("settings")).commit();
                searchItem.setVisible(true);
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchItem = menu.findItem(R.id.action_menu_search);
        Fragment test = (Fragment) getSupportFragmentManager().findFragmentByTag("settings");
        if (test != null && test.isVisible()) {
            searchItem.setVisible(false);
        }
        else {
            searchItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Search is only available when on the Main Fragment. Will redirect us to the search class
            case R.id.action_menu_search:
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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