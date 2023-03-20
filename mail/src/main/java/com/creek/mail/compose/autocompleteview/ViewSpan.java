package com.creek.mail.compose.autocompleteview;



import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

public class ViewSpan extends ReplacementSpan {
    protected View view;
    private int maxWidth;

    public ViewSpan(View v, int maxWidth) {
        this.maxWidth = maxWidth;
        this.view = v;
        this.view.setLayoutParams(new LayoutParams(-2, -2));
    }

    private void prepView() {
        int widthSpec = MeasureSpec.makeMeasureSpec(this.maxWidth, -2147483648);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        this.view.measure(widthSpec, heightSpec);
        this.view.layout(0, 0, this.view.getMeasuredWidth(), this.view.getMeasuredHeight());
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        this.prepView();
        canvas.save();
        int padding = (bottom - top - this.view.getBottom()) / 2;
        canvas.translate(x, (float)(bottom - this.view.getBottom() - padding));
        this.view.draw(canvas);
        canvas.restore();
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fm) {
        this.prepView();
        if (fm != null) {
            int height = this.view.getMeasuredHeight();
            int need = height - (fm.descent - fm.ascent);
            if (need > 0) {
                int ascent = need / 2;
                fm.descent += need - ascent;
                fm.ascent -= ascent;
                fm.bottom += need - ascent;
                fm.top -= need / 2;
            }
        }

        return this.view.getRight();
    }
}
