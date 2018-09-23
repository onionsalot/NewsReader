package org.example.trongnguyen.newsreader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class NewsProvider extends ContentProvider {
    private static final String TAG = "NewsProvider";
    private NewsDbHelper mNewsDbHelper;
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = NewsProvider.class.getSimpleName();
    // Defines a new Uri object that receives the result of the insertion
    Uri mNewUri;

    /** URI matcher code for the content URI for the news table */
    private static final int NEWS = 100;

    /** URI matcher code for the content URI for a single news item in the news table */
    private static final int NEWS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS, NEWS);
        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS + "/#", NEWS_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mNewsDbHelper = new NewsDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Gets readable database.
        SQLiteDatabase database = mNewsDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NEWS_ID:
                selection = NewsContract.NewsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the news table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return insertNews(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertNews(Uri uri, ContentValues values) {
        // Get writable database
        SQLiteDatabase database = mNewsDbHelper.getWritableDatabase();

        // Insert the new item with the given values
        long id = database.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion has failed. Log the error and return null;
        if (id == -1) {
            Log.d(TAG, "Failed to insert row for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Update method is not needed. It is not needed for this app to work properly.
        return 0;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        // Get writable database
        SQLiteDatabase database = mNewsDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NEWS_ID:
                // Delete a single row given by the ID in the URI
                selection = NewsContract.NewsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

}
