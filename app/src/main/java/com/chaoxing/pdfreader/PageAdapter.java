package com.chaoxing.pdfreader;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bighu on 2018/4/1.
 */

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mImageList = new ArrayList<>();

    public PageAdapter() {
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
        mImageList.add("android_p.png");
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PageViewHolder viewHolder = (PageViewHolder) holder;
//        viewHolder.mPageView.setImage(ImageSource.asset(mImageList.get(position)));
        viewHolder.mPageView.setImage(ImageSource.resource(R.mipmap.d));
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {

        SubsamplingScaleImageView mPageView;

        public PageViewHolder(View itemView) {
            super(itemView);
            mPageView = itemView.findViewById(R.id.page_view);
        }
    }
}
