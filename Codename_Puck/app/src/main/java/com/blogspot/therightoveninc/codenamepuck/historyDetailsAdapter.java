package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jjgo on 4/29/15.
 */
public class historyDetailsAdapter extends ArrayAdapter<String> {
    protected Context context;
    protected String[] values;

    public historyDetailsAdapter(Context context, String[] values) {
        super(context, R.layout.history_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.detail_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        textView.setText(values[position]);

        if (values[position].equals("secretString"))
        {
            ViewGroup.LayoutParams params;
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);

            params = imageView.getLayoutParams();
            params.height = phoneSettings.xPixels;
            params.width = phoneSettings.xPixels;
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(phoneSettings.historyBitmap);
        }
        else
        {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }

        return rowView;
    }
}
