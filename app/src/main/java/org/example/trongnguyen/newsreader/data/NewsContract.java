package org.example.trongnguyen.newsreader.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NewsContract {
    public static final String CONTENT_AUTHORITY = "org.example.trongnguyen.newsreader";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "news";

    public static final class NewsEntry implements BaseColumns {
        public static final String TABLE_NAME = "news";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NEWS_UID = "uid";
        public static final String COLUMN_NEWS_NAME = "name";
        public static final String COLUMN_NEWS_AUTHOR = "author";
        public static final String COLUMN_NEWS_DATE = "date";
        public static final String COLUMN_NEWS_DESCRIPTION = "description";
        public static final String COLUMN_NEWS_SOURCE = "source";
        public static final String COLUMN_NEWS_TAGS = "tags";
        public static final String COLUMN_NEWS_LINK = "link";
        public static final String COLUMN_NEWS_PICTURE = "picture";


    }
}
