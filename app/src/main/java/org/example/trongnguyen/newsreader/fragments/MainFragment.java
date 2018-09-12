package org.example.trongnguyen.newsreader.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    // How the Webhose.io API is constructed, laid out in a readable manner.
    private static final String NEWS_STATIC_URL =
            "http://webhose.io/filterWebContent" +
                    "?token=da31bd42-351a-40ab-b014-ac6944b07a65" +
                    "&format=json" +
                    "&ts=1534820444293" +
                    "&sort=published" +
                    "&q=language%3Aenglish" +
                    "%20site_type%3Anews" +
                    "%20is_first%3Atrue" +
                    "%20thread.country%3AUS" +
                    "%20(site%3Aarstechnica.com" +
                        "%20OR%20site%3Awired.com" +
                        "%20OR%20site%3Areuters.com" +
                        "%20OR%20site%3Acnbc.com" +
                        "%20OR%20site%3Awashingtonpost.com" +
                        "%20OR%20site%3Awsj.com" +
                        "%20OR%20site%3Apolygon.com" +
                        "%20OR%20site%3Adailycaller.com" +
                        "%20OR%20site%3Asiliconera.com" +
                        "%20OR%20site%3Agamespot.com" +
                        "%20OR%20site%3Aanimenewsnetwork.com" +
                        "%20OR%20site%3Acrunchyroll.com)" +
                    "%20thread.title%3ASpider";
    private static final String NEWS_BASE_URL1 =
            "http://webhose.io/filterWebContent" +
                    "?token=da31bd42-351a-40ab-b014-ac6944b07a65" +
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
    private boolean dataFetched = false;
    private SharedPreferences.OnSharedPreferenceChangeListener spChangedListener;
    TextView emptyView;
    ProgressBar progressBar;
    View rootView;
    ListView listView;
    public MainFragment() { }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: saved");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated Initialized");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Initialized");
        // Retain the instance between configuration changes
        setRetainInstance(true);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: change detected");
                clearAdapter();
            }
        };
        sp.registerOnSharedPreferenceChangeListener(spChangedListener);

        // OnCreate is not called again in the lifecycle is rotated so the adapter is initiated here.
        mAdapter = new NewsAdapter(getActivity(), 0, new ArrayList<News>());  // Get a reference to the LoaderManager, in order to interact with loaders.
        getLoaderManager().initLoader(0, null, this);
        dataFetched = true;
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
        if (!dataFetched) {
            createAdapter();
        }
    }

    /**
     *
     * onCreateView is called after onCreate. Once we have the loader and adapter initialized, we
     * start processing the UI elements and inflate them into the fragment.
     *
     * This method also holds our onItemClick for the adapter.
     *
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                Log.d(TAG, "onCreateView: mAdapter is not null. Do nothing information is properly saved");
            } else {
                // New instance.
                Log.d(TAG, "onCreateView: HUH WHAT IS THIS");
                progressBar.setVisibility(View.VISIBLE);
            }
        }

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
                            View footerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_footer, null, false);
                            listView.addFooterView(footerView);
                            doNothing = true;
                        } else {
                            addItems();
                        }
                    }
                }
            }
        });

        return rootView;
    }

    /**
     *
     * This method was created to add more data into the list once the end of the list has been reached.
     * Tests show that the memory consumption or memory saved is very minimal at best and I am unable
     * to test in a wider environment, so with the theory that adding only the 0th element of the
     * savedSession/data then immediately removing it will save on data, I used the same method I used
     * to craft the initial list here.
     * This appends the next item into the bottom of the list. Once the savedSession.size() = 0; we know that
     * the data is at its end, which will activate endOfList bool and the list will no longer add items
     * per scroll and instead, activate a footer to show the user that the end of the list has been reached.
     *
     */
    private void addItems() {
        int currentNum;
        if (savedSession.size() > 10) {
            currentNum = 10;
        } else {
            currentNum = savedSession.size();
        }


        for(int i = 0; i < currentNum; i++) {
            mAdapter.insert(savedSession.get(0), mAdapter.getCount());
            savedSession.remove(0);
        }
        if (savedSession.size() == 0) {
            endOfList = true;
        }
    }

    private String formURL() {
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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart Initialized");
        super.onStart();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
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
        // Update the UI with the result
        Log.d(TAG, "onLoadFinished: Start");
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
        * only load the amount that the user wishes, a for loop which will add each individual view
        * was created. This will also be helpful later when loading more data each time the user
        * scrolls down.
        *
         */
        // Checks how many results we have in data.size(). If the results are less than 10
        // then only print out that amount. Normally our default print is 10.
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
         * We insert the information in index 0 of "data" one at a time into i+ index of mAdapter.
         * Afterwards we remove the0th element of "data." this will shrink down the information in
         * data thus should help with performance ever so slightly.
         *
          */
        for (int i = 0; i < results; i++) {
            mAdapter.insert(data.get(0), i);
            data.remove(0);
        }
        // Save the data into savedSession to grab more elements later when end of scroll list is reached.
        Log.d(TAG, "onLoadFinished: data after insertion" + data.size());
        savedSession = data;

        // Once information is loaded, make the progress bar invisible again.
        progressBar.setVisibility(View.INVISIBLE);
        // Once the Adapter has finished loading the data, he adapter will be destroyed to prevent
        // any reloading of data. This is to fix the issue of the adapter adding onto the end
        // of the data stream a new set of data each time the activity is destroyed.
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        //Loader reset, so we can clear out our existing data. Garbage cleaning
        Log.d(TAG, "onLoaderReset: Start");
        mAdapter.clear();
    }

    private void clearAdapter() {
        mAdapter.clear();
        savedSession.clear();
        listView.setAdapter(null);
        dataFetched= false;
        endOfList = false;
        doNothing = false;
        progressBar.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
        getLoaderManager().destroyLoader(0);
    }

    private void createAdapter() {
        mAdapter = new NewsAdapter(getActivity(), 0, new ArrayList<News>());  // Get a reference to the LoaderManager, in order to interact with loaders.
        getLoaderManager().initLoader(0, null, this);
        dataFetched = true;
        progressBar.setVisibility(View.VISIBLE);
        listView.setAdapter(mAdapter);
    }

}
