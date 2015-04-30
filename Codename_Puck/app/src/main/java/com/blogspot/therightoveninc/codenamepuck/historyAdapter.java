package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;

/**
 * Created by jjgo on 4/14/15.
 * This class is for loading a list of items to be scrolled
 * through in a list view.
 */
public class historyAdapter extends ArrayAdapter<String > {
    protected Context context;
    protected String[] values;

    public historyAdapter(Context context, String[] values) {
        super(context, R.layout.history_row, values);
        this.context = context;
        this.values = values;
    }

    // set the text for each item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_row, parent, false);
        TextView nameText = (TextView) rowView.findViewById(R.id.nameText);

        nameText.setText(values[position]);

        return rowView;
    }
}
