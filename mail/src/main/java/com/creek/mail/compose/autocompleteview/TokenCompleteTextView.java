package com.creek.mail.compose.autocompleteview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.QwertyKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.TextView;

import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.NonNull;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class TokenCompleteTextView<T> extends androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView implements OnEditorActionListener {
    public static final String TAG = "TokenAutoComplete";
    private char[] splitChar = new char[]{',', ';'};
    private Tokenizer tokenizer;
    private T selectedObject;
    private TokenCompleteTextView.TokenListener<T> listener;
    private TokenCompleteTextView<T>.TokenSpanWatcher spanWatcher;
    private TokenCompleteTextView<T>.TokenTextWatcher textWatcher;
    private ArrayList<T> objects;
    private List<TokenCompleteTextView<T>.TokenImageSpan> hiddenSpans;
    private TokenCompleteTextView.TokenDeleteStyle deletionStyle;
    private TokenCompleteTextView.TokenClickStyle tokenClickStyle;
    private CharSequence prefix;
    private boolean hintVisible;
    private Layout lastLayout;
    private boolean allowDuplicates;
    private boolean focusChanging;
    private boolean initialized;
    private boolean performBestGuess;
    private boolean savingState;
    private boolean shouldFocusNext;
    private boolean allowCollapse;
    private int tokenLimit;
    boolean inInvalidate;

    protected void addListeners() {
        Editable text = this.getText();
        if (text != null) {
            text.setSpan(this.spanWatcher, 0, text.length(), 18);
            this.addTextChangedListener(this.textWatcher);
        }

    }

    protected void removeListeners() {
        Editable text = this.getText();
        if (text != null) {
            TokenCompleteTextView<T>.TokenSpanWatcher[] spanWatchers = (TokenCompleteTextView.TokenSpanWatcher[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenSpanWatcher.class);
            TokenCompleteTextView.TokenSpanWatcher[] var3 = spanWatchers;
            int var4 = spanWatchers.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                TokenCompleteTextView<T>.TokenSpanWatcher watcher = var3[var5];
                text.removeSpan(watcher);
            }

            this.removeTextChangedListener(this.textWatcher);
        }

    }

    private void init() {
        if (!this.initialized) {
            this.setTokenizer(new CommaTokenizer());
            this.objects = new ArrayList();
            Editable text = this.getText();

            assert null != text;

            this.spanWatcher = new TokenCompleteTextView.TokenSpanWatcher();
            this.textWatcher = new TokenCompleteTextView.TokenTextWatcher();
            this.hiddenSpans = new ArrayList();
            this.addListeners();
            this.setTextIsSelectable(false);
            this.setLongClickable(false);
            this.setInputType(this.getInputType() | 524288 | 65536);
            this.setHorizontallyScrolling(false);
            this.setOnEditorActionListener(this);
            this.setFilters(new InputFilter[]{new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (TokenCompleteTextView.this.tokenLimit != -1 && TokenCompleteTextView.this.objects.size() == TokenCompleteTextView.this.tokenLimit) {
                        return "";
                    } else if (source.length() == 1 && TokenCompleteTextView.this.isSplitChar(source.charAt(0))) {
                        TokenCompleteTextView.this.performCompletion();
                        return "";
                    } else if (dstart < TokenCompleteTextView.this.prefix.length()) {
                        if (dstart == 0 && dend == 0) {
                            return null;
                        } else {
                            return dend <= TokenCompleteTextView.this.prefix.length() ? TokenCompleteTextView.this.prefix.subSequence(dstart, dend) : TokenCompleteTextView.this.prefix.subSequence(dstart, TokenCompleteTextView.this.prefix.length());
                        }
                    } else {
                        return null;
                    }
                }
            }});
            this.setDeletionStyle(TokenCompleteTextView.TokenDeleteStyle.Clear);
            this.initialized = true;
        }
    }

    public TokenCompleteTextView(Context context) {
        super(context);
        this.deletionStyle = TokenCompleteTextView.TokenDeleteStyle._Parent;
        this.tokenClickStyle = TokenCompleteTextView.TokenClickStyle.None;
        this.prefix = "";
        this.hintVisible = false;
        this.lastLayout = null;
        this.allowDuplicates = true;
        this.focusChanging = false;
        this.initialized = false;
        this.performBestGuess = true;
        this.savingState = false;
        this.shouldFocusNext = false;
        this.allowCollapse = true;
        this.tokenLimit = -1;
        this.inInvalidate = false;
        this.init();
    }

    public TokenCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.deletionStyle = TokenCompleteTextView.TokenDeleteStyle._Parent;
        this.tokenClickStyle = TokenCompleteTextView.TokenClickStyle.None;
        this.prefix = "";
        this.hintVisible = false;
        this.lastLayout = null;
        this.allowDuplicates = true;
        this.focusChanging = false;
        this.initialized = false;
        this.performBestGuess = true;
        this.savingState = false;
        this.shouldFocusNext = false;
        this.allowCollapse = true;
        this.tokenLimit = -1;
        this.inInvalidate = false;
        this.init();
    }

    public TokenCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.deletionStyle = TokenCompleteTextView.TokenDeleteStyle._Parent;
        this.tokenClickStyle = TokenCompleteTextView.TokenClickStyle.None;
        this.prefix = "";
        this.hintVisible = false;
        this.lastLayout = null;
        this.allowDuplicates = true;
        this.focusChanging = false;
        this.initialized = false;
        this.performBestGuess = true;
        this.savingState = false;
        this.shouldFocusNext = false;
        this.allowCollapse = true;
        this.tokenLimit = -1;
        this.inInvalidate = false;
        this.init();
    }

    protected void performFiltering(@NonNull CharSequence text, int start, int end, int keyCode) {
        if (start < this.prefix.length()) {
            start = this.prefix.length();
        }

        Filter filter = this.getFilter();
        if (filter != null) {
            if (this.hintVisible) {
                filter.filter("");
            } else {
                filter.filter(text.subSequence(start, end), this);
            }
        }

    }

    public void setTokenizer(Tokenizer t) {
        super.setTokenizer(t);
        this.tokenizer = t;
    }

    public void setDeletionStyle(TokenCompleteTextView.TokenDeleteStyle dStyle) {
        this.deletionStyle = dStyle;
    }

    public void setTokenClickStyle(TokenCompleteTextView.TokenClickStyle cStyle) {
        this.tokenClickStyle = cStyle;
    }

    public void setTokenListener(TokenCompleteTextView.TokenListener<T> l) {
        this.listener = l;
    }

    public boolean isTokenRemovable(T token) {
        return true;
    }

    public void setPrefix(CharSequence p) {
        this.prefix = "";
        Editable text = this.getText();
        if (text != null) {
            text.insert(0, p);
        }

        this.prefix = p;
        this.updateHint();
    }

    public List<T> getObjects() {
        return this.objects;
    }

    public void setSplitChar(char[] splitChar) {
        char[] fixed = splitChar;
        if (splitChar[0] == ' ') {
            fixed = new char[splitChar.length + 1];
            fixed[0] = 167;
            System.arraycopy(splitChar, 0, fixed, 1, splitChar.length);
        }

        this.splitChar = fixed;
        this.setTokenizer(new CharacterTokenizer(splitChar));
    }

    public void setSplitChar(char splitChar) {
        this.setSplitChar(new char[]{splitChar});
    }

    private boolean isSplitChar(char c) {
        char[] var2 = this.splitChar;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char split = var2[var4];
            if (c == split) {
                return true;
            }
        }

        return false;
    }

    public void allowDuplicates(boolean allow) {
        this.allowDuplicates = allow;
    }

    public void performBestGuess(boolean guess) {
        this.performBestGuess = guess;
    }

    public void allowCollapse(boolean allowCollapse) {
        this.allowCollapse = allowCollapse;
    }

    public void setTokenLimit(int tokenLimit) {
        this.tokenLimit = tokenLimit;
    }

    protected abstract View getViewForObject(T var1);

    protected abstract T defaultObject(String var1);

    public CharSequence getTextForAccessibility() {
        if (this.getObjects().size() == 0) {
            return this.getText();
        } else {
            SpannableStringBuilder description = new SpannableStringBuilder();
            Editable text = this.getText();
            int selectionStart = -1;
            int selectionEnd = -1;

            int i;
            int origSelectionStart;
            int origSelectionEnd;
            for(i = 0; i < text.length(); ++i) {
                origSelectionStart = Selection.getSelectionStart(text);
                if (i == origSelectionStart) {
                    selectionStart = description.length();
                }

                origSelectionEnd = Selection.getSelectionEnd(text);
                if (i == origSelectionEnd) {
                    selectionEnd = description.length();
                }

                TokenCompleteTextView<T>.TokenImageSpan[] tokens = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(i, i, TokenCompleteTextView.TokenImageSpan.class);
                if (tokens.length > 0) {
                    TokenCompleteTextView<T>.TokenImageSpan token = tokens[0];
                    description = description.append(this.tokenizer.terminateToken(token.getToken().toString()));
                    i = text.getSpanEnd(token);
                } else {
                    description = description.append(text.subSequence(i, i + 1));
                }
            }

            origSelectionStart = Selection.getSelectionStart(text);
            if (i == origSelectionStart) {
                selectionStart = description.length();
            }

            origSelectionEnd = Selection.getSelectionEnd(text);
            if (i == origSelectionEnd) {
                selectionEnd = description.length();
            }

            if (selectionStart >= 0 && selectionEnd >= 0) {
                Selection.setSelection(description, selectionStart, selectionEnd);
            }

            return description;
        }
    }

    @SuppressLint("WrongConstant")
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() == 8192) {
            CharSequence text = this.getTextForAccessibility();
            event.setFromIndex(Selection.getSelectionStart(text));
            event.setToIndex(Selection.getSelectionEnd(text));
            event.setItemCount(text.length());
        }

    }

    private int getCorrectedTokenEnd() {
        Editable editable = this.getText();
        int cursorPosition = this.getSelectionEnd();
        return this.tokenizer.findTokenEnd(editable, cursorPosition);
    }

    private int getCorrectedTokenBeginning(int end) {
        int start = this.tokenizer.findTokenStart(this.getText(), end);
        if (start < this.prefix.length()) {
            start = this.prefix.length();
        }

        return start;
    }

    protected String currentCompletionText() {
        if (this.hintVisible) {
            return "";
        } else {
            Editable editable = this.getText();
            int end = this.getCorrectedTokenEnd();
            int start = this.getCorrectedTokenBeginning(end);
            return TextUtils.substring(editable, start, end);
        }
    }

    protected float maxTextWidth() {
        return (float)(this.getWidth() - this.getPaddingLeft() - this.getPaddingRight());
    }

    @TargetApi(16)
    private void api16Invalidate() {
        if (this.initialized && !this.inInvalidate) {
            this.inInvalidate = true;
            this.setShadowLayer(this.getShadowRadius(), this.getShadowDx(), this.getShadowDy(), this.getShadowColor());
            this.inInvalidate = false;
        }

    }

    public void invalidate() {
        if (VERSION.SDK_INT >= 16) {
            this.api16Invalidate();
        }

        super.invalidate();
    }

    public boolean enoughToFilter() {
        if (this.tokenizer != null && !this.hintVisible) {
            int cursorPosition = this.getSelectionEnd();
            if (cursorPosition < 0) {
                return false;
            } else {
                int end = this.getCorrectedTokenEnd();
                int start = this.getCorrectedTokenBeginning(end);
                return end - start >= Math.max(this.getThreshold(), 1);
            }
        } else {
            return false;
        }
    }

    public void performCompletion() {
        if ((this.getAdapter() == null || this.getListSelection() == -1) && this.enoughToFilter()) {
            Object bestGuess;
            if (this.getAdapter() != null && this.getAdapter().getCount() > 0 && this.performBestGuess) {
                bestGuess = this.getAdapter().getItem(0);
            } else {
                bestGuess = this.defaultObject(this.currentCompletionText());
            }

            this.replaceText(this.convertSelectionToString(bestGuess));
        } else {
            super.performCompletion();
        }

    }

    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        InputConnection superConn = super.onCreateInputConnection(outAttrs);
        if (superConn != null) {
            TokenCompleteTextView<T>.TokenInputConnection conn = new TokenCompleteTextView.TokenInputConnection(superConn, true);
            outAttrs.imeOptions &= -1073741825;
            outAttrs.imeOptions |= 268435456;
            return conn;
        } else {
            return null;
        }
    }

    private void handleDone() {
        this.performCompletion();
        @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager)this.getContext().getSystemService("input_method");
        imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        boolean handled = super.onKeyUp(keyCode, event);
        if (this.shouldFocusNext) {
            this.shouldFocusNext = false;
            this.handleDone();
        }

        return handled;
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        boolean handled = false;
        switch(keyCode) {
            case 23:
            case 61:
            case 66:
                if (event.hasNoModifiers()) {
                    this.shouldFocusNext = true;
                    handled = true;
                }
                break;
            case 67:
                handled = !this.canDeleteSelection(1) || this.deleteSelectedObject(false);
        }

        return handled || super.onKeyDown(keyCode, event);
    }

    private boolean deleteSelectedObject(boolean handled) {
        if (this.tokenClickStyle != null && this.tokenClickStyle.isSelectable()) {
            Editable text = this.getText();
            if (text == null) {
                return handled;
            }

            TokenCompleteTextView<T>.TokenImageSpan[] spans = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenImageSpan.class);
            TokenCompleteTextView.TokenImageSpan[] var4 = spans;
            int var5 = spans.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                TokenCompleteTextView<T>.TokenImageSpan span = var4[var6];
                if (span.view.isSelected()) {
                    this.removeSpan(span);
                    handled = true;
                    break;
                }
            }
        }

        return handled;
    }

    public boolean onEditorAction(TextView view, int action, KeyEvent keyEvent) {
        if (action == 6) {
            this.handleDone();
            return true;
        } else {
            return false;
        }
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getActionMasked();
        Editable text = this.getText();
        boolean handled = false;
        if (this.tokenClickStyle == TokenCompleteTextView.TokenClickStyle.None) {
            handled = super.onTouchEvent(event);
        }

        if (this.isFocused() && text != null && this.lastLayout != null && action == 1) {
            int offset = this.getOffsetForPosition(event.getX(), event.getY());
            if (offset != -1) {
                TokenCompleteTextView<T>.TokenImageSpan[] links = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(offset, offset, TokenCompleteTextView.TokenImageSpan.class);
                if (links.length > 0) {
                    links[0].onClick();
                    handled = true;
                } else {
                    this.clearSelections();
                }
            }
        }

        if (!handled && this.tokenClickStyle != TokenCompleteTextView.TokenClickStyle.None) {
            handled = super.onTouchEvent(event);
        }

        return handled;
    }

    protected void onSelectionChanged(int selStart, int selEnd) {
        if (this.hintVisible) {
            selStart = 0;
        }

        Editable text;
        if (this.tokenClickStyle != null && this.tokenClickStyle.isSelectable()) {
            text = this.getText();
            if (text != null) {
                this.clearSelections();
            }
        }

        if (this.prefix != null && (selStart < this.prefix.length() || selStart < this.prefix.length())) {
            this.setSelection(this.prefix.length());
        } else {
            text = this.getText();
            if (text != null) {
                TokenCompleteTextView<T>.TokenImageSpan[] spans = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(selStart, selStart, TokenCompleteTextView.TokenImageSpan.class);
                TokenCompleteTextView.TokenImageSpan[] var5 = spans;
                int var6 = spans.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    TokenCompleteTextView<T>.TokenImageSpan span = var5[var7];
                    int spanEnd = text.getSpanEnd(span);
                    if (selStart <= spanEnd && text.getSpanStart(span) < selStart) {
                        if (spanEnd == text.length()) {
                            this.setSelection(spanEnd);
                        } else {
                            this.setSelection(spanEnd + 1);
                        }

                        return;
                    }
                }
            }

            super.onSelectionChanged(selStart, selStart);
        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.lastLayout = this.getLayout();
    }

    public void performCollapse(boolean hasFocus) {
        this.focusChanging = true;
        final Editable text;
        int count;
        CountSpan cs;
        if (!hasFocus) {
            text = this.getText();
            if (text != null && this.lastLayout != null) {
                int lastPosition = this.lastLayout.getLineVisibleEnd(0);
                TokenCompleteTextView<T>.TokenImageSpan[] tokens = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(0, lastPosition, TokenCompleteTextView.TokenImageSpan.class);
                count = this.objects.size() - tokens.length;
                CountSpan[] countSpans = (CountSpan[])text.getSpans(0, lastPosition, CountSpan.class);
                if (count > 0 && countSpans.length == 0) {
                    ++lastPosition;
                    cs = new CountSpan(count, this.getContext(), this.getCurrentTextColor(), (int)this.getTextSize(), (int)this.maxTextWidth());
                    text.insert(lastPosition, cs.text);
                    float newWidth = Layout.getDesiredWidth(text, 0, lastPosition + cs.text.length(), this.lastLayout.getPaint());
                    if (newWidth > this.maxTextWidth()) {
                        text.delete(lastPosition, lastPosition + cs.text.length());
                        if (tokens.length > 0) {
                            TokenCompleteTextView<T>.TokenImageSpan token = tokens[tokens.length - 1];
                            lastPosition = text.getSpanStart(token);
                            cs.setCount(count + 1);
                        } else {
                            lastPosition = this.prefix.length();
                        }

                        text.insert(lastPosition, cs.text);
                    }

                    text.setSpan(cs, lastPosition, lastPosition + cs.text.length(), 33);
                    this.hiddenSpans = new ArrayList(Arrays.asList((TokenCompleteTextView.TokenImageSpan[])((TokenCompleteTextView.TokenImageSpan[])text.getSpans(lastPosition + cs.text.length(), text.length(), TokenCompleteTextView.TokenImageSpan.class))));
                    Iterator var17 = this.hiddenSpans.iterator();

                    while(var17.hasNext()) {
                        TokenCompleteTextView<T>.TokenImageSpan span = (TokenCompleteTextView.TokenImageSpan)var17.next();
                        this.removeSpan(span);
                    }
                }
            }
        } else {
            text = this.getText();
            if (text != null) {
                CountSpan[] counts = (CountSpan[])text.getSpans(0, text.length(), CountSpan.class);
                CountSpan[] var11 = counts;
                count = counts.length;

                for(int var16 = 0; var16 < count; ++var16) {
                    cs = var11[var16];
                    text.delete(text.getSpanStart(cs), text.getSpanEnd(cs));
                    text.removeSpan(cs);
                }

                Iterator var12 = this.hiddenSpans.iterator();

                while(var12.hasNext()) {
                    TokenCompleteTextView<T>.TokenImageSpan span = (TokenCompleteTextView.TokenImageSpan)var12.next();
                    this.insertSpan(span);
                }

                this.hiddenSpans.clear();
                if (this.hintVisible) {
                    this.setSelection(this.prefix.length());
                } else {
                    this.postDelayed(new Runnable() {
                        public void run() {
                            TokenCompleteTextView.this.setSelection(text.length());
                        }
                    }, 10L);
                }

                TokenCompleteTextView<T>.TokenSpanWatcher[] watchers = (TokenCompleteTextView.TokenSpanWatcher[])this.getText().getSpans(0, this.getText().length(), TokenCompleteTextView.TokenSpanWatcher.class);
                if (watchers.length == 0) {
                    text.setSpan(this.spanWatcher, 0, text.length(), 18);
                }
            }
        }

        this.focusChanging = false;
    }

    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);
        if (!hasFocus) {
            this.performCompletion();
        }

        this.clearSelections();
        if (this.allowCollapse) {
            this.performCollapse(hasFocus);
        }

    }

    protected CharSequence convertSelectionToString(Object object) {
        this.selectedObject = (T) object;
        switch(this.deletionStyle) {
            case Clear:
                return "";
            case PartialCompletion:
                return this.currentCompletionText();
            case ToString:
                return object != null ? object.toString() : "";
            case _Parent:
            default:
                return super.convertSelectionToString(object);
        }
    }

    private SpannableStringBuilder buildSpannableForText(CharSequence text) {
        char sentinel = this.splitChar[0];
        return new SpannableStringBuilder(String.valueOf(sentinel) + this.tokenizer.terminateToken(text));
    }

    protected TokenCompleteTextView<T>.TokenImageSpan buildSpanForObject(T obj) {
        if (obj == null) {
            return null;
        } else {
            View tokenView = this.getViewForObject(obj);
            return new TokenCompleteTextView.TokenImageSpan(tokenView, obj, (int)this.maxTextWidth());
        }
    }

    protected void replaceText(CharSequence text) {
        this.clearComposingText();
        if (this.selectedObject != null && !this.selectedObject.toString().equals("")) {
            SpannableStringBuilder ssb = this.buildSpannableForText(text);
            TokenCompleteTextView<T>.TokenImageSpan tokenSpan = this.buildSpanForObject(this.selectedObject);
            Editable editable = this.getText();
            int cursorPosition = this.getSelectionEnd();
            int end = cursorPosition;
            int start = cursorPosition;
            if (!this.hintVisible) {
                end = this.getCorrectedTokenEnd();
                start = this.getCorrectedTokenBeginning(end);
            }

            String original = TextUtils.substring(editable, start, end);
            if (editable != null) {
                if (tokenSpan == null) {
                    editable.replace(start, end, "");
                } else if (!this.allowDuplicates && this.objects.contains(tokenSpan.getToken())) {
                    editable.replace(start, end, "");
                } else {
                    QwertyKeyListener.markAsReplaced(editable, start, end, original);
                    editable.replace(start, end, ssb);
                    editable.setSpan(tokenSpan, start, start + ssb.length() - 1, 33);
                }
            }

        }
    }

    public boolean extractText(@NonNull ExtractedTextRequest request, @NonNull ExtractedText outText) {
        try {
            return super.extractText(request, outText);
        } catch (IndexOutOfBoundsException var4) {
            Log.d("TokenAutoComplete", "extractText hit IndexOutOfBoundsException. This may be normal.", var4);
            return false;
        }
    }

    public void addObject(final T object, final CharSequence sourceText) {
        this.post(new Runnable() {
            public void run() {
                if (object != null) {
                    if (TokenCompleteTextView.this.allowDuplicates || !TokenCompleteTextView.this.objects.contains(object)) {
                        if (TokenCompleteTextView.this.tokenLimit == -1 || TokenCompleteTextView.this.objects.size() != TokenCompleteTextView.this.tokenLimit) {
                            TokenCompleteTextView.this.insertSpan(object, sourceText);
                            if (TokenCompleteTextView.this.getText() != null && TokenCompleteTextView.this.isFocused()) {
                                TokenCompleteTextView.this.setSelection(TokenCompleteTextView.this.getText().length());
                            }

                        }
                    }
                }
            }
        });
    }

    public void addObject(T object) {
        this.addObject(object, "");
    }

    public void removeObject(final T object) {
        this.post(new Runnable() {
            public void run() {
                Editable text = TokenCompleteTextView.this.getText();
                if (text != null) {
                    ArrayList<TokenCompleteTextView<T>.TokenImageSpan> toRemove = new ArrayList();
                    Iterator var3 = TokenCompleteTextView.this.hiddenSpans.iterator();

                    TokenCompleteTextView.TokenImageSpan spanx;
                    while(var3.hasNext()) {
                        spanx = (TokenCompleteTextView.TokenImageSpan)var3.next();
                        if (spanx.getToken().equals(object)) {
                            toRemove.add(spanx);
                        }
                    }

                    var3 = toRemove.iterator();

                    while(var3.hasNext()) {
                        spanx = (TokenCompleteTextView.TokenImageSpan)var3.next();
                        TokenCompleteTextView.this.hiddenSpans.remove(spanx);
                        TokenCompleteTextView.this.spanWatcher.onSpanRemoved(text, spanx, 0, 0);
                    }

                    TokenCompleteTextView.this.updateCountSpan();
                    TokenCompleteTextView<T>.TokenImageSpan[] spans = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenImageSpan.class);
                    TokenCompleteTextView.TokenImageSpan[] var9 = spans;
                    int var5 = spans.length;

                    for(int var6 = 0; var6 < var5; ++var6) {
                        TokenCompleteTextView<T>.TokenImageSpan span = var9[var6];
                        if (span.getToken().equals(object)) {
                            TokenCompleteTextView.this.removeSpan(span);
                        }
                    }

                }
            }
        });
    }

    private void updateCountSpan() {
        Editable text = this.getText();
        CountSpan[] counts = (CountSpan[])text.getSpans(0, text.length(), CountSpan.class);
        int newCount = this.hiddenSpans.size();
        CountSpan[] var4 = counts;
        int var5 = counts.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            CountSpan count = var4[var6];
            if (newCount == 0) {
                text.delete(text.getSpanStart(count), text.getSpanEnd(count));
                text.removeSpan(count);
            } else {
                count.setCount(this.hiddenSpans.size());
                text.setSpan(count, text.getSpanStart(count), text.getSpanEnd(count), 33);
            }
        }

    }

    private void removeSpan(TokenCompleteTextView<T>.TokenImageSpan span) {
        Editable text = this.getText();
        if (text != null) {
            TokenCompleteTextView<T>.TokenSpanWatcher[] spans = (TokenCompleteTextView.TokenSpanWatcher[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenSpanWatcher.class);
            if (spans.length == 0) {
                this.spanWatcher.onSpanRemoved(text, span, text.getSpanStart(span), text.getSpanEnd(span));
            }

            text.delete(text.getSpanStart(span), text.getSpanEnd(span) + 1);
            if (this.allowCollapse && !this.isFocused()) {
                this.updateCountSpan();
            }

        }
    }

    private void insertSpan(T object, CharSequence sourceText) {
        SpannableStringBuilder ssb = this.buildSpannableForText(sourceText);
        TokenCompleteTextView<T>.TokenImageSpan tokenSpan = this.buildSpanForObject(object);
        Editable editable = this.getText();
        if (editable != null) {
            if (this.allowCollapse && !this.isFocused() && !this.hiddenSpans.isEmpty()) {
                this.hiddenSpans.add(tokenSpan);
                this.spanWatcher.onSpanAdded(editable, tokenSpan, 0, 0);
                this.updateCountSpan();
            } else {
                int offset = editable.length();
                if (this.hintVisible) {
                    offset = this.prefix.length();
                    editable.insert(offset, ssb);
                } else {
                    String completionText = this.currentCompletionText();
                    if (completionText != null && completionText.length() > 0) {
                        offset = TextUtils.indexOf(editable, completionText);
                    }

                    editable.insert(offset, ssb);
                }

                editable.setSpan(tokenSpan, offset, offset + ssb.length() - 1, 33);
                if (!this.isFocused() && this.allowCollapse) {
                    this.performCollapse(false);
                }

                if (!this.objects.contains(object)) {
                    this.spanWatcher.onSpanAdded(editable, tokenSpan, 0, 0);
                }
            }

        }
    }

    private void insertSpan(T object) {
        String spanString;
        if (this.deletionStyle == TokenCompleteTextView.TokenDeleteStyle.ToString) {
            spanString = object != null ? object.toString() : "";
        } else {
            spanString = "";
        }

        this.insertSpan(object, spanString);
    }

    private void insertSpan(TokenCompleteTextView<T>.TokenImageSpan span) {
        this.insertSpan(span.getToken());
    }

    public void clear() {
        this.post(new Runnable() {
            public void run() {
                Editable text = TokenCompleteTextView.this.getText();
                if (text != null) {
                    TokenCompleteTextView<T>.TokenImageSpan[] spans = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenImageSpan.class);
                    TokenCompleteTextView.TokenImageSpan[] var3 = spans;
                    int var4 = spans.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                        TokenCompleteTextView<T>.TokenImageSpan span = var3[var5];
                        TokenCompleteTextView.this.removeSpan(span);
                        TokenCompleteTextView.this.spanWatcher.onSpanRemoved(text, span, text.getSpanStart(span), text.getSpanEnd(span));
                    }

                }
            }
        });
    }

    private void updateHint() {
        Editable text = this.getText();
        CharSequence hintText = this.getHint();
        if (text != null && hintText != null) {
            if (this.prefix.length() > 0) {
                HintSpan[] hints = (HintSpan[])text.getSpans(0, text.length(), HintSpan.class);
                HintSpan hint = null;
                int testLength = this.prefix.length();
                if (hints.length > 0) {
                    hint = hints[0];
                    testLength += text.getSpanEnd(hint) - text.getSpanStart(hint);
                }

                int style;
                if (text.length() == testLength) {
                    this.hintVisible = true;
                    if (hint != null) {
                        return;
                    }

                    Typeface tf = this.getTypeface();
                    style = 0;
                    if (tf != null) {
                        style = tf.getStyle();
                    }

                    ColorStateList colors = this.getHintTextColors();
                    HintSpan hintSpan = new HintSpan((String)null, style, (int)this.getTextSize(), colors, colors);
                    text.insert(this.prefix.length(), hintText);
                    text.setSpan(hintSpan, this.prefix.length(), this.prefix.length() + this.getHint().length(), 33);
                    this.setSelection(this.prefix.length());
                } else {
                    if (hint == null) {
                        return;
                    }

                    int sStart = text.getSpanStart(hint);
                    style = text.getSpanEnd(hint);
                    text.removeSpan(hint);
                    text.replace(sStart, style, "");
                    this.hintVisible = false;
                }
            }

        }
    }

    private void clearSelections() {
        if (this.tokenClickStyle != null && this.tokenClickStyle.isSelectable()) {
            Editable text = this.getText();
            if (text != null) {
                TokenCompleteTextView<T>.TokenImageSpan[] tokens = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenImageSpan.class);
                TokenCompleteTextView.TokenImageSpan[] var3 = tokens;
                int var4 = tokens.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    TokenCompleteTextView<T>.TokenImageSpan token = var3[var5];
                    token.view.setSelected(false);
                }

                this.invalidate();
            }
        }
    }

    protected ArrayList<Serializable> getSerializableObjects() {
        ArrayList<Serializable> serializables = new ArrayList();
        Iterator var2 = this.getObjects().iterator();

        while(var2.hasNext()) {
            Object obj = var2.next();
            if (obj instanceof Serializable) {
                serializables.add((Serializable)obj);
            } else {
                Log.e("TokenAutoComplete", "Unable to save '" + obj + "'");
            }
        }

        if (serializables.size() != this.objects.size()) {
            String message = "You should make your objects Serializable or override\ngetSerializableObjects and convertSerializableArrayToObjectArray";
            Log.e("TokenAutoComplete", message);
        }

        return serializables;
    }

    protected ArrayList<T> convertSerializableArrayToObjectArray(ArrayList<Serializable> s) {
        return (ArrayList<T>) s;
    }

    public Parcelable onSaveInstanceState() {
        ArrayList<Serializable> baseObjects = this.getSerializableObjects();
        this.removeListeners();
        this.savingState = true;
        Parcelable superState = super.onSaveInstanceState();
        this.savingState = false;
        TokenCompleteTextView.SavedState state = new TokenCompleteTextView.SavedState(superState);
        state.prefix = this.prefix;
        state.allowCollapse = this.allowCollapse;
        state.allowDuplicates = this.allowDuplicates;
        state.performBestGuess = this.performBestGuess;
        state.tokenClickStyle = this.tokenClickStyle;
        state.tokenDeleteStyle = this.deletionStyle;
        state.baseObjects = baseObjects;
        state.splitChar = this.splitChar;
        this.addListeners();
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof TokenCompleteTextView.SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            TokenCompleteTextView.SavedState ss = (TokenCompleteTextView.SavedState)state;
            super.onRestoreInstanceState(ss.getSuperState());
            this.setText(ss.prefix);
            this.prefix = ss.prefix;
            this.updateHint();
            this.allowCollapse = ss.allowCollapse;
            this.allowDuplicates = ss.allowDuplicates;
            this.performBestGuess = ss.performBestGuess;
            this.tokenClickStyle = ss.tokenClickStyle;
            this.deletionStyle = ss.tokenDeleteStyle;
            this.splitChar = ss.splitChar;
            this.addListeners();
            Iterator var3 = this.convertSerializableArrayToObjectArray(ss.baseObjects).iterator();

            while(var3.hasNext()) {
                T obj = (T) var3.next();
                this.addObject(obj);
            }

            if (!this.isFocused() && this.allowCollapse) {
                this.post(new Runnable() {
                    public void run() {
                        TokenCompleteTextView.this.performCollapse(TokenCompleteTextView.this.isFocused());
                    }
                });
            }

        }
    }

    public boolean canDeleteSelection(int beforeLength) {
        if (this.objects.size() < 1) {
            return true;
        } else {
            int endSelection = this.getSelectionEnd();
            int startSelection = beforeLength == 1 ? this.getSelectionStart() : endSelection - beforeLength;
            Editable text = this.getText();
            TokenCompleteTextView<T>.TokenImageSpan[] spans = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(0, text.length(), TokenCompleteTextView.TokenImageSpan.class);
            TokenCompleteTextView.TokenImageSpan[] var6 = spans;
            int var7 = spans.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                TokenCompleteTextView<T>.TokenImageSpan span = var6[var8];
                int startTokenSelection = text.getSpanStart(span);
                int endTokenSelection = text.getSpanEnd(span);
                if (!this.isTokenRemovable(span.token)) {
                    if (startSelection == endSelection) {
                        if (endTokenSelection + 1 == endSelection) {
                            return false;
                        }
                    } else if (startSelection <= startTokenSelection && endTokenSelection + 1 <= endSelection) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private class TokenInputConnection extends InputConnectionWrapper {
        public TokenInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        public boolean deleteSurroundingText(int beforeLengthx, int afterLength) {
            if (!TokenCompleteTextView.this.canDeleteSelection(beforeLengthx)) {
                return false;
            } else if (TokenCompleteTextView.this.getSelectionStart() > TokenCompleteTextView.this.prefix.length()) {
                return super.deleteSurroundingText(beforeLengthx, afterLength);
            } else {
                int beforeLength = 0;
                return TokenCompleteTextView.this.deleteSelectedObject(false) || super.deleteSurroundingText(beforeLength, afterLength);
            }
        }
    }

    private static class SavedState extends BaseSavedState {
        CharSequence prefix;
        boolean allowCollapse;
        boolean allowDuplicates;
        boolean performBestGuess;
        TokenCompleteTextView.TokenClickStyle tokenClickStyle;
        TokenCompleteTextView.TokenDeleteStyle tokenDeleteStyle;
        ArrayList<Serializable> baseObjects;
        char[] splitChar;
        public static final Creator<TokenCompleteTextView.SavedState> CREATOR = new Creator<TokenCompleteTextView.SavedState>() {
            public TokenCompleteTextView.SavedState createFromParcel(Parcel in) {
                return new TokenCompleteTextView.SavedState(in);
            }

            public TokenCompleteTextView.SavedState[] newArray(int size) {
                return new TokenCompleteTextView.SavedState[size];
            }
        };

        SavedState(Parcel in) {
            super(in);
            this.prefix = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.allowCollapse = in.readInt() != 0;
            this.allowDuplicates = in.readInt() != 0;
            this.performBestGuess = in.readInt() != 0;
            this.tokenClickStyle = TokenCompleteTextView.TokenClickStyle.values()[in.readInt()];
            this.tokenDeleteStyle = TokenCompleteTextView.TokenDeleteStyle.values()[in.readInt()];
            this.baseObjects = (ArrayList)in.readSerializable();
            this.splitChar = in.createCharArray();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            TextUtils.writeToParcel(this.prefix, out, 0);
            out.writeInt(this.allowCollapse ? 1 : 0);
            out.writeInt(this.allowDuplicates ? 1 : 0);
            out.writeInt(this.performBestGuess ? 1 : 0);
            out.writeInt(this.tokenClickStyle.ordinal());
            out.writeInt(this.tokenDeleteStyle.ordinal());
            out.writeSerializable(this.baseObjects);
            out.writeCharArray(this.splitChar);
        }

        public String toString() {
            String str = "TokenCompleteTextView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " tokens=" + this.baseObjects;
            return str + "}";
        }
    }

    private class TokenTextWatcher implements TextWatcher {
        ArrayList<TokenCompleteTextView<T>.TokenImageSpan> spansToRemove;

        private TokenTextWatcher() {
            this.spansToRemove = new ArrayList();
        }

        protected void removeToken(TokenCompleteTextView<T>.TokenImageSpan token, Editable text) {
            text.removeSpan(token);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0 && TokenCompleteTextView.this.getText() != null) {
                Editable text = TokenCompleteTextView.this.getText();
                int end = start + count;
                if (text.charAt(start) == ' ') {
                    --start;
                }

                TokenCompleteTextView<T>.TokenImageSpan[] spans = (TokenCompleteTextView.TokenImageSpan[])text.getSpans(start, end, TokenCompleteTextView.TokenImageSpan.class);
                ArrayList<TokenCompleteTextView<T>.TokenImageSpan> spansToRemove = new ArrayList();
                TokenCompleteTextView.TokenImageSpan[] var9 = spans;
                int var10 = spans.length;

                for(int var11 = 0; var11 < var10; ++var11) {
                    TokenCompleteTextView<T>.TokenImageSpan token = var9[var11];
                    if (text.getSpanStart(token) < end && start < text.getSpanEnd(token)) {
                        spansToRemove.add(token);
                    }
                }

                this.spansToRemove = spansToRemove;
            }

        }

        public void afterTextChanged(Editable text) {
            ArrayList<TokenCompleteTextView<T>.TokenImageSpan> spansCopy = new ArrayList(this.spansToRemove);
            this.spansToRemove.clear();
            Iterator var3 = spansCopy.iterator();

            while(var3.hasNext()) {
                TokenCompleteTextView<T>.TokenImageSpan token = (TokenCompleteTextView.TokenImageSpan)var3.next();
                int spanStart = text.getSpanStart(token);
                int spanEnd = text.getSpanEnd(token);
                this.removeToken(token, text);
                --spanEnd;
                if (spanEnd >= 0 && TokenCompleteTextView.this.isSplitChar(text.charAt(spanEnd))) {
                    text.delete(spanEnd, spanEnd + 1);
                }

                if (spanStart >= 0 && TokenCompleteTextView.this.isSplitChar(text.charAt(spanStart))) {
                    text.delete(spanStart, spanStart + 1);
                }
            }

            TokenCompleteTextView.this.clearSelections();
            TokenCompleteTextView.this.updateHint();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private class TokenSpanWatcher implements SpanWatcher {
        private TokenSpanWatcher() {
        }

        public void onSpanAdded(Spannable text, Object what, int start, int end) {
            if (what instanceof TokenCompleteTextView.TokenImageSpan && !TokenCompleteTextView.this.savingState && !TokenCompleteTextView.this.focusChanging) {
                TokenCompleteTextView<T>.TokenImageSpan token = (TokenCompleteTextView.TokenImageSpan)what;
                TokenCompleteTextView.this.objects.add(token.getToken());
                if (TokenCompleteTextView.this.listener != null) {
                    TokenCompleteTextView.this.listener.onTokenAdded(token.getToken());
                }
            }

        }

        public void onSpanRemoved(Spannable text, Object what, int start, int end) {
            if (what instanceof TokenCompleteTextView.TokenImageSpan && !TokenCompleteTextView.this.savingState && !TokenCompleteTextView.this.focusChanging) {
                TokenCompleteTextView<T>.TokenImageSpan token = (TokenCompleteTextView.TokenImageSpan)what;
                if (TokenCompleteTextView.this.objects.contains(token.getToken())) {
                    TokenCompleteTextView.this.objects.remove(token.getToken());
                }

                if (TokenCompleteTextView.this.listener != null) {
                    TokenCompleteTextView.this.listener.onTokenRemoved(token.getToken());
                }
            }

        }

        public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        }
    }

    public interface TokenListener<T> {
        void onTokenAdded(T var1);

        void onTokenRemoved(T var1);
    }

    protected class TokenImageSpan extends ViewSpan implements NoCopySpan {
        private T token;

        public TokenImageSpan(View d, T token, int maxWidth) {
            super(d, maxWidth);
            this.token = token;
        }

        public T getToken() {
            return this.token;
        }

        public void onClick() {
            Editable text = TokenCompleteTextView.this.getText();
            if (text != null) {
                switch(TokenCompleteTextView.this.tokenClickStyle) {
                    case Select:
                    case SelectDeselect:
                        if (!this.view.isSelected()) {
                            TokenCompleteTextView.this.clearSelections();
                            this.view.setSelected(true);
                            return;
                        }

                        if (TokenCompleteTextView.this.tokenClickStyle == TokenCompleteTextView.TokenClickStyle.SelectDeselect || !TokenCompleteTextView.this.isTokenRemovable(this.token)) {
                            this.view.setSelected(false);
                            TokenCompleteTextView.this.invalidate();
                            return;
                        }
                    case Delete:
                        break;
                    case None:
                    default:
                        if (TokenCompleteTextView.this.getSelectionStart() != text.getSpanEnd(this) + 1) {
                            TokenCompleteTextView.this.setSelection(text.getSpanEnd(this) + 1);
                        }

                        return;
                }

                if (TokenCompleteTextView.this.isTokenRemovable(this.token)) {
                    TokenCompleteTextView.this.removeSpan(this);
                }

            }
        }
    }

    public static enum TokenClickStyle {
        None(false),
        Delete(false),
        Select(true),
        SelectDeselect(true);

        private boolean mIsSelectable = false;

        private TokenClickStyle(boolean selectable) {
            this.mIsSelectable = selectable;
        }

        public boolean isSelectable() {
            return this.mIsSelectable;
        }
    }

    public static enum TokenDeleteStyle {
        _Parent,
        Clear,
        PartialCompletion,
        ToString;

        private TokenDeleteStyle() {
        }
    }
}
