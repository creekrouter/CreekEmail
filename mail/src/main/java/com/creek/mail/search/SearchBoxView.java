package com.creek.mail.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.creek.mail.R;


public class SearchBoxView extends LinearLayout {

    private static final int DEFAULT_COLOR = 0xFF999999;

    private LinearLayout searchBoxBgLL;
    private ImageView searchBtnIV;
    private EditText searchInputET;
    private ImageView searchDelIV;
    private ImageView searchVoiceIV;

    private boolean voiceEnable = false;
    private boolean enableInput = true;
    private String inputHint;
    private int inputHintColor;
    private String inputChangeBeforeStr;

    private SearchClickListener mSearchClickListener;
    private EditChangedListener mChangedListener;
    private OnClearClickListener onClearClickListener;
    private SearchVoiceClickListener mVoiceClickListener;
    private SearchVoiceResultCallBack mCallBack;
    private float textSize;

    public SearchBoxView(Context context) {
        super(context);
        initView(context);
    }

    public SearchBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.search_box_view);
        voiceEnable = array.getBoolean(R.styleable.search_box_view_voiceEnable, false);
        enableInput = array.getBoolean(R.styleable.search_box_view_enableInput, true);
        inputHint = array.getString(R.styleable.search_box_view_inputHint);
        inputHintColor = array.getColor(R.styleable.search_box_view_inputHintColor, DEFAULT_COLOR);
        textSize = array.getDimension(R.styleable.search_box_view_documentSize, 16);


        initView(context);
        array.recycle();
    }

    public SearchBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private View initView(Context context) {

        View view = inflate(context, R.layout.search_box_view, this);

        searchBoxBgLL = view.findViewById(R.id.ll_search_box_bg);
        searchBtnIV = (ImageView) view.findViewById(R.id.iv_search_btn);
        searchInputET = (EditText) view.findViewById(R.id.et_search_input);
        searchDelIV = (ImageView) view.findViewById(R.id.iv_search_delete);
        searchVoiceIV = (ImageView) view.findViewById(R.id.iv_search_voice);

        searchInputET.setHintTextColor(inputHintColor);
        //TypedValue.COMPLEX_UNIT_SP改为TypedValue.COMPLEX_UNIT_DIP，目的是为适应文字大小的设置改变时，搜索框不受影响的需求
        searchInputET.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

        //临时去掉
        if (voiceEnable) {
//            searchVoiceIV.setVisibility(View.VISIBLE);
            searchVoiceIV.setVisibility(View.GONE);

        } else {
            searchVoiceIV.setVisibility(View.GONE);
        }

        //====<<<<< 取消该功能
//        searchVoiceIV.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mVoiceClickListener != null) {
//                    mVoiceClickListener.voiceClick(v);
//                    return;
//                }
//                startVoice();
//            }
//        });


        if (!TextUtils.isEmpty(inputHint)) {
            searchInputET.setHint(inputHint);
        }

        setInputAble(searchInputET, enableInput);

        searchDelIV.setVisibility(View.GONE);
        searchDelIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInputET.setText("");
                searchDelIV.setVisibility(View.GONE);
                if (onClearClickListener != null) {
                    onClearClickListener.onClear();
                }
            }
        });
//        searchInputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    if (mSearchClickListener != null) {
//                        mSearchClickListener.startSearch(searchInputET.getText().toString().trim());
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
        searchInputET.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
                    if (mSearchClickListener != null) {
                        mSearchClickListener.startSearch(searchInputET.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });
        searchInputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputChangeBeforeStr = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text)) {
                    searchDelIV.setVisibility(View.VISIBLE);
                } else {
                    searchDelIV.setVisibility(View.GONE);
                }
                if (mChangedListener != null && !s.toString().trim().equals(inputChangeBeforeStr)) {
                    mChangedListener.changedSearch(s.toString().trim());
                }
            }
        });

        return view;
    }


    /**
     * 设置editText的输入状态
     */
    private void setInputAble(EditText et, boolean inputAble) {
        et.setFocusable(inputAble);
        et.setClickable(inputAble);
        et.setEnabled(inputAble);
        et.setFocusableInTouchMode(inputAble);
    }

    public String getInputText() {
        return searchInputET.getText().toString().trim();
    }

    public void setSearchInputHint(String hint) {
        if (!TextUtils.isEmpty(hint)) {
            searchInputET.setHint(hint);
        }
    }

    public void setSearchInputHintColor(int color) {
        searchInputET.setHintTextColor(color);
    }

    /**
     * 设置搜索内容
     */
    public void setSearchText(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            return;
        }
        searchInputET.setText(searchText);
        searchInputET.setSelection(searchText.length());
    }

    //设置搜索框背景
    public void setSearchBoxBg(int resId) {
        searchBoxBgLL.setBackgroundResource(resId);
    }

    public EditText getSearchInputET() {
        return searchInputET;
    }

    public void setSearchClickListener(SearchClickListener searchClickListener) {
        if (searchClickListener != null) {
            mSearchClickListener = searchClickListener;
        }
    }

    public void setChangedListener(EditChangedListener changedListener) {
        if (changedListener != null) {
            mChangedListener = changedListener;
        }
    }

    /**
     * 设置语音搜索按钮点击监听，若设置此监听，则点击语音搜索按钮，则立马回调
     */
    public void setSearchVoiceClickListener(SearchVoiceClickListener searchVoiceClickListener) {
        if (searchVoiceClickListener != null) {
            mVoiceClickListener = searchVoiceClickListener;
        }
    }


    public void setSearchVoiceCallBack(SearchVoiceResultCallBack callBack) {
        mCallBack = callBack;
    }

    public void setOnClearClickListener(OnClearClickListener clearClickListener) {
        this.onClearClickListener = clearClickListener;
    }

    public interface SearchVoiceResultCallBack {
        void voiceResult(String result);
    }

    public interface SearchVoiceClickListener {
        void voiceClick(View v);
    }

    public interface SearchClickListener {
        void startSearch(String searchKey);
    }

    public interface EditChangedListener {
        void changedSearch(String searchKey);
    }

    public interface OnClearClickListener {
        void onClear();
    }
}
