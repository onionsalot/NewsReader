package org.example.trongnguyen.newsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsAdapter extends ArrayAdapter<News>{
    ViewHolder holder;
    Context mContext;
    public NewsAdapter(@NonNull Context context, int resource, @NonNull List<News> objects) {
        super(context, resource, objects);
        mContext = context;
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
        Log.d(TAG, "getView called. New listView added most likely.");
        if(listItemView == null) {
            Log.d(TAG, "getView: holder initiated");
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_items,parent, false);
            holder = new ViewHolder();
            holder.newsTitle = (TextView) listItemView.findViewById(R.id.article_title);
            holder.newsAuthor = (TextView) listItemView.findViewById(R.id.article_author);
            holder.newsDescription = (TextView) listItemView.findViewById(R.id.article_desc);
            holder.itemPicture = (ImageView) listItemView.findViewById(R.id.article_image);
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
        holder.newsDescription.setText(w.getDate() + "-" + "\n" + w.getDescription());

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
            holder.itemPicture.setImageResource(R.drawable.ic_launcher_background);
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
        return listItemView;
    }

    private static class ViewHolder {
        public TextView newsTitle;
        public TextView newsAuthor;
        public TextView newsDescription;
        public ImageView itemPicture;
    }
}
