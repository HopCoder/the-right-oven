package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Timothy D. Mahon on 4/15/2015.
 * The main menu page. From here the application sends for other classes.
 */
public class mainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onCameraClick(View view){
        Intent cameraIntent = new Intent(this, cameraActivity.class);
        startActivity(cameraIntent);
    }

    public void onViewPosts(View view){
        Intent viewIntent = new Intent(this, viewActivity.class);
        startActivity(viewIntent);
    }
    public void onHistoryClick(View view){
        Intent historyIntent = new Intent(this, history.class);
        startActivity(historyIntent);

    }
}
