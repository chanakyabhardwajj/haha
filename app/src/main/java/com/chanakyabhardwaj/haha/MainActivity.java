package com.chanakyabhardwaj.haha;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chanakyabhardwaj.haha.data.JokesContract;

/**
 * Created by cb on 3/24/15.
 */
public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = MainActivity.class.getSimpleName();

    JokesPagerAdapter mJokesPagerAdapter;
    protected Cursor mJokesCursor;
    ViewPager mViewPager;

    private void getJokes() {
        new JokesFetchTask(this).execute();
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mJokesCursor = null;
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
                Log.v(LOG_TAG, "New page number : " + position);
            }
        });
    }

    public class JokesPagerAdapter extends FragmentStatePagerAdapter {
        public JokesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String jokeTitle = "fake joke title " + position;
            String jokeText = "fake joke text " + position;

            if (mJokesCursor != null && mJokesCursor.getCount() > 0) {
                mJokesCursor.moveToPosition(position);
                int idx_jokes_title = mJokesCursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TITLE);
                jokeTitle = mJokesCursor.getString(idx_jokes_title);

                int idx_jokes_text = mJokesCursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TEXT);
                jokeText = mJokesCursor.getString(idx_jokes_text);
            }

            JokePageFragment jokeFrag = new JokePageFragment();
            Bundle args = new Bundle();
            args.putString("title", jokeTitle);
            args.putString("text", jokeText);
            jokeFrag.setArguments(args);

            return jokeFrag;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 20;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    public static class JokePageFragment extends Fragment {
        public JokePageFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.list_item_joke, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.list_item_joke_view);

            String jokeTitle = getArguments().getString("title", "fake default title");
            String jokeText = getArguments().getString("text", "fake default text");

            dummyTextView.setText(jokeTitle + " \n " + jokeText);
            return rootView;
        }
    }
}

