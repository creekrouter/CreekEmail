package com.creek.mail.search;

import android.text.Html;
import android.text.Spanned;

public class SearchKeyText {
    public static Spanned getKeyPart(String keyWord, String textDisplay) {
        Spanned temp = null;
        String text = textDisplay;
        if (textDisplay != null) {
            text = textDisplay.replace("\"", "");
        }

        if (text != null && text.toLowerCase().contains(keyWord.toLowerCase())) {
            int index = text.toLowerCase().indexOf(keyWord.toLowerCase());
            int len = keyWord.length();
            if (text.length() < 25) {//单行小于25个字符时，从头开始显示，显示全
                temp = Html.fromHtml(text.substring(0, index)
                        + "<font color=#FF0000>"
                        + text.substring(index, index + len) + "</font>"
                        + text.substring(index + len, text.length()));
            } else {//大于25个字开始截取显示

                if (index >= 0 && index <= 7) {
                    temp = Html.fromHtml(text.substring(0, index)
                            + "<font color=#FF0000>"
                            + text.substring(index, index + len) + "</font>"
                            + text.substring(index + len, text.length()));
                } else if (index > 7) {
                    temp = Html.fromHtml("..." + text.substring(index - 6, index)
                            + "<font color=#FF0000>"
                            + text.substring(index, index + len) + "</font>"
                            + text.substring(index + len, text.length()));
                }

            }
        } else {//不包含关键词显示全部
            temp = Html.fromHtml(text);
            return temp;
        }
        return temp;
    }

}
