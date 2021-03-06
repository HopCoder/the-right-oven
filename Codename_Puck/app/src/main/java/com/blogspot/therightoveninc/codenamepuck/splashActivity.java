package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * Created by jjgo on 2/21/15.
 * The main splash class that appears when first starting the application.
 * This class assigns and utilizes a random phone id to identify the phone.
 */
public class splashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000; //Time in millisecond for the splash page

    //The on creat method that creates the application splash rage and presents it to the user.
    //Likewise the function creates a random 10 digit string to be used as a id and stores it locally
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //Look for the unique identitfier (in this case called phonenum) otherwise create a new one
        SharedPreferences settings = this.getPreferences(Context.MODE_PRIVATE);
        Random gen = new Random();
        String PhoneNum = "";
        for (int i = 0; i < 10; i++){
            int rand = gen.nextInt(10);
            PhoneNum += Integer.toString(rand);
        }
        phoneSettings.phoneNum = settings.getString("phoneID", PhoneNum);
        if (phoneSettings.phoneNum == PhoneNum) {
            settings.edit().putString("phoneID", PhoneNum).commit();
            Log.d("id", "saved new");
        }else{
            Log.d("id", "used old");
        }
        phoneSettings.postUrl = "http://52.10.111.12:8000/post/" + phoneSettings.phoneNum + "/69/34/";
        Log.d("splsh:", phoneSettings.postUrl);
        getDimensions();

        new GetCookieAsyncTask().execute();

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splashActivity screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(splashActivity.this, mainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                    }
                }, SPLASH_TIME_OUT);
    }

    private class GetCookieAsyncTask extends AsyncTask<URL, Void, Void>
    {
        protected Void doInBackground(URL... urls) {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://52.10.111.12:8000/authenticate");

                HttpResponse response = httpclient.execute(httpGet);
                List<Cookie> cookies = httpclient.getCookieStore().getCookies();
                BasicCookieStore cookieStore = new BasicCookieStore();
                if (cookies.size() > 0) {
                    cookieStore.addCookie(cookies.get(0));
                    phoneSettings.cookieStore = cookieStore;

                    phoneSettings.cfsr = cookies.get(0).getValue();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    // Get Dimensions (measured in pixels)
    private void getDimensions()
    {
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        phoneSettings.xPixels = size.x;
        phoneSettings.yPixels = size.y;
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

 //               phoneSettings.xPixels = metrics.widthPixels;
   //             phoneSettings.yPixels = metrics.heightPixels;
            }
            // Everything else
            else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
           //         phoneSettings.xPixels = (Integer) mGetRawW.invoke(display);
             //       phoneSettings.yPixels = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
            //    } catch (IllegalAccessException e) {
                    e.printStackTrace();
              //  } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
    }


}
