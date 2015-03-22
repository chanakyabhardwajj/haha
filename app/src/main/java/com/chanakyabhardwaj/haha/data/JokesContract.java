package com.chanakyabhardwaj.haha.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cb on 3/9/15.
 */

/**
 * Defines table and column names for the jokes database.
 */
public class JokesContract {

    public static final String CONTENT_AUTHORITY = "com.chanakyabhardwaj.haha";

    // Base of all URI's
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_JOKES = "jokes";

    /* Inner class that defines the table contents of the location table */
    public static final class JokesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_JOKES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_JOKES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_JOKES;

        // Table name
        public static final String TABLE_NAME = "jokes";

        // The joke id.
        public static final String COLUMN_JOKE_ID = "joke_id";

        // The title of the joke.
        public static final String COLUMN_JOKE_TITLE = "joke_title";

        // The text of the joke.
        public static final String COLUMN_JOKE_TEXT = "joke_text";

        public static Uri buildJokeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}