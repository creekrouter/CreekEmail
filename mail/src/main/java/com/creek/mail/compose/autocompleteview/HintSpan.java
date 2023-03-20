package com.creek.mail.compose.autocompleteview;

import android.content.res.ColorStateList;
import android.text.style.TextAppearanceSpan;

public class HintSpan extends TextAppearanceSpan {
    public HintSpan(String family, int style, int size, ColorStateList color, ColorStateList linkColor) {
        super(family, style, size, color, linkColor);
    }
}