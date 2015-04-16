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
import android.view.Menu;
import android.view.MenuItem;
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

        setDimensions();

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
                url = new URL("http://52.10.111.12:8000/view/5036792541/69/54/50/");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:

                return true;
            case R.id.action_report:
                reportClick();
                return true;
            case R.id.action_camera:
                onCameraClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDimensions()
    {
       return;
    }

    public void puckClick(View v)
    {
        Log.e("a", "hi");
        // send back url
        new GetImageAsyncTask().execute();

    }

    public void shuckClick(View v)
    {
        Log.e("a", "shuck that!");
        new GetImageAsyncTask().execute();

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

    public void onCameraClick(){
        Intent cameraIntent = new Intent(this, cameraActivity.class);
        startActivity(cameraIntent);
    }

    private void messageDelete()
    {
        messageCount -= 1;

        if (messageCount <= 0)
        {
            setContentView(R.layout.splash);
        }
    }

}
