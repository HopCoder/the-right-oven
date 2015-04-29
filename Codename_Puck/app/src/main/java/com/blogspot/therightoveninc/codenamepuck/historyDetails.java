package com.blogspot.therightoveninc.codenamepuck;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jjgo on 4/27/15.
 */
public class historyDetails extends abstractPhotoDetails{
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        listView = (ListView) findViewById(R.id.listView);

        listValues = new ArrayList<String>();
        listValues.add("secretString");

        String commentAddress = phoneSettings.redirectedReceive.toString();

        new GetHistoryCommentsAsyncTask().execute(commentAddress);
        new GetBitmapAsyncTask().execute();
    }

    @Override
    protected void refreshListView()
    {
        historyDetailsAdapter adapter = new historyDetailsAdapter(this, listValues.toArray(new String[listValues.size()]));
        listView.setAdapter(adapter);
    }

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
                urlConnection = (HttpURLConnection) phoneSettings.redirectedReceive.openConnection();
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
                phoneSettings.historyBitmap = BitmapFactory.decodeStream(phoneSettings.redirectedReceive.openConnection().getInputStream(), null, options);
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

    protected class GetHistoryCommentsAsyncTask extends GetCommentsAsyncTask
    {
    }
}
