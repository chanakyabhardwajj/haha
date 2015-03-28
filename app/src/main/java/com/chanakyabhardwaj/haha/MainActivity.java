package com.chanakyabhardwaj.haha;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.chanakyabhardwaj.haha.data.JokesContract;

/**
 * Created by cb on 3/24/15.
 */
public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String PREFS_NAME = "JokesPrefsFile";
    private String LOG_TAG = MainActivity.class.getSimpleName();

    Cursor mJokesCursor;
    JokesPagerAdapter mJokesPagerAdapter;
    ViewPager mViewPager;
    private int JOKES_COUNT = 10;
    private int pageNumber;

    private void getJokes() {
        new JokesFetchTask(this, JOKES_COUNT).execute();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        getJokes();

        Uri jokesUri = JokesContract.JokesEntry.CONTENT_URI;
        return new CursorLoader(this, jokesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mJokesCursor = data;
        JOKES_COUNT = mJokesCursor.getCount();
        mJokesPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(pageNumber);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mJokesCursor = null;
        mJokesCursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        pageNumber = settings.getInt("pageNumber", 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("pageNumber", pageNumber);
        editor.commit();
    }

    private String[] extractJokeFromCursor(int position) {
        mJokesCursor.moveToPosition(position);

        int idx_jokes_title = mJokesCursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TITLE);
        String jokeTitle = mJokesCursor.getString(idx_jokes_title);

        int idx_jokes_text = mJokesCursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TEXT);
        String jokeText = mJokesCursor.getString(idx_jokes_text);

        return new String[] {jokeTitle, jokeText};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(0, null, this);

        setContentView(R.layout.viewpager);

        mJokesPagerAdapter = new JokesPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mJokesPagerAdapter);

        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                view.setTranslationX(view.getWidth() * -position);

                if (position <= -1.0F || position >= 1.0F) {
                    view.setAlpha(0.0F);
                } else if (position == 0.0F) {
                    view.setAlpha(1.0F);
                } else {
                    // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                    view.setAlpha(1.0F - Math.abs(position));
                }
            }
        });

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
            return JOKES_COUNT > 0 ? JOKES_COUNT : 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}

