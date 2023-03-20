package com.creek.common.photo.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creek.common.R;
import com.creek.common.photo.ui.adapter.PreviewPhotosFragmentAdapter;


public class PreviewFragment extends Fragment implements PreviewPhotosFragmentAdapter.OnClickListener {

    private OnPreviewFragmentClickListener mListener;

    private RecyclerView rvPhotos;
    private PreviewPhotosFragmentAdapter adapter;


    public PreviewFragment(Activity activity, OnPreviewFragmentClickListener listener) {
        mListener = listener;
        adapter = new PreviewPhotosFragmentAdapter(activity, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preview_easy_photos, container, false);
        rvPhotos = (RecyclerView) rootView.findViewById(R.id.rv_preview_selected_photos);
        rvPhotos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvPhotos.setAdapter(adapter);
        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPhotoClick(int position) {
        mListener.onPreviewPhotoClick(position);
    }


    public interface OnPreviewFragmentClickListener {
        void onPreviewPhotoClick(int position);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        adapter.setChecked(position);
        if (position != -1) {
            rvPhotos.smoothScrollToPosition(position);
        }
    }

}
