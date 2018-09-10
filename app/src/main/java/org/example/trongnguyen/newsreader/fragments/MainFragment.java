package org.example.trongnguyen.newsreader.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ListView;

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

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>{
    // How the Webhose.io API is constructed, laid out in a readable manner.
    private static final String NEWS_STATIC_URL =
            "http://webhose.io/filterWebContent" +
                    "?token=da31bd42-351a-40ab-b014-ac6944b07a65" +
                    "&format=json" +
                    "&ts=1534820444293" +
                    "&sort=relevancy" +
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
    private static final String TAG = "----Main fragment---~";
    private NewsAdapter mAdapter;
    View rootView;
    ListView listView;
    public MainFragment() { }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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

        // OnCreate is not called again in the lifecycle is rotated so the adapter is initiated here.
        mAdapter = new NewsAdapter(getActivity(), 0, new ArrayList<News>());  // Get a reference to the LoaderManager, in order to interact with loaders.
        getLoaderManager().initLoader(0, null, this);
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * onCreateView is called after onCreate. Once we have the laoder and adapter initialized, we
     * start processing the UI elements and inflate them into the flagment.
     *
     * This method also holds our onItemClick for the adapter.
     *
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Initialized");

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
        return rootView;
    }



    @Override
    public void onStart() {
        Log.d(TAG, "onStart Initialized");
        Date currentDate = new Date();
        currentDate.getTime();
        Log.d(TAG, "onStart: " + (currentDate.getTime() - 1728000000L));
        // 20 days in the past from today
        super.onStart();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // Creates a new loader for the given URL
        Log.d(TAG, "onCreateLoader: Starts");
        // returning the URL passed in..
        return new NewsLoader(getActivity(),NEWS_STATIC_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        // Update the UI with the result
        Log.d(TAG, "onLoadFinished: Start");
        // Similar to the onPostExecute. Basically updates UI

        // Upon obtaining a valid list of news, the data will then be added onto the
        // mAdapter which will then be displayed on the screen via the onCreateView method.
        mAdapter.addAll(data);
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


}
