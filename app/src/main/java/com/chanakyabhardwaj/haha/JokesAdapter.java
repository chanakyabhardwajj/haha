package com.chanakyabhardwaj.haha;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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

    private int counter = 0;
    private Drawable backgroundImage;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView =  LayoutInflater.from(context).inflate(R.layout.list_item_joke, parent, false);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idx_jokes_title = cursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TITLE);
        String jokeTitle = cursor.getString(idx_jokes_title);

        int idx_jokes_text = cursor.getColumnIndex(JokesContract.JokesEntry.COLUMN_JOKE_TEXT);
        String jokeText = cursor.getString(idx_jokes_text);


        TextView jokeTitleView = (TextView) view.findViewById(R.id.list_item_joke_view);
        jokeTitleView.setText(jokeTitle + " \n " + jokeText);


        if (counter%2 == 1) {
            jokeTitleView.setBackgroundColor(context.getResources().getColor(R.color.default_primary));
            jokeTitleView.getBackground().setAlpha(50);
            //jokeTitleView.setBackgroundResource(R.drawable.bean);
        } else {
            //jokeTitleView.setBackgroundResource(R.drawable.freddie);
        }


        counter = counter+1;

    }
}
