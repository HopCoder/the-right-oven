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
 * Created by jjgo on 4/13/15.
 * This adapater class adaptatively changes the comment list that can be viewed on a picture.
 */
public class commentAdapter extends ArrayAdapter<String>{
    protected Context context; //The current user context (the layout parameters, the current running activity, etc.)
    protected String[] values; //The values to be presented in the list.

    //The constructor for creating a comment adapter this grabs the current context and the values of the string array.
    public commentAdapter(Context context, String[] values) {
        super(context, R.layout.comment_row, values);
        this.context = context;
        this.values = values;
    }

    //The getview method for populating and formatting the comment view with the appropriate buttons (such as the delete button) and comments.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.comment_row, parent, false);
        Button delButt = (Button)rowView.findViewById(R.id.deleteButton);

        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.newComment);
        LinearLayout commentSection = (LinearLayout) rowView.findViewById(R.id.commentSection);
        if(values[position].matches( "^.*u:[0-9]{10}$")){
            String [] stuff = values[position].split("u:");
            if (stuff[1].equals(phoneSettings.phoneNum)){
                delButt.setVisibility(View.VISIBLE);
            }else{
                delButt.setVisibility(View.GONE);
            }
            textView.setText(stuff[0]);
        }else{
            textView.setText(values[position]);
        }

        ViewGroup.LayoutParams params;

        if (values[position].equals("secretString"))
        {
            imageView.setVisibility(View.VISIBLE);
            commentSection.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            delButt.setVisibility(View.GONE);
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
            delButt.setVisibility(View.GONE);
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