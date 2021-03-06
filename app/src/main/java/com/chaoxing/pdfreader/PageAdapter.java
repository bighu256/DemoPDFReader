package com.chaoxing.pdfreader;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaoxing.pdfreader.util.Utils;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.List;

/**
 * Created by bighu on 2018/4/1.
 */

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PageAdapter.class.getSimpleName();

    private List<Resource<PageProfile>> mPageList;

    private PageListener pageListener;

    public PageAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.libpdf_item_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PageViewHolder viewHolder = (PageViewHolder) holder;
        Resource<PageProfile> pageResource = mPageList.get(position);

        if (pageResource.isIdle()) {
            viewHolder.mPageView.recycle();
            viewHolder.mLoadingStatus.setVisibility(View.GONE);
            pageListener.loadPage(position);
        } else if (pageResource.isLoading()) {
            viewHolder.mPageView.recycle();
            viewHolder.mLoadingView.setVisibility(View.VISIBLE);
            viewHolder.mTvMessage.setVisibility(View.GONE);
            viewHolder.mBtnRetry.setVisibility(View.GONE);
            viewHolder.mLoadingStatus.setVisibility(View.VISIBLE);
        } else if (pageResource.isSuccessful()) {
            viewHolder.mLoadingStatus.setVisibility(View.GONE);
            PageProfile profile = pageResource.getData();
            File pageFile = null;
            if (!Utils.isBlank(profile.getPageFile())) {
                File file = new File(profile.getPageFile());
                if (file.exists() && file.isFile()) {
                    pageFile = file;
                }
            }
            if (pageFile != null) {
                if (viewHolder.itemView.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    viewHolder.mPageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                } else {
                    viewHolder.mPageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
                }
                viewHolder.mPageView.setImage(ImageSource.uri(Uri.fromFile(pageFile)));
            } else {
                pageResource = Resource.error("页面加载失败", profile);

            }
        }

        viewHolder.mPageView.setOnPageClickListener(mOnPageClickListener);

        if (pageResource.isError()) {
            viewHolder.mPageView.recycle();
            viewHolder.mLoadingView.setVisibility(View.GONE);
            viewHolder.mTvMessage.setText(pageResource.getMessage());
            viewHolder.mTvMessage.setVisibility(View.VISIBLE);
            viewHolder.mBtnRetry.setVisibility(View.VISIBLE);
            viewHolder.mBtnRetry.setOnClickListener(view -> {
                pageListener.loadPage(position);
            });
            viewHolder.mLoadingStatus.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mPageList == null ? 0 : mPageList.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {

        SubsamplingScaleImageView mPageView;
        View mLoadingStatus;
        ProgressBar mLoadingView;
        TextView mTvMessage;
        Button mBtnRetry;

        public PageViewHolder(View itemView) {
            super(itemView);
            mPageView = itemView.findViewById(R.id.page_view);
            mLoadingStatus = itemView.findViewById(R.id.loading_status);
            mLoadingView = itemView.findViewById(R.id.loading_view);
            mTvMessage = itemView.findViewById(R.id.tv_message);
            mBtnRetry = itemView.findViewById(R.id.btn_retry);
        }

    }

    public void setPageList(List<Resource<PageProfile>> pageList) {
        if (mPageList == null) {
            mPageList = pageList;
            notifyItemRangeInserted(0, pageList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mPageList.size();
                }

                @Override
                public int getNewListSize() {
                    return pageList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return false;
                }
            });
            mPageList = pageList;
            result.dispatchUpdatesTo(this);
        }
    }

    public void updatePage(Resource<PageProfile> page) {
        PageProfile profile = page.getData();
        if (profile != null) {
            int pageNumber = profile.getNumber();
            mPageList.set(pageNumber, page);
            notifyItemChanged(pageNumber);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        Log.i(TAG, "onViewRecycled : " + position);
        PageViewHolder viewHolder = (PageViewHolder) holder;
        viewHolder.mPageView.recycle();
    }

    private OnPageClickListener mOnPageClickListener = new OnPageClickListener() {
        @Override
        public void onClick(View view, MotionEvent e) {
            pageListener.onPageClicked(view, e);
        }
    };

    public interface PageListener {
        void loadPage(int pageNumber);

        void onPageClicked(View view, MotionEvent e);
    }

    public void setPageListener(PageListener pageListener) {
        this.pageListener = pageListener;
    }

}
