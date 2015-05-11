package com.blogspot.therightoveninc.codenamepuck;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

/*
    This class represents the activity for browsing photos.
 */
public class viewActivity extends ActionBarActivity {
    private PopupWindow popupWindow; //A popup window that creates a report window to report certain photos.
    private ImageButton imageButton; //The image being presented to the user.
    private URL url; //the url for grabbing the most recent posts from the server
    private float previousX, previousY; //the values for the previous x,y coordinates of the touch gesture used for calculating the difference between the most recent x, y tap.
    private final float swipeThres = 10f; //the threshold for detecting a movement as a swipe as opposed to a swipe
    private final float tapThres = 0.00001f; //the threshold for detecting a tap as opposed to a swipe
    private boolean puckshuck = false; //boolean for blocking asynchronous tasks for pucking and shucking photos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view);

        // Swipe detection
        findViewById(R.id.imageButton).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                float dx = previousX - x;
                float dy = y - previousY;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(dx) < tapThres && Math.abs(dy) < tapThres && !puckshuck)
                            commentClick();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (Math.abs(dx) > swipeThres && !puckshuck) {
                            if(dx > 0){
                                messageDelete();
                            }else{
                                new PuckItAsyncTask().execute();
                            }
                            puckshuck = true;
                        }
                        break;
                    case MotionEvent.ACTION_DOWN://
                        break;
                }
                previousX = x;
                previousY = y;
                return true;
            }
        });
        imageButton = (ImageButton)findViewById(R.id.imageButton);
    }

    //on resume method responsible for getting the lastest post from the server and
    //filling it into the imagaebutton container to be presented to the user.
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

    // Get the next photo, anything with urls must be Async.
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

    // Scale and load the photo.
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
                // get the redirected url
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                is = urlConnection.getInputStream();
                phoneSettings.redirectedReceive = urlConnection.getURL();

                // create a BitmapFactory to get photo parameters
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
            // If there's no more photos, set a red background
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

    // Button implementation for Pucking
    public void puckClick(View v)
    {
        if (!puckshuck){
            puckshuck = true;
            new PuckItAsyncTask().execute();
        }
    }

    // Notify server of a Puck
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

    //the shuck button callback for deleting a message
    public void shuckClick(View v)
    {
        if (!puckshuck){
            puckshuck = true;
            messageDelete();
        }

    }

    // Get details of comments
    public void commentClick()
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

    //report click function to confirm a report
    public void reportConfirmClick(View v)
    {
        // TODO: add user report logic here

        popupWindow.dismiss();
        messageDelete();
    }
    //report click function to dismiss the report window
    public void reportCancelClick(View v)
    {
        popupWindow.dismiss();
    }

    //the camera button callback to start the camera activity class
    public void onCameraClick(View v){
        Intent cameraIntent = new Intent(this, cameraActivity.class);
        startActivity(cameraIntent);
    }

    // Get a new photo from the server for each Puck or Shuck
    private void messageDelete()
    {
        phoneSettings.redirectedReceive = null;

        new GetImageAsyncTask().execute();
    }
}
