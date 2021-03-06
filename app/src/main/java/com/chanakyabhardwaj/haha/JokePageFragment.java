package com.chanakyabhardwaj.haha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;

/**
 * Created by chanakya.bharwaj on 28/03/15.
 */

public class JokePageFragment extends Fragment {
    private String jokeTitle;
    private String jokeText;
    private int position;
    private ShareActionProvider mShareActionProvider;

    public JokePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.joke_view, container, false);

        int[] backgrounds = getResources().getIntArray(R.array.backgrounds);

        jokeTitle = getArguments().getString("title", "");
        TextView jokeTitleView = (TextView) rootView.findViewById(R.id.joke_title);
        jokeTitleView.setText(Html.fromHtml(jokeTitle));

        jokeText = getArguments().getString("text", "");
        TextView jokeTextView = (TextView) rootView.findViewById(R.id.joke_text);
        jokeTextView.setText(Html.fromHtml(jokeText));

        position = getArguments().getInt("position", 0);
        jokeTitleView.setBackgroundColor(backgrounds[position % backgrounds.length]);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_joke, menu);

        MenuItem shareButton = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareButton);
        mShareActionProvider.setShareIntent(shareJokeIntent());
    }

    private Intent shareJokeIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, jokeTitle + "\n" + jokeText);
        sendIntent.setType("text/plain");
        return sendIntent;
    }
}