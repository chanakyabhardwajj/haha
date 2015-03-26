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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanakyabhardwaj.haha.data.JokesContract;

/**
 * Created by cb on 3/24/15.
 */
public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = MainActivity.class.getSimpleName();

    Cursor mJokesCursor;
    JokesPagerAdapter mJokesPagerAdapter;
    ViewPager mViewPager;

    private void getJokes() {
        new JokesFetchTask(this).execute("meanjokes");
        new JokesFetchTask(this).execute("jokes");
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
        mJokesPagerAdapter.notifyDataSetChanged();
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

        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                view.setTranslationX(view.getWidth() * -position);

                if(position <= -1.0F || position >= 1.0F) {
                    view.setAlpha(0.0F);
                } else if( position == 0.0F ) {
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
                Log.v(LOG_TAG, "New page number : " + position);
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
            args.putInt("position", position);
            jokeFrag.setArguments(args);

            return jokeFrag;
        }

        @Override
        public int getCount() {
            return 100;
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
            View rootView = inflater.inflate(R.layout.joke_view, container, false);

            int[] backgrounds = getResources().getIntArray(R.array.backgrounds);


            String jokeTitle = getArguments().getString("title", "");
            TextView jokeTitleView = (TextView) rootView.findViewById(R.id.joke_title);
            jokeTitleView.setText(jokeTitle);

            String jokeText = getArguments().getString("text", "");
            TextView jokeTextView = (TextView) rootView.findViewById(R.id.joke_text);
            jokeTextView.setText(jokeText);

            Integer position = getArguments().getInt("position", 0);
            LinearLayout jokeLayout = (LinearLayout) rootView.findViewById(R.id.joke_layout);
            jokeLayout.setBackgroundColor(backgrounds[position % backgrounds.length]);

            return rootView;
        }
    }
}

