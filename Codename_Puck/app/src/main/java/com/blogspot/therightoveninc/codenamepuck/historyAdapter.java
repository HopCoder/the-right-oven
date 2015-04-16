package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jjgo on 4/14/15.
 */
public class historyAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final Integer[] counts;

    public historyAdapter(Context context, String[] values, Integer[] counts) {
        super(context, R.layout.comment_row, values);
        this.context = context;
        this.values = values;
        this.counts = counts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_row, parent, false);
        TextView nameText = (TextView) rowView.findViewById(R.id.nameText);
        TextView upCount = (TextView) rowView.findViewById(R.id.upCount);

        nameText.setText(values[position]);
        upCount.setText(counts[position]);

        return rowView;
    }
}
