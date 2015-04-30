package com.blogspot.therightoveninc.codenamepuck;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jjgo on 4/27/15.
 * This abstract class is the basis for detailed views of
 * photos from the comments and history.
 */
public abstract class abstractPhotoDetails extends ActionBarActivity {
    protected ListView listView;
    protected String commentString;
    protected ArrayList<String> listValues;

    // Load the comments attached to the photo.
    protected abstract class GetCommentsAsyncTask extends AsyncTask<String, Void, Integer>
    {
        @Override
        protected Integer doInBackground(String... strings)
        {
            if (strings[0] == null) {
                commentString = null;
                return null;
            }
            commentString = strings[0];

            // change the url to get comments for a photo
            commentString = commentString.concat("/comments/");
            commentString = commentString.replace("/static","");
            try {
                URL oracle = new URL(commentString);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.length() > 0 && !inputLine.contains("<br/>")) {
                        listValues.add(inputLine);
                    }
                }
                in.close();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return 0;
        }
    }

    // let this get overloaded
    protected void refreshListView()
    {
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}

