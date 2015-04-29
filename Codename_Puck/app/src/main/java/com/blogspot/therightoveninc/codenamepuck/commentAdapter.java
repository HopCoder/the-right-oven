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

import java.net.URL;

/**
 * Created by jjgo on 4/13/15.
 */
public class commentAdapter extends ArrayAdapter<String>{
    protected Context context;
    protected String[] values;

    public commentAdapter(Context context, String[] values) {
        super(context, R.layout.comment_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.comment_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.newComment);
        LinearLayout commentSection = (LinearLayout) rowView.findViewById(R.id.commentSection);
        textView.setText(values[position]);

        ViewGroup.LayoutParams params;

        if (values[position].equals("secretString"))
        {
            imageView.setVisibility(View.VISIBLE);
            commentSection.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);

            params = imageView.getLayoutParams();
            params.height = phoneSettings.xPixels;
            params.width = phoneSettings.xPixels;
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(phoneSettings.currentBitmap);
        }
        else if (values[position].equals("theEndString"))
        {
            imageView.setVisibility(View.GONE);
            commentSection.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            imageView.setVisibility(View.GONE);
            commentSection.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);

            EditText editText = (EditText) rowView.findViewById(R.id.editText);
            Button submitButton = (Button) rowView.findViewById(R.id.submitButton);
            TextView spacer = (TextView) rowView.findViewById(R.id.spacer);

            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
            ViewGroup.LayoutParams buttonParams = submitButton.getLayoutParams();
            ViewGroup.LayoutParams spacerParams = spacer.getLayoutParams();

            float width = phoneSettings.xPixels - buttonParams.width - spacerParams.width -
                math.convertDpToPixel(marginLayoutParams.leftMargin + marginLayoutParams.rightMargin);

            params = editText.getLayoutParams();
            params.width = (int) width;
            editText.setLayoutParams(params);
        }

        return rowView;
    }
}