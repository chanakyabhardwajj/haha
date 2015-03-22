package com.chanakyabhardwaj.haha;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.chanakyabhardwaj.haha.data.JokesContract;

/**
 * Created by cb on 3/9/15.
 */
public class JokesAdapter extends CursorAdapter {

    public JokesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_joke, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idx_jokes_title = cursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TITLE);
        String jokeTitle = cursor.getString(idx_jokes_title);

        TextView jokeTitleView = (TextView) view.findViewById(R.id.list_item_joke_title_view);
        jokeTitleView.setText(jokeTitle);

        int idx_jokes_text = cursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TEXT);
        String jokeText = cursor.getString(idx_jokes_text);

        TextView jokeTextView = (TextView) view.findViewById(R.id.list_item_joke_text_view);
        jokeTextView.setText(Html.fromHtml(jokeText));
    }
}
