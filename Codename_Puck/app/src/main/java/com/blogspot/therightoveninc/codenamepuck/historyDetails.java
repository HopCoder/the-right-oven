package com.blogspot.therightoveninc.codenamepuck;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jjgo on 4/27/15.
 * This class represents the activity when looking at
 * the comments of a user's previous post.
 */
public class historyDetails extends abstractPhotoDetails{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        listView = (ListView) findViewById(R.id.listView);

        listValues = new ArrayList<String>();
        listValues.add("secretString");

        String commentAddress = phoneSettings.redirectedHistory.toString();

        new GetHistoryCommentsAsyncTask().execute(commentAddress);
        new GetBitmapAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_delete:
                new DeletePhotoAsyncTask().execute(phoneSettings.redirectedHistory);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DeletePhotoAsyncTask extends AsyncTask<URL, Void, Integer>
    {
        @Override
        protected Integer doInBackground(URL... urls)
        {
            try
            {
            if (phoneSettings.redirectedHistory == null)
                return -1;
            String deleteString = phoneSettings.redirectedHistory.toString().replace("static", "delete");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(deleteString);
            HttpResponse response = httpclient.execute(httpGet);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return -1;
            }
            catch (IOException e) {
                e.printStackTrace();
                return -1;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (result == 0) {
                // finish() is what the back button does
                finish();
            }
            return;
        }
    }

    @Override
    protected void refreshListView()
    {
        historyDetailsAdapter adapter = new historyDetailsAdapter(this, listValues.toArray(new String[listValues.size()]));
        listView.setAdapter(adapter);
    }

    // cache the image that is being viewed
    private class GetBitmapAsyncTask extends AsyncTask<URL, Void, Integer>
    {
        HttpURLConnection urlConnection;

        @Override
        protected Integer doInBackground(URL... urls)
        {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                urlConnection = (HttpURLConnection) phoneSettings.redirectedHistory.openConnection();
                BitmapFactory.decodeStream(urlConnection.getInputStream(), null, options);
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
                // no more files
                return -1;
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return -1;
            }

            // Calculate inSampleSize
            options.inSampleSize = math.calculateInSampleSize(options, phoneSettings.xPixels, phoneSettings.xPixels);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            try {
                phoneSettings.historyBitmap = BitmapFactory.decodeStream(phoneSettings.redirectedHistory.openConnection().getInputStream(), null, options);
            }
            catch (Exception e)
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

    // need a class to not be abstract, no need to overload anything
    protected class GetHistoryCommentsAsyncTask extends GetCommentsAsyncTask
    {
    }
}
