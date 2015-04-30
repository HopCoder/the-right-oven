package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjgo on 4/14/15.
 */
public class history extends ActionBarActivity {
    private ListView listView;
    private Context context;
    private String urlString;
    private List<String> listHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        context = this;

        listView = (ListView) findViewById(R.id.listView);

        urlString = "http://52.10.111.12:8000/" + phoneSettings.phoneNum + "/history/";
    }

    @Override
    protected void onResume()
    {
        listHistory = new ArrayList<String>();
        new GetHistoryAsyncTask().execute();
        super.onResume();

    }

    private void refreshListView()
    {
        historyAdapter adapter = new historyAdapter(context, listHistory.toArray(new String[listHistory.size()]));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                String itemUrl = urlString.replace("history", Integer.toString(position));
                new HistoryDetailsAsyncTask().execute(itemUrl);
            }

        });
    }

    private class HistoryDetailsAsyncTask extends AsyncTask<String,Boolean,Integer>
    {
        @Override
        protected Integer doInBackground(String... strings)
        {
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(strings[0]);

                // initialize a url connection
                urlConnection = (HttpURLConnection) url.openConnection();

                // get redirect url on next attempt
                urlConnection.setInstanceFollowRedirects(true);

                // request redirect
                InputStream is = urlConnection.getInputStream();

                phoneSettings.redirectedReceive = urlConnection.getURL();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            // need function to change context out of thread
            getDetails();
        }
    }

    private void getDetails()
    {
        if(null != phoneSettings.redirectedReceive) {
            Intent i = new Intent(this, historyDetails.class);
            startActivity(i);
        }
    }

    private class GetHistoryAsyncTask extends AsyncTask<URL,Void,Integer>
    {
        @Override
        protected Integer doInBackground(URL... urls)
        {
            try {
                URL oracle = new URL(urlString);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.length() > 0 && !inputLine.contains("<br/>")) {
                        listHistory.add(inputLine);
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

        @Override
        protected void onPostExecute(Integer result)
        {
            refreshListView();
        }
    }
}