package com.chanakyabhardwaj.haha;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.chanakyabhardwaj.haha.data.JokesContract;
import com.chanakyabhardwaj.haha.data.JokesDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by cb on 3/9/15.
 */
public class JokesFetchTask extends AsyncTask<Integer, Void, Void> {
    private final String LOG_TAG = JokesFetchTask.class.getSimpleName();


    private JokesDBHelper dbHelper;
    private final Context mContext;
    private final Integer JOKES_COUNT;

    public JokesFetchTask(Context context, int count) {
        mContext = context;
        dbHelper = new JokesDBHelper(this.mContext);
        JOKES_COUNT = count;
    }


    @Override
    protected Void doInBackground(Integer... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jokesJsonStr = null;

        try {
            Log.v(LOG_TAG, "Fetching the funny from Reddit ... ;)");

            String lastJokeId = dbHelper.lastJokeInDB();
            String urlString = "https://www.reddit.com/r/jokes+meanjokes+antijokes+dadjokes/.json?sort=top&t=week&limit=" + JOKES_COUNT;
            if(lastJokeId != null) {
                urlString = urlString + "&after=t3_" + lastJokeId;
            }
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jokesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            addRedditJokesFromJson(jokesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    /*Reddit joke parser*/
    private void addRedditJokesFromJson(String jokesJsonStr) throws JSONException {

        JSONObject jokesJson = new JSONObject(jokesJsonStr);
        JSONObject data = jokesJson.getJSONObject("data");

        JSONArray jokesArray = data.getJSONArray("children");
        Log.v(LOG_TAG, "JSON dump has " + jokesArray.length() + " jokes");
        Vector<ContentValues> cVVector = new Vector<ContentValues>(JOKES_COUNT);

        for (int i = 0; i < JOKES_COUNT; i++) {
            JSONObject jokeObject = jokesArray.getJSONObject(i).getJSONObject("data");
            String jokeId = jokeObject.getString("id");
            String jokeTitle = jokeObject.getString("title");
            String jokeText = jokeObject.getString("selftext");

            ContentValues jokeValues = new ContentValues();
            jokeValues.put(JokesContract.JokesEntry.COLUMN_JOKE_ID, jokeId);
            jokeValues.put(JokesContract.JokesEntry.COLUMN_JOKE_TITLE, jokeTitle);
            jokeValues.put(JokesContract.JokesEntry.COLUMN_JOKE_TEXT, jokeText);
            cVVector.add(jokeValues);
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = mContext.getContentResolver()
                    .bulkInsert(JokesContract.JokesEntry.CONTENT_URI, cvArray);
            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of jokes");
            Log.v(LOG_TAG, "TOTAL  " + dbHelper.jokesCountInDB());
        }
    }
}