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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.example.trongnguyen.newsreader.DetailsActivity;
import org.example.trongnguyen.newsreader.News;
import org.example.trongnguyen.newsreader.R;
import org.example.trongnguyen.newsreader.data.NewsContract;
import org.example.trongnguyen.newsreader.data.NewsCursorAdapter;
import org.example.trongnguyen.newsreader.data.NewsDbHelper;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private NewsDbHelper mDbHelper;
    View rootView;
    NewsCursorAdapter mNewsCursorAdapter;
    String sortBy;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NewsDbHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        Button byTitle = (Button) rootView.findViewById(R.id.sort_name);
        Button byDate = (Button) rootView.findViewById(R.id.sort_date);
        byTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatabaseInfo(1);
            }
        });
        byDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatabaseInfo(2);
            }
        });


        displayDatabaseInfo(0);

        return rootView;
    }

    private void displayDatabaseInfo(int sort) {

        switch (sort) {
            case 0:
                sortBy = null;
                break;
            case 1:
                if (TextUtils.isEmpty(sortBy)) {
                    sortBy = NewsContract.NewsEntry.COLUMN_NEWS_NAME + " ASC";
                } else {
                    if (sortBy.equals(NewsContract.NewsEntry.COLUMN_NEWS_NAME + " ASC")) {
                        sortBy = NewsContract.NewsEntry.COLUMN_NEWS_NAME + " DESC";
                    } else {
                        sortBy = NewsContract.NewsEntry.COLUMN_NEWS_NAME + " ASC";
                    }
                }
                break;
            case 2:
                if (TextUtils.isEmpty(sortBy)) {
                    sortBy = NewsContract.NewsEntry._ID + " ASC";
                } else {
                    if (sortBy.equals(NewsContract.NewsEntry._ID + " ASC")) {
                        sortBy = NewsContract.NewsEntry._ID + " DESC";
                    } else {
                        sortBy = NewsContract.NewsEntry._ID + " ASC";
                    }
                }
                break;
            default:
                break;
        }
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

        final Cursor cursor = getActivity().getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                projection,
                null,
                null,
                sortBy,
                null
        );


        // Cast the list view
        ListView newsListView = (ListView) rootView.findViewById(R.id.favorites_list_view);

        // Create adapter to form each list item for each row. Pass in cursor to get the data
        mNewsCursorAdapter = new NewsCursorAdapter(getContext(), cursor);

        // set the adapter on the casted view
        newsListView.setAdapter(mNewsCursorAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor1 = mNewsCursorAdapter.getCursor();
                int linkColumnIndex = cursor1.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_LINK);
                String url = cursor1.getString(linkColumnIndex);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        TextView savedResults = (TextView) rootView.findViewById(R.id.saved_results);
        savedResults.setText("Saved Articles: " + mNewsCursorAdapter.getCount());
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
                sortBy
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
