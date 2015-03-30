package com.chanakyabhardwaj.haha;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
public class JokesFetchTask extends AsyncTask<Integer, Void, Boolean> {
    private final String LOG_TAG = JokesFetchTask.class.getSimpleName();

    private JokesDBHelper dbHelper;
    private final Context mContext;
    private final Integer JOKES_COUNT;
    private Exception error;

    public JokesFetchTask(Context context, int count) {
        mContext = context;
        dbHelper = new JokesDBHelper(this.mContext);
        JOKES_COUNT = count;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jokesJsonStr = null;

        try {
            String lastJokeId = dbHelper.lastJokeInDB();
            String urlString = "https://www.reddit.com/r/jokes+meanjokes/.json?sort=hot&limit=" + JOKES_COUNT;

            if (lastJokeId != null && !lastJokeId.isEmpty()) {
                urlString = urlString + "&after=t3_" + lastJokeId;
            }

            Log.v(LOG_TAG, "URL : " + urlString);

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

            try {
                addRedditJokesFromJson(jokesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            error = e;
            return false;
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
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            Toast.makeText(mContext, "No internet. No funny.", Toast.LENGTH_LONG).show();
            Log.v(LOG_TAG, error.toString());
        }
    }

    /*Reddit joke parser*/
    private void addRedditJokesFromJson(String jokesJsonStr) throws JSONException {

        JSONObject jokesJson = new JSONObject(jokesJsonStr);
        JSONObject data = jokesJson.getJSONObject("data");

        JSONArray jokesArray = data.getJSONArray("children");
        Vector<ContentValues> cVVector = new Vector<ContentValues>(JOKES_COUNT);

        for (int i = 0; i < jokesArray.length(); i++) {

            JSONObject jokeObject = jokesArray.getJSONObject(i).getJSONObject("data");
            String jokeId = jokeObject.getString("id");
            String jokeTitle = jokeObject.getString("title");
            String jokeText = jokeObject.getString("selftext");

            Log.v(LOG_TAG, "Joke : " + jokeTitle);

            ContentValues jokeValues = new ContentValues();
            jokeValues.put(JokesContract.JokesEntry.COLUMN_JOKE_ID, jokeId);
            jokeValues.put(JokesContract.JokesEntry.COLUMN_JOKE_TITLE, jokeTitle);
            jokeValues.put(JokesContract.JokesEntry.COLUMN_JOKE_TEXT, jokeText);
            cVVector.add(jokeValues);
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver()
                    .bulkInsert(JokesContract.JokesEntry.CONTENT_URI, cvArray);
        }
    }
}