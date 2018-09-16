package org.example.trongnguyen.newsreader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    String mUrl;
    private List<News> items;
    // This is the constructor class.
    // @param context of the activity
    // @param url to load data from
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl=url;
    }


    @Override
    public List<News> loadInBackground() {
        // This method will be used to parse out data. Similar to asyncTask doInBackground
        Log.d(TAG, "loadInBackground: start");
        if (mUrl == null) {
            return null;
        }
        items = QueryTools.fetchNewsData(mUrl);
        return (items);
    }

    @Override
    protected void onStartLoading() {
        // onStartLoading was getting called upon each orientation change even if data was pulled.
        // Checks if data is null before forceLoad(). If data is not null, then it is an orientation change.
        if (items == null) {
            forceLoad();
        } else {

        }
    }



}
