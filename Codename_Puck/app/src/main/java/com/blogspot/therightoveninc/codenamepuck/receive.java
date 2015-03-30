package com.blogspot.therightoveninc.codenamepuck;

import android.annotation.TargetApi;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Display;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


public class receive extends ActionBarActivity {
    public int messageCount = 3;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive);

        setDimensions();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDimensions()
    {
        // Get Dimensions (measured in pixels)
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Get Action Bar Height (pixels)
        TypedValue tv = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        // Convert pixels to dp
        float fl_width = math.convertPixelsToDp(size.x);
        float fl_height = math.convertPixelsToDp(size.y);
        float fl_actionBarHeight = math.convertPixelsToDp(actionBarHeight);

        // Adjust dimensions to have 15 dp margins
        fl_width -= 30f;

        // Calculate dp values
        float fl_button = (fl_height - (fl_width + 15f + fl_actionBarHeight )) / 2;
        float fl_button_margin = (float) (fl_width - (2.5 * fl_button)) / 2;
        float fl_spacer_width = fl_button / 2;

        // Convert dp to pixels
        int pic_size = (int)math.convertDpToPixel(fl_width);
        int button_size = (int)math.convertDpToPixel(fl_button);
        int button_margin = (int)math.convertDpToPixel(fl_button_margin);
        int spacer_width = (int)math.convertDpToPixel(fl_spacer_width);

        // Set picture dimensions
        ImageView image = (ImageView) findViewById(R.id.imageView);
        ViewGroup.LayoutParams imageParams = image.getLayoutParams();
        imageParams.width = pic_size;
        imageParams.height = pic_size;
        image.setLayoutParams(imageParams);

        // Set button dimensions
        ImageButton shuck_button = (ImageButton) findViewById(R.id.shuck_button);
        Button puck_button = (Button) findViewById(R.id.puck_button);
        ViewGroup.LayoutParams buttonParams = shuck_button.getLayoutParams();
        buttonParams.width = button_size;
        buttonParams.height = button_size;
        shuck_button.setLayoutParams(buttonParams);
        puck_button.setLayoutParams(buttonParams);

        // Set spacer width
        View view = findViewById(R.id.view);
        ViewGroup.LayoutParams spacerParams = view.getLayoutParams();
        spacerParams.width = spacer_width;
        spacerParams.height = 1;
        view.setLayoutParams(spacerParams);

        // Set button spacing and locations
        View linearLayout = findViewById(R.id.linear_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(button_margin,spacerParams.width/2,0,0);
        params.addRule(RelativeLayout.BELOW, R.id.imageView);
        linearLayout.setLayoutParams(params);
    }

    public void puckClick(View v)
    {
        Log.e("a", "hi");

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
        View popupView = layoutInflater.inflate(R.layout.popup, null);
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
        messageCount -= 1;

        if (messageCount <= 0)
        {
            setContentView(R.layout.intro);
        }
    }

}
