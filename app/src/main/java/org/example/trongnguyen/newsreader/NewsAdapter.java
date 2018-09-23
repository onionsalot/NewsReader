package org.example.trongnguyen.newsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.example.trongnguyen.newsreader.data.NewsContract;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsAdapter extends ArrayAdapter<News>{
    ViewHolder holder;
    Context mContext;
    int mLayout;
    ArrayList<String> mUids;
    public NewsAdapter(@NonNull Context context, int resource, @NonNull List<News> objects, int layout) {
        super(context, resource, objects);
        mUids = compareFavorites();
        mContext = context;
        mLayout = layout;
    }

    /*
     *
     * ViewHolder experimented with.
     * Initially, a ViewHolder was never created and this, each time a new View was activated,
     * the cpu would have to use findViewById to search for the view again which is very heavy on
     * computation. By using a ViewHolder, the views will only need to be found once, then recycled
     * for each subsequent find.
     *
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        // Initiate the holder
        holder = null;
        if(listItemView == null) {
            Log.d(TAG, "getView: holder initiated");
            if (mLayout == 0) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_items, parent, false);
            } else if (mLayout == 1){
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_items_small, parent, false);
            } else {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_items_large, parent, false);
            }
            holder = new ViewHolder();
            holder.newsTitle = (TextView) listItemView.findViewById(R.id.article_title);
            holder.newsAuthor = (TextView) listItemView.findViewById(R.id.article_author);
            holder.newsDescription = (TextView) listItemView.findViewById(R.id.article_desc);
            holder.itemPicture = (ImageView) listItemView.findViewById(R.id.article_image);
            holder.heartPicture = (LikeButton) listItemView.findViewById(R.id.heart_image);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        final News w = getItem(position);

        // Title of the Article
        holder.newsTitle.setText(w.getTitle());

        // Author of the Article. Followed by site scraped
        if (w.getAuthor().isEmpty()) {
            holder.newsAuthor.setText(w.getSource());
        } else {
            holder.newsAuthor.setText(w.getAuthor() + " - " + w.getSource());
        }
        /* Description of the Article. Description follows the format of
         * site - Date
         *    Main text
         */
        if (mLayout != 1) {
            holder.newsDescription.setText(w.getDate() + "-" + "\n" + w.getDescription());
        }

        // Picture
        /*
         Picasso framework is used to get images easily
         Picasso is unable to accept http responses so we convert
         the http: responses to https: responses.
         Some responses may vary. Picasso is loaded in via importing from
         the build.gradle.

         Callback() method used as a listener part of the new Picasso framework.
         Can be used to check for errors.

         If the callback returns an error, then to catch the error we pass in
         a temp clipart to not make the field empty.
          */
        if (w.getPicture().equals("") || w.getPicture().equals("null")) {
            holder.itemPicture.setImageResource(R.drawable.no_image_found);
        } else {
            Picasso.get()
                    .load(w.getPicture())
                    .centerCrop()
                    .fit()
                    .into(holder.itemPicture, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("error", "onError: ERRROROORORR" + e);
                        }
                    });
        }

        if (mUids.contains(w.getUuid())) {
            holder.heartPicture.setLiked(true);
        } else {
            holder.heartPicture.setLiked(false);
        }
        holder.heartPicture.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addFavorites(w);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                deleteFavorites(w);
            }
        });
        return listItemView;
    }

    private static class ViewHolder {
        public TextView newsTitle;
        public TextView newsAuthor;
        public TextView newsDescription;
        public ImageView itemPicture;
        public LikeButton heartPicture;
    }
    private void addFavorites(News position) {
        ContentValues values = new ContentValues();
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_UID, position.getUuid());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_NAME, position.getTitle());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_AUTHOR, position.getAuthor());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_DATE, position.getDate());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION, position.getDescription());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_SOURCE, position.getSource());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_TAGS, position.getTags());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_LINK, position.getUrl());
        values.put(NewsContract.NewsEntry.COLUMN_NEWS_PICTURE, position.getPicture());

        // Insert a new row into the provider using the ContentResolver. Use the CONTENT_URI to indicate
        // that we want to insert into the news DB table.
        // Receive the new content URI that will allow us to access data in the future.
        Uri newUri = getContext().getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI,values);
        Toast.makeText(getContext(), "Favorites has been added!",
                Toast.LENGTH_SHORT).show();
        mUids = compareFavorites();
    }

    private void deleteFavorites(News position) {
        String uuid = position.getUuid();
        String selection = NewsContract.NewsEntry.COLUMN_NEWS_UID + " = ?";
        int rowsDeleted = getContext().getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI,selection,new String[]{uuid});
        Toast.makeText(getContext(), rowsDeleted + " row deleted successfully!",
                Toast.LENGTH_SHORT).show();
        mUids = compareFavorites();
    }

    private ArrayList<String> compareFavorites() {
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

        return uids;
    }

    @Override
    public void notifyDataSetChanged() {
        mUids = compareFavorites();
        super.notifyDataSetChanged();
    }
}
