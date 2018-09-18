package org.example.trongnguyen.newsreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.example.trongnguyen.newsreader.data.NewsContract.NewsEntry;

public class NewsDbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "newsapp.db";
    public static final int DATABASE_VERSION = 1;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the news table
        String SQL_CREATE_NEWS_TABLE =  "CREATE TABLE " + NewsEntry.TABLE_NAME + " ("
                + NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewsEntry.COLUMN_NEWS_UID + " INTEGER NOT NULL, "
                + NewsEntry.COLUMN_NEWS_NAME + " TEXT NOT NULL, "
                + NewsEntry.COLUMN_NEWS_AUTHOR + " TEXT, "
                + NewsEntry.COLUMN_NEWS_DATE + " TEXT, "
                + NewsEntry.COLUMN_NEWS_DESCRIPTION + " TEXT, "
                + NewsEntry.COLUMN_NEWS_SOURCE + " TEXT, "
                + NewsEntry.COLUMN_NEWS_TAGS + " TEXT, "
                + NewsEntry.COLUMN_NEWS_LINK + " TEXT, "
                + NewsEntry.COLUMN_NEWS_PICTURE + " TEXT); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_NEWS_TABLE);
    }
    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
