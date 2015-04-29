package com.blogspot.therightoveninc.codenamepuck;

import android.graphics.Bitmap;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.net.URL;

/**
 * Created by jjgo on 4/4/15.
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

  //  public static String postUrl = "http://192.168.1.120:8000/post/5036792514/69/34/";
    public static URL redirectedReceive;
}
