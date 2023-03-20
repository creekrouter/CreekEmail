package com.creek.mail.compose.autocompleteview;

import android.content.Context;
import android.widget.TextView;

public class CountSpan extends ViewSpan {
    public String text = "";

    public CountSpan(int count, Context ctx, int textColor, int textSize, int maxWidth) {
        super(new TextView(ctx), maxWidth);
        TextView v = (TextView)this.view;
        v.setTextColor(textColor);
        v.setTextSize(0, (float)textSize);
        this.setCount(count);
    }

    public void setCount(int c) {
        this.text = "+" + c;
        ((TextView)this.view).setText(this.text);
    }
}