package com.chanakyabhardwaj.haha.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chanakyabhardwaj.haha.data.JokesContract.JokesEntry;

/**
 * Created by cb on 3/9/15.
 */

public class JokesDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "jokes.db";

    public JokesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold jokes. A joke consists of :
        // id, title, text
        final String SQL_CREATE_JOKES_TABLE = "CREATE TABLE " + JokesEntry.TABLE_NAME + " (" +
                JokesEntry._ID + " INTEGER PRIMARY KEY," +
                JokesEntry.COLUMN_JOKE_ID + " TEXT NOT NULL," +
                JokesEntry.COLUMN_JOKE_TITLE + " TEXT NOT NULL," +
                JokesEntry.COLUMN_JOKE_TEXT + " TEXT NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_JOKES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JokesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JokesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public boolean jokeExistsInDB(String jokeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + JokesContract.JokesEntry.TABLE_NAME + " where " + JokesContract.JokesEntry.COLUMN_JOKE_ID + " = '" + jokeId + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    /*
        Helper function for debugging purposes. Not really used.
     */
    public int jokesCountInDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + JokesContract.JokesEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        return cursor.getCount();
    }
}
