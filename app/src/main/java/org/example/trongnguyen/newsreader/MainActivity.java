package org.example.trongnguyen.newsreader;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* SavedInstanceState usually only is not null if the activity had never been fired up.
        * Cold loading would result in the SIS being null, which would then call for the
        * MainFragment to be loaded. If the activity is destroyed but not cold loaded, the MainFragment
        * will not randomly display.
        */
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: reloading the main fragment due to savedInstanceState being null");
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new MainFragment(), "main").commit();
        }




        /*
        *
        * The NavigationView's listener.
        * This will house the choices and functions for each of the Navigation objects that are clickable.
        * Each navigation will function in a similar way.
        *
        * Check to see if either of the fragments NOT picked are active. If they are, hide them.
        * Finally check if the fragment picked had ever been initialized. If it has, simply "show"
        * it into frontal view. If it has not, "add" it to the current frontal View. Add is used because
        * it will not destroy the fragment that it is replacing. "replace" is another option but that would
        * destroy the replaced fragment unless backstacks are used. The way that the app functions now,
        * there is no real need for backstacks are "searches" and "details" are both performed on exterior
        * Intents.
        *
        * REVISED*
        *
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (getSupportFragmentManager().findFragmentByTag("main").isVisible()) {
                            // DO NOTHING
                        } else {
                            getSupportFragmentManager().popBackStackImmediate();
                        }
                        searchItem.setVisible(true);
                        break;
                    case R.id.nav_settings:
                        // Fragment has never been created. Create a brand new one
                        if (getSupportFragmentManager().findFragmentByTag("main").isVisible()) {
                            getSupportFragmentManager().beginTransaction()
                                    .addToBackStack("mainStack").replace(R.id.fragment_container,
                                    new SettingsFragment(), "settings").commit();
                        }
                        searchItem.setVisible(false);
                        break;
                    case R.id.nav_favorites:
                        if (getSupportFragmentManager().findFragmentByTag("settings") != null) {
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("settings")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("main") != null) {
                            Log.d(TAG, "onNavigationItemSelected: is now hidden");
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("main")).commit();
                        }

                        if (getSupportFragmentManager().findFragmentByTag("favorites") != null ) {
                            getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("favorites")).commit();

                        } else {
                            // Fragment has never been created. Create a brand new one
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                                    new FavoritesFragment(), "favorites").commit();
                        }
                        searchItem.setVisible(false);
                        break;
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchItem = menu.findItem(R.id.action_menu_search);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Search is only available when on the Main Fragment. Will redirect us to the search class
            case R.id.action_menu_search:
                // Do nothing for now
                return true;

        }
        return super.onOptionsItemSelected(item);
    }






}
