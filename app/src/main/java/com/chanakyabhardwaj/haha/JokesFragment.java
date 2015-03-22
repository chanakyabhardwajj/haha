package com.chanakyabhardwaj.haha;

/**
 * Created by cb on 3/7/15.
 */


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.chanakyabhardwaj.haha.data.JokesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class JokesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public JokesFragment() {
    }

    private JokesAdapter jokesAdapter;

    private void getJokes() {
        new JokesFetchTask(getActivity()).execute(30);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        getJokes();

        Uri jokesUri = JokesContract.JokesEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), jokesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        jokesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        jokesAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        jokesAdapter = new JokesAdapter(getActivity(), null, 0);
        ListView jokesListView = (ListView) rootView.findViewById(R.id.listview_jokes);
        jokesListView.setAdapter(jokesAdapter);

        return rootView;
    }
}