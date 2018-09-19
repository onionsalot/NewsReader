package org.example.trongnguyen.newsreader.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.example.trongnguyen.newsreader.NewsAdapter;
import org.example.trongnguyen.newsreader.R;

import static android.content.ContentValues.TAG;

public class NewsCursorAdapter extends CursorAdapter{
    Context mContext;
    public NewsCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mContext = context;
        return LayoutInflater.from(context).inflate(R.layout.news_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView newsTitle = (TextView) view.findViewById(R.id.article_title);
        TextView newsAuthor = (TextView) view.findViewById(R.id.article_author);
        TextView newsDescription = (TextView) view.findViewById(R.id.article_desc);
        ImageView listItemPicture = (ImageView) view.findViewById(R.id.article_image);
        LikeButton likeButton = (LikeButton) view.findViewById(R.id.heart_image);
        // Move the cursor to the columns that we want
        int uidColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_UID);
        int nameColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_NAME);
        int authorColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_AUTHOR);
        int sourceColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_SOURCE);
        int dateColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_DATE);
        int descriptionColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION);
        int pictureColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_PICTURE);
        int tagsColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_TAGS);
        int linkColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_LINK);

        // Read the attributes for the news item at location ColumnIndex
        final String newsUidInfo = cursor.getString(uidColumnIndex);
        String newsTitleInfo = cursor.getString(nameColumnIndex);
        String newsAuthorInfo = cursor.getString(authorColumnIndex);
        String newsDateInfo = cursor.getString(dateColumnIndex);
        String newsSourceInfo = cursor.getString(sourceColumnIndex);
        String newsDescriptionInfo = cursor.getString(descriptionColumnIndex);
        String newsPictureInfo = cursor.getString(pictureColumnIndex);
        String newsTagsInfo = cursor.getString(tagsColumnIndex);
        String newsLinkInfo = cursor.getString(linkColumnIndex);

        newsTitle.setText(newsTitleInfo);
        if (newsAuthorInfo.isEmpty()) {
            newsAuthor.setText(newsSourceInfo);
        } else {
            newsAuthor.setText(newsAuthorInfo + " - " + newsSourceInfo);
        }
        newsDescription.setText(newsDateInfo + "-" + "\n" + newsDescriptionInfo);
        if (newsPictureInfo.equals("") || newsPictureInfo.equals("null")) {
            listItemPicture.setImageResource(R.drawable.ic_launcher_background);
        } else {
            Picasso.get()
                    .load(newsPictureInfo)
                    .centerCrop()
                    .fit()
                    .into(listItemPicture, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("error", "onError: ERRROROORORR" + e);
                        }
                    });
        }

        likeButton.setLiked(true);
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                // Do nothing. Should be liked by default.
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                deleteLiked(newsUidInfo);
            }
        });
    }

    private void deleteLiked(String uid) {
        Cursor cursor = getCursor();
        String uuid = uid;
        String selection = NewsContract.NewsEntry.COLUMN_NEWS_UID + " = ?";
        int rowsDeleted = mContext.getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI,selection,new String[]{uuid});
        Toast.makeText(mContext, rowsDeleted + " row deleted successfully!",
                Toast.LENGTH_SHORT).show();
    }

}
