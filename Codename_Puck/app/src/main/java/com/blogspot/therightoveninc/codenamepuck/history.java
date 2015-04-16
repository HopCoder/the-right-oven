package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jjgo on 4/14/15.
 */
public class history extends ActionBarActivity {
    private ListView listView;
    private Integer[] upCount;
    private String[] values;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        context = this;

        listView = (ListView) findViewById(R.id.listView);

        new GetHistoryAsyncTask().execute();
    }

    private class GetHistoryAsyncTask extends AsyncTask<URL,Void,Integer>
    {
        @Override
        protected Integer doInBackground(URL... urls)
        {
            // TODO: Get sql info as upCount and values
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            historyAdapter adapter = new historyAdapter(context, values, upCount);
            listView.setAdapter(adapter);
        }
    }
}