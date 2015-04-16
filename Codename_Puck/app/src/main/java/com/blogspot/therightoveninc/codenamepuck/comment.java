package com.blogspot.therightoveninc.codenamepuck;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by jjgo on 4/13/15.
 */
public class comment extends ActionBarActivity {
    private ListView listView;
    private String newComment;
    private String commentString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);

        listView = (ListView) findViewById(R.id.listView);


        phoneSettings.listValues = new ArrayList<String>();
        phoneSettings.listValues.add("secretString");

        new GetCommentsAsyncTask().execute();

        refreshListView();
    }

    public class GetCommentsAsyncTask extends AsyncTask<URL, Void, Integer>
    {
        @Override
        protected Integer doInBackground(URL... urls)
        {
            if (phoneSettings.redirectedReceive == null) {
                commentString = null;
                return null;
            }
            commentString = phoneSettings.redirectedReceive.toString();
            commentString = commentString.concat("/comments/");
            commentString = commentString.replace("/static","");
            try {
                URL oracle = new URL(commentString);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.length() > 0 && !inputLine.contains("<br/>")) {
                        phoneSettings.listValues.add(inputLine);
                    }
                }
                in.close();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            phoneSettings.listValues.add("theEndString");
            refreshListView();
        }
    }

    private void refreshListView()
    {
        commentAdapter adapter = new commentAdapter(this, phoneSettings.listValues.toArray(new String[phoneSettings.listValues.size()]));
        listView.setAdapter(adapter);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void addCommentClick(View v) {
        try {
            View parent = getViewByPosition(phoneSettings.listValues.size() - 1, listView);
            EditText editText = (EditText) parent.findViewById(R.id.editText);
            newComment = editText.getText().toString();
            if (newComment.equals(""))
                return;
            phoneSettings.listValues.add(phoneSettings.listValues.size()-1,newComment);
            refreshListView();
            new PostCommentAsyncTask().execute();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
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
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            return null;
        }
    }
}
