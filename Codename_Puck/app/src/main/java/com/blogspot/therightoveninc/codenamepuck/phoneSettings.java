package com.blogspot.therightoveninc.codenamepuck;

import android.graphics.Bitmap;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.net.URL;

/**
 * Created by jjgo on 4/4/15.
 * This class is a series of static variables utilized across the project.
 */
public class phoneSettings {
    public static int xPixels, yPixels;
    public static Bitmap currentBitmap;
    public static Bitmap historyBitmap;

    public static BasicCookieStore cookieStore;

    public static Cookie cookie;
    public static String cfsr;
    public static String phoneNum;
    public static String postUrl;

    public static URL redirectedReceive;
    public static URL redirectedHistory;
}
