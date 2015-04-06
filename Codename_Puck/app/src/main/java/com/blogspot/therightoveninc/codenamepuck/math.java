package com.blogspot.therightoveninc.codenamepuck;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by jjgo on 2/24/15.
 */
public class math {
    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

}
