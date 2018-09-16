package org.example.trongnguyen.newsreader.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.example.trongnguyen.newsreader.DetailsActivity;
import org.example.trongnguyen.newsreader.News;
import org.example.trongnguyen.newsreader.NewsAdapter;
import org.example.trongnguyen.newsreader.NewsLoader;
import org.example.trongnguyen.newsreader.R;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>{
    private static final String NEWS_BASE_URL1 =
            "http://webhose.io/filterWebContent" +
                    "?token=fae3fadc-f18f-4ec4-a8d2-25fdc41bf0ab" +
                    "&format=json";
    private static final String NEWS_BASE_URL2 =
                    "&sort=published" +
                    "&q=language%3Aenglish" +
                    "%20site_type%3Anews" +
                    "%20is_first%3Atrue" +
                    "%20thread.country%3AUS%20";
    private static final String TAG = "----Main fragment---~";
    private NewsAdapter mAdapter;
    private List<News> savedSession;
    private boolean endOfList = false;
    private boolean doNothing = false;
    private boolean dataFetched = false; // Primarily to check if onResume needs to show a progress bar.
    private SharedPreferences.OnSharedPreferenceChangeListener spChangedListener;
    private SharedPreferences sp;
    int currentViewCount = 10;
    int layoutValue;
    TextView emptyView;
    ProgressBar progressBar;
    View rootView;
    ListView listView;
    ImageView layoutSmall;
    ImageView layoutMed;
    ImageView layoutLarge;
    public MainFragment() { }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Should get called at the start and should get called if app is out of focus.
        Log.d(TAG, "onSaveInstanceState: saved");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         *
         * onCreate's function is to create and populate the page upon first viewing.
         * This is where I instance SharedPreferences and invoke its onSharedPreferenceChanged method.
         * If a change happens in the user preferences (barring layouts) the adapter will clear
         * calling the custom clearAdapter() method. A new adapter will be created once the fragment is
         * placed back in front.
         *
         * It will make the initial instance call for the NewsAdapter into mAdapter.
         * Then the Loader will be initiated with id 0. Any subsequent calls to the loader
         * will check if the loader at 0 is already initiated and if not, initiate again.
         *
         */
        Log.d(TAG, "onCreate Initialized");
        // Retain the instance between configuration changes
        setRetainInstance(true);

        /*
        Shared Preferences created here. Will check if settings or layout has been changed.
        This is where the layoutValue will also be determined and will call the sharedPreference
        with key "layout" to set as its layout value;
         */
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: change detected + " + sharedPreferences + key);
                if (key.equals("layout")) {

                } else {
                    clearAdapter();
                }
            }
        };
        sp.registerOnSharedPreferenceChangeListener(spChangedListener);
        layoutValue = sp.getInt("layout", 0);

        // OnCreate is not called again in the lifecycle is rotated so the adapter is initiated here.
        mAdapter = new NewsAdapter(getActivity(), 0, new ArrayList<News>(),layoutValue);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);

        dataFetched = true; // Primarily used for onResume. See onResume for more detail
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         *
         * onCreateView is called after onCreate. Once we have the loader and adapter initialized, we
         * start processing the UI elements and inflate them into the fragment.
         *
         * Primarily for inflating elements.
         * Also houses the onClick methods for;
         *  onClickListener for each list view to send users to it's details page
         *  Scroll listener to add more views if the user has scrolled past a certain point
         *  onClickListener for the 3 layout change buttons up top
         *
         */
        Log.d(TAG, "onCreateView Initialized");

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Find the views for both the emptyView and progressBar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        emptyView = (TextView) rootView.findViewById(R.id.emptyView);

        /*
         *
         * The following bit is used to check the session state.
         * When the Fragment was created, the savedInstanceState will be called and save the state.
         * We check this and see that if it is not null, that means that there was most likely an orientation
         * change only. There is no need to do anything with the data.
         *
         * However, the problem exists where android will place the Fragment onPause() if placed in the backstack.
         * Once restored from the backstack, the Fragment's saveInstanceState will always be null for some reason.
         * To remedy this, we also check to see if savedSessions is null. By default, it will be null.
         * Once it has been used, it will no longer be null but instead, once the data is all cleared out, it will
         * remain as [] and not null. So if savedInstanceState and savedSession are both NOT null, this will be
         * a completely new session (IE. opening the app cold) and would result in the progress bar showing
         * until data is presented or data is invalid.
         *
         */
        if (savedInstanceState != null) {
            // Orientation change most likely.

        } else {
            if (savedSession != null) {
                // Returning from backstack most likely. Do nothing
                Log.d(TAG, "onCreateView: mAdapter is not null. Do nothing information is properly saved.");
            } else {
                // New instance.
                Log.d(TAG, "onCreateView: New instance. Turn on progress bar and continue on.");
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        // Inflate listView and set it's adapter. Then set onClickListener to it.
        listView = (ListView) rootView.findViewById(R.id.news_list_view);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: Item " + position + " was clicked");
                // Instantiate the current news item using the parent and position
                News news = (News) parent.getItemAtPosition(position);
                // Pass news as a Serializable Object to the DetailsActivity class
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("news", news);
                startActivity(intent);
            }
        });
        // Set onScrollListener to enable more views to be added via scrolling.
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if (doNothing) {
                        // Do nothing here. This means end of list has been reached and footer has been printed
                    } else {
                        if (endOfList) {
                            // If end of list has been reached and variable endOfList has been activated by addItems();
                            // The following code will run to add a footer showing that the end of the list has been reached.
                            // TODO: Make the footer not so ugly
                            Toast.makeText(getActivity(), "End of the list",
                                    Toast.LENGTH_SHORT).show();
                            doNothing = true; // Turn on doNothing so that the scrollListener will no longer do anything.
                        } else {
                            addItems(); // Add items to the view if the end of the list has not been reached. addItems will turn on endOfList if end of adapter.
                        }
                    }
                }
            }
        });

        /*
        *
        * Creating the views for each of the 3 layouts along with setting its onClick methods.
        * If clicked, each item will then check the layoutValue ( Declared in onCreate)
        * 0; Medium
        * 1; Small
        * 2; Large
        *
        * Will call changeLayout() and colorLayout() if click is determined.
        *
         */
        layoutSmall = (ImageView) rootView.findViewById(R.id.change_small);
        layoutSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutValue == 1) {
                    // Do nothing, already on layout
                } else { changeLayout(1);colorLayout(1);}
            }
        });
        layoutMed = (ImageView) rootView.findViewById(R.id.change_medium);
        layoutMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutValue == 0) {
                    // Do nothing, already on layout
                } else { changeLayout(0); colorLayout(0);}
            }
        });
        layoutLarge = (ImageView) rootView.findViewById(R.id.change_large);
        layoutLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutValue == 2) {
                    // Do nothing, already on layout
                } else { changeLayout(2);colorLayout(2);}
            }
        });
        colorLayout(layoutValue); // Called once the ImageViews have been inflated to highlight which item is on by default.
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated Initialized");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart Initialized");
        super.onStart();
    }

    @Override
    public void onResume() {
        /**
         *
         * onResume is used to check if the app is coming off of certain life states.
         * When the app first cold boots and onCreate is called, the dataFetched will be turned
         * to true.
         *
         * The only cases where dataFetched is false is if the adapter has been cleared via
         * clearAdapter() method or a cold boot.
         *
         * Since onResume is called each time the fragment comes back into view, it will check if
         * the data is available and if it isn't, in the case where the adapter has been cleared
         * (ie preference changes) the progress bar will start to spin indicating to users that
         * it is trying to load data.
         *
         */
        Log.d(TAG, "onResume: called");
        super.onResume();
        if (!dataFetched) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        /**
         *
         * Will be called once the Loader has been init.
         * Basically follows the guidelines of the webhose.io site to construct the string URL.
         *
         * Will construct the unix time to comply with webhose structure of crawling then form the sources
         * based on the user preferences in settings. Then loads that full URL into NewsLoader.
         *
         */
        // Creates a new loader for the given URL
        Log.d(TAG, "onCreateLoader: Starts");
        //========== Create the URL using user preferences starts here ===================================
        // Construct the date to be placed in. We are printing all articles that are crawled within the last 20 days
        Date currentDate = new Date();
        String urlTime = "&ts=" + String.valueOf(currentDate.getTime() - 1728000000L);
        // call the formURL function up top and gather information about the user preferences.
        String urlSources = formURL();
        // Form the URL
        String fullURL = NEWS_BASE_URL1 + urlTime + NEWS_BASE_URL2 + urlSources;
        Log.d(TAG, "onCreateLoader: Full URl after user preferences taken into account\n" + fullURL);
        // Return the results with the specified URL
        return new NewsLoader(getActivity(),fullURL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        /**
         *
         * Used to update the UI with the results we found.
         * Initially turns on dataFetched as we now have our data regardless if null or not.
         *
         * Checks if data is null to prevent any NPE.
         *
         */
        Log.d(TAG, "onLoadFinished: Start");
        dataFetched = true;
        // Similar to the onPostExecute. Basically updates UI
        if (data == null) {
            // Checks if we have proper data. If we don't then return user.
            // TODO: create an emptyView
            // Since there will be no results if data is null, we set emptyView to show users that
            // there is no results to be displayed. Also disable the progress bar.
            progressBar.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No results found");
            return;
        }
        /*
         *
         * Upon obtaining a valid list of news, the data will then be added onto the
         * mAdapter which will then be displayed on the screen via the onCreateView method.
         * Normally an mAdapter.addAll(data); would be the way to go, but in order to make the app
         * only load 10 at a time, a forLoop which will add each individual view
         * was created. This will also be helpful later when loading more data each time the user
         * scrolls down.
         *
         * onFinishLoading will actually be called with certain lifecycle changes. It will also
         * be called when we attempt to change layouts due to the fact that we have to instance
         * the adapter again. I make sure that the listView has already been reset with a new
         * Adapter before this is ever called again.
         *
         */
        if (listView.getCount() == 0) {
            int results;
            Log.d(TAG, "onLoadFinished: current data" + data.size());
            if (data.size() < 10) {
                results = data.size();
            } else {
                results = 10;
            }

            /*
             *
             * Once checks have been established, we make a for loop to get the data on the screen.
             * We insert the information in index i of "data" one at a time into i+ index of mAdapter.
             *
             */
            for (int i = 0; i < results; i++) {
                mAdapter.insert(data.get(i), i);
            }
            // Save the data into savedSession to grab more elements later when end of scroll list is reached.
            Log.d(TAG, "onLoadFinished: data after insertion" + data.size());
            savedSession = data;

            // Once information is loaded, make the progress bar invisible again.
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        //Loader reset, so we can clear out our existing data. Garbage cleaning
        Log.d(TAG, "onLoaderReset: Start");
        mAdapter.clear();
    }

    private String formURL() {
        /**
         *
         * Custom method used to create the URL using user preferences.
         *
         */
        // Initialize shared preferences and get the Set<String> of our multiple_choice_prefs.
        // We will construct the news source section first.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> sources = sp.getStringSet("multiple_choice_prefs", null);
        StringBuilder stringBuilder = new StringBuilder();
        // StringBuilder will check if user has checked each of the sources, then append the source.
        stringBuilder.append("(");
        if (sources != null) {
            if (sources.contains("arstechnica")) {
                stringBuilder.append("site%3Aarstechnica.com%20OR%20");
            }
            if (sources.contains("wired")) {
                stringBuilder.append("site%3Awired.com%20OR%20");
            }
            if (sources.contains("reuters")) {
                stringBuilder.append("site%3Areuters.com%20OR%20");
            }
            if (sources.contains("cnbc")) {
                stringBuilder.append("site%3Acnbc.com%20OR%20");
            }
            if (sources.contains("washington_post")) {
                stringBuilder.append("site%3Awashingtonpost.com%20OR%20");
            }
            if (sources.contains("wallstreet_journal")) {
                stringBuilder.append("site%3Awsj.com%20OR%20");
            }
            if (sources.contains("daily_caller")) {
                stringBuilder.append("site%3Adailycaller.com%20OR%20");
            }
            if (sources.contains("polygon")) {
                stringBuilder.append("site%3Apolygon.com%20OR%20");
            }
            if (sources.contains("siliconera")) {
                stringBuilder.append("site%3Asiliconera.com%20OR%20");
            }
            if (sources.contains("gamespot")) {
                stringBuilder.append("site%3Agamespot.com%20OR%20");
            }
            if (sources.contains("anime_news_network")) {
                stringBuilder.append("site%3Aanimenewsnetwork.com%20OR%20");
            }
            if (sources.contains("crunchyroll")) {
                stringBuilder.append("site%3Acrunchyroll.com%20OR%20");
            }
        }
        stringBuilder.replace(stringBuilder.length()-5,stringBuilder.length(),"");
        stringBuilder.append(")");

        // After appending the URL, we check if the user had specified a certain topic of interest.
        // If the user entered in anything and did not leave the topic_text preference empty,
        // append that into the end of the URL and return it.
        String topicString = sp.getString("topic_text", "").trim();
        if (!(topicString.equals(""))) {
            stringBuilder.append("%20thread.title%3A").append(sp.getString("topic_text", ""));
        }
        return stringBuilder.toString();
    }


    private void addItems() {
        /**
         *
         * This method was created to add more data into the list once the end of the list has been reached.
         * savedSession is used to access the full array of data outside of the loader. Since I am adding
         * in individual objects into the list one by one, I needed a way to access the data outside of the loader.
         *
         * Since savedSession is already loaded up with data, first the addItems() method will check its size.
         * We can determine that the list has ended because the savedSession size is always the max amount of
         * data we have. If the mAdapter (which is the object we are adding data from savedSession to) is equal to
         * savedSession, it is determined that the list has ended. Thus turning on endOfList which will deactivate
         * the scroll listener.
         *
         * Besides that, the insert method basically inserts the information from savedSession.get(currentViewCount)
         * which is 10 by default and + 1 for each loop into the current last position of mAdapter with .getCount().
         *
         * Even though currentViewCount defaults at 10, it will never be filed off if the data is under 10 because
         * anything under 10 would have already been dealt with in the original data as that by default will print
         * the first 10.
         *
         */
        for(int i = 0; i < 10; i++) {

            if (savedSession.size() == mAdapter.getCount()) {
                endOfList = true;
                break;
            }
            mAdapter.insert(savedSession.get(currentViewCount), mAdapter.getCount());
            Log.d(TAG, "addItems: inserting : " + savedSession.get(currentViewCount) + " current count " + currentViewCount + " into " + (mAdapter.getCount()));
            currentViewCount++;
        }
    }

    private void clearAdapter() {
        /**
         *
         * Cleared out any trace of the current adapters. Is called on a destroy such as when sharedPrefs
         * are changed.
         *
         * Used basically to reset all variables.
         * Nullifies the mAdapter. Once the view has been returned to, the onActivityCreated will be reinitialized
         * and it will see that the listItem is now null which will be as if cold booted and thus start adding int the data
         * into mAdapter. The loader being reinitialized here resets the data that will be added into the Adatper.
         *
         */
        Log.d(TAG, "clearAdapter: called");
        if (mAdapter != null) {mAdapter.clear();}
        if (savedSession != null) {savedSession.clear();}
        listView.setAdapter(null);
        dataFetched= false;
        endOfList = false;
        doNothing = false;
        currentViewCount = 10;
        getLoaderManager().destroyLoader(0);
        emptyView.setVisibility(View.INVISIBLE);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }


    private void changeLayout(int number) {
        /**
         *
         * Changed the layout of the main page. How this works is that it will place the new layout
         * into the SharedPrefs using the key value layout and commit it using the number passed in
         * by whomever called it. The layout will then refresh all the values back to its original form,
         * followed by instantiating a new NewsAdapter onto the mAdapter to reload the layout using
         * the new value passed in. Afterwards the loader will then be called again to load information
         * onto the list. New information will not be obtained because the loader already exists.
         *
         */
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("layout", number);
        editor.commit();
        layoutValue = sp.getInt("layout", 0);
        endOfList = false;
        doNothing = false;
        Log.d(TAG, "changeLayout: clicked");
        currentViewCount = 10;
        mAdapter = new NewsAdapter(getActivity(), 0, new ArrayList<News>(), number);
        listView.setAdapter(mAdapter);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);


        Log.d(TAG, "changeLayout: layoutValue" + layoutValue);
    }

    private void colorLayout(int number) {
        /**
         *
         * Changes the color of the icons. Check for the case number when called and then
         * sets the other layouts as transparent while setting the active one as GRAY.
         *
         */
        switch (number) {
            case 0: // Set color of the background for Med to indicate picked
                layoutSmall.setBackgroundColor(Color.TRANSPARENT);
                layoutMed.setBackgroundColor(Color.GRAY);
                layoutLarge.setBackgroundColor(Color.TRANSPARENT);
                break;
            case 1:// Set color of the background for Small to indicate picked
                layoutSmall.setBackgroundColor(Color.GRAY);
                layoutMed.setBackgroundColor(Color.TRANSPARENT);
                layoutLarge.setBackgroundColor(Color.TRANSPARENT);
                break;
            case 2:// Set color of the background for Large to indicate picked
                layoutSmall.setBackgroundColor(Color.TRANSPARENT);
                layoutMed.setBackgroundColor(Color.TRANSPARENT);
                layoutLarge.setBackgroundColor(Color.GRAY);
                break;
            default:
                break;
        }
    }
}
