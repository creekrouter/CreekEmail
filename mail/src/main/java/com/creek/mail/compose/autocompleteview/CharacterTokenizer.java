package com.creek.mail.compose.autocompleteview;



import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import java.util.ArrayList;

public class CharacterTokenizer implements Tokenizer {
    ArrayList<Character> splitChar;

    CharacterTokenizer(char[] splitChar) {
        this.splitChar = new ArrayList(splitChar.length);
        char[] var2 = splitChar;
        int var3 = splitChar.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char c = var2[var4];
            this.splitChar.add(c);
        }

    }

    public int findTokenStart(CharSequence text, int cursor) {
        int i;
        for(i = cursor; i > 0 && !this.splitChar.contains(text.charAt(i - 1)); --i) {
        }

        while(i < cursor && text.charAt(i) == ' ') {
            ++i;
        }

        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;

        int len;
        for(len = text.length(); i < len; ++i) {
            if (this.splitChar.contains(text.charAt(i))) {
                return i;
            }
        }

        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i;
        for(i = text.length(); i > 0 && text.charAt(i - 1) == ' '; --i) {
        }

        if (i > 0 && this.splitChar.contains(text.charAt(i - 1))) {
            return text;
        } else {
            String token = (this.splitChar.size() > 1 && (Character)this.splitChar.get(0) == ' ' ? (Character)this.splitChar.get(1) : (Character)this.splitChar.get(0)) + " ";
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + token);
                TextUtils.copySpansFrom((Spanned)text, 0, text.length(), Object.class, sp, 0);
                return sp;
            } else {
                return text + token;
            }
        }
    }
}
