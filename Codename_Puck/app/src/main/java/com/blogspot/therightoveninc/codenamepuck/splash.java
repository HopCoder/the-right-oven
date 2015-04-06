package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

/**
 * Created by jjgo on 2/21/15.
 */
public class splash extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        getDimensions();

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(splash.this, receive.class);
                    startActivity(i);

                    // close this activity
                    finish();
                    }
                }, SPLASH_TIME_OUT);

    }

    private void getDimensions()
    {
        // Get Dimensions (measured in pixels)
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        phoneSettings.xPixels = size.x;
        phoneSettings.yPixels = size.y;
    }


}
