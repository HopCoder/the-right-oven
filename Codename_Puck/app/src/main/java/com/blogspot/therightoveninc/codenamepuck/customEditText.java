package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by jjgo on 4/13/15.

 This is needed in order to allow the EditText of a new comment to lose focus at proper times.
 Without it, the keyboard constantly hides stuff and it's dumb.
 */
public class customEditText extends EditText{
    public customEditText(Context context)
    {
        this(context,null);
    }

    public customEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
