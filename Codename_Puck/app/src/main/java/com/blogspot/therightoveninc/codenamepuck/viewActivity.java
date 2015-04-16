package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class viewActivity extends Activity {
    public int messageCount = 3;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view);

        if (messageCount > 0)
        {
            // getImage();
            new GetImageAsyncTask().execute();
        }
    }

    private class GetImageAsyncTask extends AsyncTask<URL, Void, Integer>
    {
        private ImageView i;
        private Bitmap image;

        protected Integer doInBackground(URL... urls) {
            URL url;

            try
            {
                url = new URL("http://52.10.111.12:8000/view/1234567890/69/34/50");
//                url = new URL("http://upsilondelts.org/images/bros/thaw.jpg");
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
             //   i.setImageBitmap(image);
            }
            catch (MalformedURLException e)
            {
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
            i = (ImageView) findViewById(R.id.imageView);
            i.setImageBitmap(image);

            return;
        }
    }

    private void getImage()
    {
        ImageView i = (ImageView) findViewById(R.id.imageView);
        URL url;

        try
        {
            url = new URL("http://52.10.111.12:8000/static/a.jpg");
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
    //        i.setImageBitmap(image);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commentClick(View v)
    {
        return;
    }

    public void cameraClick(View v)
    {
        Intent cameraIntent = new Intent(this, cameraActivity.class);
        startActivity(cameraIntent);
    }

    public void puckClick(View v)
    {
        Log.e("a", "hi");
        // send back url

        messageDelete();
    }

    public void shuckClick(View v)
    {
        Log.e("a", "shuck that!");

        messageDelete();
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

    public void  reportConfirmClick(View v)
    {
        // TODO: add user report logic here

        popupWindow.dismiss();
        messageDelete();
    }

    public void reportCancelClick(View v)
    {
        popupWindow.dismiss();
    }

    private void messageDelete()
    {
        new GetImageAsyncTask().execute();
    }

}
