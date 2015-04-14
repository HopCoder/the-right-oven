package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jjgo on 4/13/15.
 */
public class comment extends ActionBarActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);

        listView = (ListView) findViewById(R.id.listView);

        if(phoneSettings.listValues.size() == 0)
        {
            phoneSettings.listValues = new ArrayList<String>();
            phoneSettings.listValues.add("secretString");

            String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                    "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                    "Linux", "OS/2", "Dick", "Butt", "Munchkin","Petty","argh",
                    "a really, really long comment is used here for testing purposes and stuff like that"};

            for (int i=0; i<values.length; i++)
            {
                phoneSettings.listValues.add(values[i]);
            }

            phoneSettings.listValues.add("theEndString");
        }

        refreshListView();
    }

    private void refreshListView()
    {
        commentAdapter adapter = new commentAdapter(this, phoneSettings.listValues.toArray(new String[phoneSettings.listValues.size()]));
        listView.setAdapter(adapter);
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

    public void addCommentClick(View v) {
        try {
            View parent = getViewByPosition(phoneSettings.listValues.size() - 1, listView);
            EditText editText = (EditText) parent.findViewById(R.id.editText);
            String newComment = editText.getText().toString();

            phoneSettings.listValues.remove(phoneSettings.listValues.size() - 1);
            phoneSettings.listValues.add(newComment);
            refreshListView();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
