
package com.creek.mail.pages;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.creek.common.BaseActivity;
import com.creek.common.router.Launcher;
import com.creek.common.view.PreViewImage;
import com.creek.router.annotation.CreekBean;


@CreekBean(path = "activity_image_view_activity")
public class ImageViewActivity extends BaseActivity {

    public static void goToImageActivity(Context context, String filePath) {
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        Launcher.startActivity(context,ImageViewActivity.class,bundle);
    }


    private PreViewImage mImg;
    private boolean doubleTapFlag = false;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (!doubleTapFlag) {
                    finish();
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImg = new PreViewImage(mContext);
        mImg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mImg);
        mImg.setBackgroundColor(Color.BLACK);

        String imagePath = getIntent().getStringExtra("filePath");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        mImg.setImageBitmap(bitmap);

        mImg.setOnImageViewTapListener(new PreViewImage.ImageViewTap() {
            @Override
            public void onImageSingleTapUp() {
                doubleTapFlag = false;
                mHandler.sendEmptyMessageDelayed(100, 500);
            }

            @Override
            public void onImageDoubleTap() {
                doubleTapFlag = true;
            }
        });
    }


}