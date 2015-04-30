package com.blogspot.therightoveninc.codenamepuck;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by jjgo on 4/13/15.
 * This class creates the comment list and provides basic functionality to the comment buttons and options.
 * Coupled alongside the comment adapter it creates the comprehensive comment view.
 */
public class comment extends abstractPhotoDetails {
    private String newComment; //The new comment string sent to the server to be stored upon a sucessful submit.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        listView = (ListView) findViewById(R.id.listView); //The actual list object that will be filled by the comments on a sucessful get response from the server.

        listValues = new ArrayList<String>();
        listValues.add("secretString");

        String commentAddress = phoneSettings.redirectedReceive.toString();

        new ViewCommentsAsyncTask().execute(commentAddress);
        refreshListView();
    }

    @Override
    protected void refreshListView()
    {
        commentAdapter adapter = new commentAdapter(this, listValues.toArray(new String[listValues.size()]));
        listView.setAdapter(adapter);
    }

    public void addCommentClick(View v) {
        try {
            View parent = getViewByPosition(listValues.size() - 1, listView);
            EditText editText = (EditText) parent.findViewById(R.id.editText);
            newComment = editText.getText().toString();
            if (newComment.equals(""))
                return;
            listValues.add(listValues.size()-1,newComment);
            refreshListView();
            new PostCommentAsyncTask().execute();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void deleteCommentClick(View v) {
        View parent = (View) v.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.textView);
        String remove = textView.getText().toString();
        newComment = "<remove>";
        newComment = newComment.concat(remove);

        for (int i=0; i<listValues.size(); i++)
        {
            if (listValues.get(i).equals(remove))
            {
                listValues.remove(i);
                break;
            }
        }

        refreshListView();
        new PostCommentAsyncTask().execute();
    }

    public class PostCommentAsyncTask extends AsyncTask<URL,Void,Void>
    {
        protected Void doInBackground(URL... urls) {
            if (commentString == null){
                return null;
            }
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(commentString);

            byte[] postData = newComment.getBytes(Charset.forName("UTF-8"));
            httpPost.setEntity(new ByteArrayEntity(postData));
            try {
                HttpResponse response = httpClient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());
                int x = 0;
                for (x =0; x< result.length()/500; x = x+1)
                {
                    Log.e("o",result.substring(x*500,(x+1)*500));
                }
                Log.e("o", result.substring(x*500));
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    protected class ViewCommentsAsyncTask extends GetCommentsAsyncTask
    {
        @Override
        protected void onPostExecute(Integer result)
        {
            listValues.add("theEndString");
            refreshListView();
        }
    }
}
