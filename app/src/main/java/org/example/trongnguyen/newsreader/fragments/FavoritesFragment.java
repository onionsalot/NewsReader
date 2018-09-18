package org.example.trongnguyen.newsreader.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.example.trongnguyen.newsreader.R;
import org.example.trongnguyen.newsreader.data.NewsContract;
import org.example.trongnguyen.newsreader.data.NewsCursorAdapter;
import org.example.trongnguyen.newsreader.data.NewsDbHelper;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private NewsDbHelper mDbHelper;
    View rootView;
    TextView textView;
    NewsCursorAdapter mNewsCursorAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NewsDbHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        Button button = (Button) rootView.findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testButton();
            }
        });
        Button button2 = (Button) rootView.findViewById(R.id.test_button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testButton2();
            }
        });

        displayDatabaseInfo();

        return rootView;
    }

    private void displayDatabaseInfo() {
        String [] projection = {
                BaseColumns._ID,
                NewsContract.NewsEntry.COLUMN_NEWS_UID,
                NewsContract.NewsEntry.COLUMN_NEWS_NAME,
                NewsContract.NewsEntry.COLUMN_NEWS_AUTHOR,
                NewsContract.NewsEntry.COLUMN_NEWS_DATE,
                NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION,
                NewsContract.NewsEntry.COLUMN_NEWS_SOURCE,
                NewsContract.NewsEntry.COLUMN_NEWS_TAGS,
                NewsContract.NewsEntry.COLUMN_NEWS_LINK,
                NewsContract.NewsEntry.COLUMN_NEWS_PICTURE
        };

        Cursor cursor = getActivity().getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null,
                null
        );


        // Cast the list view
        ListView newsListView = (ListView) rootView.findViewById(R.id.favorites_list_view);

        // Create adapter to form each list item for each row. Pass in cursor to get the data
        NewsCursorAdapter adapter = new NewsCursorAdapter(getContext(), cursor);

        // set the adapter on the casted view
        newsListView.setAdapter(adapter);
    }

    private void testButton() {
        String [] projection = {
                NewsContract.NewsEntry.COLUMN_NEWS_UID
        };

        Cursor cursor = getContext().getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null,
                null
        );
        ArrayList<String> uids = new ArrayList<String>();
        try {
            cursor.moveToFirst();
            while(!(cursor.isAfterLast())){
                uids.add(cursor.getString(cursor.getColumnIndex("uid")));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        Log.d(TAG, "compareFavorites: " + uids.get(1));
    }
    private void testButton2() {
        getActivity().getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, null, null);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String [] projection = {
                BaseColumns._ID,
                NewsContract.NewsEntry.COLUMN_NEWS_UID,
                NewsContract.NewsEntry.COLUMN_NEWS_NAME,
                NewsContract.NewsEntry.COLUMN_NEWS_AUTHOR,
                NewsContract.NewsEntry.COLUMN_NEWS_DATE,
                NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION,
                NewsContract.NewsEntry.COLUMN_NEWS_SOURCE,
                NewsContract.NewsEntry.COLUMN_NEWS_TAGS,
                NewsContract.NewsEntry.COLUMN_NEWS_LINK,
                NewsContract.NewsEntry.COLUMN_NEWS_PICTURE
        };

        return new CursorLoader(getContext(),
                NewsContract.NewsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mNewsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNewsCursorAdapter.swapCursor(null);
    }
}
