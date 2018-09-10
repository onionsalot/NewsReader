package org.example.trongnguyen.newsreader;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from the API.
 * Used to basically relieve the main activity from being flooded by code.
 * The main activity will still contain the Async task but instead delagate info to here
 * for parsing.
 */
public final class QueryTools {

    private static final String TAG = "--------QueryTag---";

    /**
     * Create a private constructor because no one should ever create a {@link QueryTools} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryTools() {
    }

    public static ArrayList<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        Log.d("QueryUtils.Java", "doInBackground: Current URL " + url);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO handle IO exception
        }

        ArrayList<News> items = extractFeatureFromJson(jsonResponse);

        return items;
    }

    public static URL createUrl(String googleBooksApi) {
        URL url = null;
        try {
            url = new URL(googleBooksApi);
        } catch (MalformedURLException e) {
            Log.e("Error", "createUrl: Error with creating URL" + e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            // Makes the connection. Everything before this line is to set up the connection.
            urlConnection.connect();

            // Once the connection has been established, we check if the response is valid.
            // Response will be an int and we check if it is a 200 int (connection success).

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            // TODO handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        // Create a StringBuilder. Strings cannot be changed once its created, it can only be completely
        // replaced IE via concatnation. A StringBuilder can be changed after it is created using
        // methods created for the builder. IE .append,.delete. The StringBuilder can then be frozen
        // into a String using the builder.toString() method.

        // Setup Builder
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            // Setup Reader
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Ask the reader for a line of Text
            String line = reader.readLine();
            // Append result to the end of the String line if the results isn't null. Then move onto the next
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        // Once the reader has run out of lines, it will finalize the string and pass it back to the makeHttpRequest
        return output.toString();
    }


    private static ArrayList<News> extractFeatureFromJson(String jsonResponse) {
        // If the JSON string is empty or null from the makeHttp method we can use TextUtils to check.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            ArrayList<News> baseArray = new ArrayList<News>();
            Log.d(TAG, "extractFeatureFromJson: totalResults found= " + baseJsonResponse.getInt("totalResults"));
            // Another layer of checks to make sure newsArray "posts" is not empty which will cause a crash
            if (baseJsonResponse.getInt("totalResults") == 0) {
                // TODO: Create case for returning 0 items
            } else {
                JSONArray newsArray = baseJsonResponse.getJSONArray("posts");
                // Checks to see how many posts are returned. Will be using the user preferences
                // to get the amount of results the user wishes to display on the screen at a time.
                if (newsArray.length() > 0) {
                    int resultsNum;
                    if (newsArray.length() > 40) {
                        resultsNum = 40;
                    } else {
                        resultsNum = newsArray.length();
                    }

                    // Begin the process of grabbing the items. First loop through each item.
                    for(int i = 0; i < resultsNum; i++) {
                        // Gets the the object position of newsArray then checks for "thread." As per the API,
                        // Post > Thread > 'others'
                        JSONObject initialItem = newsArray.getJSONObject(i);
                        JSONObject itemThreads = initialItem.getJSONObject("thread");

                        // Extract out the title values
                        // Created a new JSONObject named firstItem so we can pull info from within the baseJsonResponse Array
                        // The following are the fields that we are going to grab
                        String title;
                        String author;
                        String published; // Date field
                        String text; // Description field
                        String site;
                        String main_image;
                        String url;
                        String tags;
                        /*
                         * Checks if there is a "title" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (itemThreads.has("title")) {
                            title = itemThreads.getString("title");
                        } else {
                            title = "";
                        }

                        /*
                         * Checks if there is a "author" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (initialItem.has("author")) {
                            author = initialItem.getString("author");
                        } else {
                            author = "";
                        }

                        /*
                         * Checks if there is a "published" field in the JSON array..
                         * 'Has' method is check if a field is there. If it is not then create a
                         * text generic response.
                         */
                        if (itemThreads.has("published")) {
                            String date = itemThreads.getString("published");
                            String[] separated = date.split("T");
                            published = separated[0] + " at " + separated[1].replace(".000"," ");
                        } else {
                            published = "";
                        }


                        /*
                         * Checks if there is a "text" field in the JSON array..
                         * text is the main description. A lot of articles don't seem to have descriptions
                         */
                        if (initialItem.has("text")) {
                            text = initialItem.getString("text");
                        } else {
                            text = "";
                        }

                        /*
                         * Checks if there is a "site" field in the JSON array..
                         * this will display the site that published the article.
                         */
                        if (itemThreads.has("site")) {
                            site = itemThreads.getString("site");
                        } else {
                            site = "";
                        }

                        /*
                         * Checks if there is a "main_image" field in the JSON array..
                         * url is then passed back to PICASSO to display to the user.
                         */
                        if (itemThreads.has("main_image")) {
                            main_image = itemThreads.getString("main_image");
                        } else {
                            main_image = "";
                        }

                        /*
                         * Checks if there is a "url" field in the JSON array..
                         * url is used to fetch the URL which will redirect the user to app browser
                         */
                        if (itemThreads.has("url")) {
                            url = itemThreads.getString("url");
                        } else {
                            url = "";
                        }

                        /*
                         * Checks if there is a "site_categories" field in the JSON array..
                         * This field is also an array in it of itself, so we must treat it as such.
                         * url is used to fetch the URL which will redirect the user to app browser
                         */
                        if (itemThreads.has("site_categories")) {
                            JSONArray siteCategories = itemThreads.getJSONArray("site_categories");
                            /*
                            *
                            * Once we established that there is a "site_categories" field in the "threads" object
                            * in position p of the siteCategories, we extract the String.
                            *
                            * In site_categories situation the JSON looked like this;
                            * "site_categories": [
                            * "financial_news",
                            * "finance"
                            * ]
                            * {} is an object and [] is an array of items. We just grab the string of arrays inside
                            *
                            * I am aware that this was unnecessary and could be done simply by manipulating
                            * the strings to remove the [] instead but the following is a good layout for future
                            * items that require a more complex string building.
                            *
                             */
                            StringBuilder sp = new StringBuilder();
                            if (siteCategories.length() > 0) {
                                for (int p = 0; p < siteCategories.length(); p++) {
                                    if (sp.length() == 0) {
                                        sp.append(siteCategories.getString(p));
                                    } else {
                                        sp.append(", ");
                                        sp.append(siteCategories.getString(p));
                                    }
                                }
                            }
                            tags = sp.toString();
                        } else {
                            tags = "";
                        }
                        // Add the info obtained into the baseObject.
                        baseArray.add(new News(title, author, published, site, text, main_image, url, tags));
                        }
                            // Create the object and return it
                            return baseArray;
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

