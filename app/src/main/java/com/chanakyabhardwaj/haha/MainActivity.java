package com.chanakyabhardwaj.haha;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chanakyabhardwaj.haha.data.JokesContract;
import com.chanakyabhardwaj.haha.data.JokesDBHelper;

/**
 * Created by cb on 3/24/15.
 */
public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String PREFS_NAME = "JokesPrefsFile";
    private int pageNumber; //tracks the page/joke the user is on.

    Cursor mJokesCursor;
    JokesPagerAdapter mJokesPagerAdapter;
    ViewPager mViewPager;
    private int JOKES_COUNT;

    private void getJokes() {
        //Fetch only 10 jokes every time.
        //This will ensure that a user sees only 9 stale jokes upon her revisit.
        new JokesFetchTask(this, 10).execute();
    }

    private void reset() {
        pageNumber = 0;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("pageNumber", pageNumber);
        editor.commit();

        JokesDBHelper dbHelper = new JokesDBHelper(getApplicationContext());
        Log.v("Before Delete all", Integer.toString(dbHelper.jokesCountInDB()));
        dbHelper.deleteAll();
        Log.v("After Delete all", Integer.toString(dbHelper.jokesCountInDB()));

        getSupportLoaderManager().restartLoader(0, null, this);
    }

    //Track the last joke the user was reading
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("pageNumber", pageNumber);
        editor.commit();
    }

    //Resume from the last joke the user was reading
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        pageNumber = settings.getInt("pageNumber", 0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        getJokes();
        Uri jokesUri = JokesContract.JokesEntry.CONTENT_URI;
        return new CursorLoader(this, jokesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && mJokesCursor != data) {
            mJokesCursor = data;
            JOKES_COUNT = mJokesCursor.getCount();
            if (JOKES_COUNT > 0) {
                mJokesPagerAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(pageNumber);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mJokesCursor.close();
        mJokesCursor = null;
    }

    //Helper function
    private String[] extractJokeFromCursor(int position) {
        mJokesCursor.moveToPosition(position);

        int idx_jokes_title = mJokesCursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TITLE);
        String jokeTitle = mJokesCursor.getString(idx_jokes_title);

        int idx_jokes_text = mJokesCursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TEXT);
        String jokeText = mJokesCursor.getString(idx_jokes_text);

        return new String[]{jokeTitle, jokeText};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(0, null, this);

        setContentView(R.layout.viewpager);

        mJokesPagerAdapter = new JokesPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mJokesPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                pageNumber = position;
                if (JOKES_COUNT - position < 2) {
                    getJokes();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class JokesPagerAdapter extends FragmentStatePagerAdapter {
        public JokesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            String jokeTitle = "";
            String jokeText = "";

            if (mJokesCursor != null && mJokesCursor.getCount() > 0) {
                String[] joke = extractJokeFromCursor(position);
                jokeTitle = joke[0];
                jokeText = joke[1];
            } else {
                Toast.makeText(getApplicationContext(), "No internet. No funny.", Toast.LENGTH_LONG).show();
            }

            JokePageFragment jokeFrag = new JokePageFragment();
            Bundle args = new Bundle();
            args.putString("title", jokeTitle);
            args.putString("text", jokeText);
            args.putInt("position", position);
            jokeFrag.setArguments(args);

            return jokeFrag;
        }

        @Override
        public int getCount() {
            return JOKES_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}

