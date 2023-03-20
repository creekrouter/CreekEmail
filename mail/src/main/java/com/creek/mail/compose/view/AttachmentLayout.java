package com.creek.mail.compose.view;

import static com.creek.mail.compose.ComposeActivity.REQUEST_CODE_CHOOSE;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.creek.common.MailAttachment;
import com.creek.common.filepicker.FilePicker;
import com.creek.common.photo.EasyPhotos;
import com.creek.common.photo.callback.SelectCallback;
import com.creek.common.photo.models.album.entity.Photo;
import com.creek.mail.R;
import com.creek.mail.compose.manager.DataManager;
import com.creek.mail.compose.msg.ComposeHandler;
import com.creek.mail.compose.msg.EventID;
import com.mail.tools.context.AppContext;
import com.mail.tools.permission.OnPermissionCallback;
import com.mail.tools.permission.Permission;
import com.mail.tools.permission.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AttachmentLayout extends RelativeLayout implements View.OnClickListener {
    public AttachmentLayout(Context context) {
        super(context);
        init(context);
    }

    public AttachmentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private View mRootView;
    private AttachmentView mAttachView;
    private ImageView mTakePhoto, mSelectPic, mSelectFile;
    private DataManager mDataManager;
    private ComposeHandler mHandler;

    private void init(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.compose_email_attach_layout, this);

        mAttachView = mRootView.findViewById(R.id.compose_email_attachment_view);

        mTakePhoto = mRootView.findViewById(R.id.rich_edit_capture);
        mSelectPic = mRootView.findViewById(R.id.rich_edit_photo);
        mSelectFile = mRootView.findViewById(R.id.rich_edit_file);

        mTakePhoto.setOnClickListener(this);
        mSelectPic.setOnClickListener(this);
        mSelectFile.setOnClickListener(this);
    }

    public AttachmentView getAttachView() {
        return mAttachView;
    }

    public void setManager(DataManager dataManager,ComposeHandler handler) {
        mDataManager = dataManager;
        mHandler = handler;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rich_edit_capture) {

            XXPermissions.with(getContext())
                    .permission(Permission.CAMERA)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            Toast.makeText(getContext(), "a", Toast.LENGTH_SHORT).show();

                        }
                    });

        } else if (id == R.id.rich_edit_photo) {
            XXPermissions.with(getContext())
                    .permission(Permission.READ_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            selectImage();
                        }
                    });

        } else if (id == R.id.rich_edit_file) {
            XXPermissions.with(getContext())
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                return;
                            }
                            fileSelect();
                        }
                    });

        }
    }

    private void fileSelect(){
        FilePicker
                .from(AppContext.getTopActivity())
                .chooseForMimeType()
                .setMaxCount(10)
                .setFileTypes("png", "doc","apk", "mp3", "gif", "txt", "mp4", "zip")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }

    private void selectImage() {

        EasyPhotos.createAlbum(AppContext.getTopActivity(), true, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.creek.mail.fileprovider")
                .setCount(9)
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        if (photos == null) {
                            return;
                        }
                        for (Photo photo : photos) {
                            File f = new File(photo.path);
                            MailAttachment attach = new MailAttachment();
                            attach.setFile_path(f.getAbsolutePath());
                            attach.setFile_name(f.getName());
                            attach.setDownloadStateFinished();
                            attach.setFile_size(f.length());
                            mDataManager.mAttachmentList.add(attach);
                        }
                        mHandler.sendEvent(EventID.Compose_Mail_Refresh_Attachment_List);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }
}
