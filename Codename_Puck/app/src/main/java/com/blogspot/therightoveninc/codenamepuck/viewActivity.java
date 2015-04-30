package com.blogspot.therightoveninc.codenamepuck;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class viewActivity extends ActionBarActivity {
    private PopupWindow popupWindow;
    private ImageButton imageButton;
    private URL url;
    private float previousX, previousY;
    private final float swipeThres = 10f;
    private boolean puckshuck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view);
        findViewById(R.id.imageButton).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                Log.d("onTouch:", "x:" + x);
//        float y = event.getY();
                float dx = previousX - x;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("onTouch:", "dx:" + dx);
                        if (Math.abs(dx) > swipeThres && !puckshuck) {
                            if(dx < 0){
                                messageDelete();
                                Log.d("onTouch:", "shuck" + dx);
                            }else{
                                new PuckItAsyncTask().execute();
                                Log.d("onTouch:", "puck" + dx);
                            }
                            puckshuck = true;
                        }
                        break;
                    case MotionEvent.ACTION_DOWN://
                        break;
                }
                previousX = x;
//        previousY = y;
                return true;
            }
        });
        imageButton = (ImageButton)findViewById(R.id.imageButton);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Do we need a new photo or do we already have one?
        if (null == phoneSettings.redirectedReceive)
        {
            new GetImageAsyncTask().execute();
        }
        else
        {
            imageButton.setBackgroundColor(Color.parseColor("#942CFF"));
            imageButton.setImageBitmap(phoneSettings.currentBitmap);
        }
    }

    private class GetImageAsyncTask extends AsyncTask<URL, Void, Integer>
    {
        protected Integer doInBackground(URL... urls) {
            try
            {
                url = new URL("http://52.10.111.12:8000/view/" + phoneSettings.phoneNum + "/69/34/10/");
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            new DecodeSampledBitmapFromStream().execute();
        }
    }

    private class DecodeSampledBitmapFromStream extends AsyncTask<URL, Void, Integer>
    {
        HttpURLConnection urlConnection;
        InputStream is;
        protected Integer doInBackground(URL... urls)
        {
            ViewGroup.LayoutParams imageParams = imageButton.getLayoutParams();

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                is = urlConnection.getInputStream();
                phoneSettings.redirectedReceive = urlConnection.getURL();
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
            options.inSampleSize = math.calculateInSampleSize(options, imageParams.width, imageParams.height);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            try {
                phoneSettings.currentBitmap = BitmapFactory.decodeStream(phoneSettings.redirectedReceive.openConnection().getInputStream(), null, options);
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
            if (result == -1) {
                imageButton.setBackgroundColor(Color.parseColor("#000000"));
                imageButton.setImageResource(android.R.color.holo_red_dark);
                if (null != phoneSettings.redirectedReceive) {
                    phoneSettings.redirectedReceive = null;
                }
            }
            else
            {
                imageButton.setBackgroundColor(Color.parseColor("#942CFF"));
                imageButton.setImageBitmap(phoneSettings.currentBitmap);
            }
            puckshuck = false;
        }
    }

    public void puckClick(View v)
    {
        if (!puckshuck){
            puckshuck = true;
            new PuckItAsyncTask().execute();
        }
    }

    private class PuckItAsyncTask extends AsyncTask<URL, Void, Integer>
    {
        @Override
        protected Integer doInBackground(URL... urls) {
            try {
                if (phoneSettings.redirectedReceive == null)
                    return null;
                String puck_string = phoneSettings.redirectedReceive.toString().replace("static", "puck_up");

                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(puck_string);
                HttpResponse response = httpclient.execute(httpGet);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            messageDelete();
        }
    }

    public void shuckClick(View v)
    {
        if (!puckshuck){
            puckshuck = true;
            messageDelete();
        }

    }

    public void commentClick(View v)
    {
        if(null != phoneSettings.redirectedReceive) {
            Intent i = new Intent(this, comment.class);
            startActivity(i);
        }
    }

    // Popup code (mostly) starts here
    public void reportClick()
    {
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.report, null);
        popupWindow = new PopupWindow(
                popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
        popupWindow.setOutsideTouchable(false);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public void reportConfirmClick(View v)
    {
        // TODO: add user report logic here

        popupWindow.dismiss();
        messageDelete();
    }

    public void reportCancelClick(View v)
    {
        popupWindow.dismiss();
    }

    public void onCameraClick(View v){
        Intent cameraIntent = new Intent(this, cameraActivity.class);
        startActivity(cameraIntent);
    }

    private void messageDelete()
    {
        phoneSettings.redirectedReceive = null;

        new GetImageAsyncTask().execute();
    }
}
