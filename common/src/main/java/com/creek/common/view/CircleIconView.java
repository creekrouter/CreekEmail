package com.creek.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;

import java.util.HashMap;


public class CircleIconView extends AppCompatImageView {

    private static HashMap<String, String> sDic = new HashMap<>();
    private static final String DEFAULT_NAME = "无";


    private Paint mPaint;
    private int mWidth, mHeight;
    private String mName;
    private float mX, mY;
    private float mRadio;


    public CircleIconView(Context context) {
        super(context);
        initData();

    }

    public CircleIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();

    }

    private void initData() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        setName(mName);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mRadio = (mWidth >= mHeight ? mHeight : mWidth) / 2 - 2;
        mX = mWidth / 2;
        mY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mX, mY, mRadio, mPaint);

        float charSize = mRadio;
        if (mName.length() > 1) {
            charSize = mRadio / 1.5f;
        }
        mPaint.setTextSize(charSize);
        float nameWidth = mPaint.measureText(mName);
        mPaint.setShader(null);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);

        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int baseline = (mHeight - fontMetricsInt.bottom - fontMetricsInt.top) / 2;

        canvas.drawText(mName, mX - nameWidth / 2, baseline, mPaint);

    }

    /*
   改变文字内容
    */
    public void setName(String name) {
        mName = shortName(name);
        invalidate();
    }

    private String shortName(String userFullName) {
        if (userFullName == null || userFullName.length() == 0) {
            return DEFAULT_NAME;
        }

        if (sDic.containsKey(userFullName)) {
            return sDic.get(userFullName);
        }

        String shortName = findShortName(userFullName.trim());
        sDic.put(userFullName, shortName);
        return shortName;
    }

    private String findShortName(String name) {
        StringBuilder strEnglish, strChinese;
        strChinese = new StringBuilder();
        strEnglish = new StringBuilder();
        boolean enFlag = true;
        boolean chFlag = true;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (isChinese(ch)) {
                if (chFlag) {
                    strChinese.append(ch);
                }
                if (strEnglish.length() > 0 && enFlag) {
                    enFlag = false;
                }
            } else if (isENChar(ch)) {
                if (enFlag) {
                    strEnglish.append(ch);
                }
                if (strChinese.length() > 0 && chFlag) {
                    chFlag = false;
                }
            } else {
                if (strEnglish.length() > 0 && enFlag) {
                    enFlag = false;
                }
                if (strChinese.length() > 0 && chFlag) {
                    chFlag = false;
                }
            }
        }
        String res;
        if (strChinese.length() > 0) {
            res = strChinese.toString();
        } else if (strEnglish.length() > 0) {
            res = Character.toUpperCase(strEnglish.charAt(0)) + "";
        } else {
            res = name.charAt(0) + "";
        }
        if (res.length() == 3) {
            res = res.substring(1, 3);
        } else if (res.length() > 2) {
            res = res.substring(0, 2);
        }
        return res;
    }

    private boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    private boolean isENChar(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' || ch <= 'Z');
    }
}
