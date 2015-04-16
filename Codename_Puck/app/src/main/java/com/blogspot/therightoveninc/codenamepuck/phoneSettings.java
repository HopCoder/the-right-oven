package com.blogspot.therightoveninc.codenamepuck;

import android.graphics.Bitmap;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.net.CookieStore;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjgo on 4/4/15.
 */
public class phoneSettings {
    public static int xPixels, yPixels;
    public static List<String> listValues = new ArrayList<>();
    public static Bitmap currentBitmap;

    public static BasicCookieStore cookieStore;


    public static Cookie cookie;
    public static String cfsr;
    public static String postUrl = "http://52.10.111.12:8000/post/5036792514/69/34/";
  //  public static String postUrl = "http://192.168.1.120:8000/post/5036792514/69/34/";
    public static URL redirectedReceive;
}
