package com.chaoxing.pdfreader;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoxing.pdfreader.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class PDFActivity extends AppCompatActivity {

    private static final String TAG = PDFActivity.class.getSimpleName();

    private PDFViewModel mViewModel;

    private View mToolbar;
    private ImageView mIvLeft;
    private TextView mTvTitle;
    private ImageView mIvRight;
    private View mBottomBar;
    private TextView mTvPageNumber;
    private RecyclerView mDocumentPager;
    private PageAdapter mPageAdapter;
    private AlertDialog mInputPasswordDialog;
    private ProgressBar mPbLoading;
    private TextView mTvMessage;

    private PageLoader mPageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        mViewModel = ViewModelProviders.of(this).get(PDFViewModel.class);

        Uri uri = getIntent().getData();
        String mimetype = getIntent().getType();
        String uriStr = uri.toString();

        final String path = UriUtils.getRealPath(this, uri);

        if (path == null) {
            finish();
            return;
        }

        initView();
        initDocument();

        mViewModel.openDocument(path);
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mIvLeft = findViewById(R.id.iv_left);
        mIvLeft.setOnClickListener(mOnClickListener);
        mTvTitle = findViewById(R.id.tv_title);
        mIvRight = findViewById(R.id.iv_right);
        mIvRight.setOnClickListener(mOnClickListener);
        mBottomBar = findViewById(R.id.bottomBar);
        mTvPageNumber = findViewById(R.id.tv_page_number);
        mDocumentPager = findViewById(R.id.document_pager);
        setPagerLayoutManager();
        mDocumentPager.setHasFixedSize(true);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mDocumentPager);
        mDocumentPager.addOnScrollListener(mOnPagerScrollListener);
        mPageAdapter = new PageAdapter();
        mPageAdapter.setPageListener(mPageListener);
        mDocumentPager.setItemViewCacheSize(1);
        mDocumentPager.setAdapter(mPageAdapter);
        mPbLoading = findViewById(R.id.pb_loading);
        mTvMessage = findViewById(R.id.tv_message);
    }

    private void setPagerLayoutManager() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDocumentPager.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            mDocumentPager.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_left) {
                onBackPressed();
            } else if (id == R.id.iv_right) {

            }
        }
    };

    private void initDocument() {
        mViewModel.getOpenDocumentResult().observe(this, mObserverOpenDocument);
        mViewModel.getCheckPasswordResult().observe(PDFActivity.this, mObserverCheckPassword);
        mViewModel.getLoadDocumentResult().observe(this, mObserverLoadDocument);
        mViewModel.getLoadPageResult().observe(this, mObserverLoadPage);
    }


    private Observer<Resource<DocumentBinding>> mObserverOpenDocument = new Observer<Resource<DocumentBinding>>() {
        @Override
        public void onChanged(@Nullable Resource<DocumentBinding> documentBinding) {
            Status status = documentBinding.getStatus();
            if (status == Status.LOADING) {
                mTvMessage.setVisibility(View.GONE);
                mPbLoading.setVisibility(View.VISIBLE);
            } else if (status == Status.ERROR) {
                mPbLoading.setVisibility(View.GONE);
                mTvMessage.setText(documentBinding.getMessage());
                mTvMessage.setVisibility(View.VISIBLE);
            } else if (status == Status.SUCCESS) {
                if (documentBinding.getData().isNeedsPassword()) {
                    mPbLoading.setVisibility(View.GONE);
                    askPassword();
                } else {
                    loadDocument();
                }
            }
        }
    };

    private void askPassword() {
        if (mInputPasswordDialog == null) {
            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(Utils.dp2px(this, 24), Utils.dp2px(this, 12), Utils.dp2px(this, 24), Utils.dp2px(this, 12));
            final AppCompatEditText etPassword = new AppCompatEditText(this);
            etPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            layout.addView(etPassword);

            mInputPasswordDialog = new AlertDialog.Builder(this)
                    .setTitle("输入密码")
                    .setView(layout)
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create();
            mInputPasswordDialog.show();
            mInputPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etPassword.length() > 0) {
                        mInputPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        mInputPasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                        mViewModel.checkPassword(etPassword.getText().toString());
                    }
                }
            });
        } else {
            mInputPasswordDialog.show();
        }
    }

    private Observer<Resource<Boolean>> mObserverCheckPassword = new Observer<Resource<Boolean>>() {
        @Override
        public void onChanged(@Nullable Resource<Boolean> result) {
            mInputPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            mInputPasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            if (result.getData()) {
                mInputPasswordDialog.dismiss();
                loadDocument();
            } else {
                Toast.makeText(PDFActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void loadDocument() {
        mViewModel.loadDocument();
    }

    private Observer<Resource<DocumentBinding>> mObserverLoadDocument = new Observer<Resource<DocumentBinding>>() {
        @Override
        public void onChanged(@Nullable Resource<DocumentBinding> documentBinding) {
            if (documentBinding.isLoading()) {
                mPbLoading.setVisibility(View.VISIBLE);
            } else if (documentBinding.isSuccessful()) {
                mPbLoading.setVisibility(View.GONE);
                mTvTitle.setText(mViewModel.getDocumentBinding().getTitle());
                int pageCount = documentBinding.getData().getPageCount();
                List<Resource<PageProfile>> pageList = new ArrayList<>(Collections.nCopies(pageCount, Resource.idle(null)));
                mPageAdapter.setPageList(pageList);
                setPageNumberText();
            } else {
                mPbLoading.setVisibility(View.GONE);
                Toast.makeText(PDFActivity.this, documentBinding.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    private Observer<Resource<PageProfile>> mObserverLoadPage = new Observer<Resource<PageProfile>>() {
        @Override
        public void onChanged(@Nullable Resource<PageProfile> resource) {
            mPageAdapter.updatePage(resource);
        }
    };

    private RecyclerView.OnScrollListener mOnPagerScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            setPageNumberText();
        }

    };

    private PageAdapter.PageListener mPageListener = new PageAdapter.PageListener() {
        @Override
        public void loadPage(int pageNumber) {
            mViewModel.loadPage(pageNumber, mDocumentPager.getWidth());
        }

        @Override
        public void onPageClicked(View view, MotionEvent e) {
            RectF centerRect;
            RectF leftEdgeRect;
            RectF rightEdgeRect;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                centerRect = new RectF(view.getWidth() / 4, view.getHeight() / 7, view.getWidth() / 4 * 3, view.getHeight() / 7 * 6);
                leftEdgeRect = new RectF(0, view.getHeight() / 7, view.getWidth() / 4, view.getHeight() / 7 * 6);
                rightEdgeRect = new RectF(view.getWidth() / 4 * 3, view.getHeight() / 7, view.getWidth(), view.getHeight() / 7 * 6);
            } else {
                centerRect = new RectF(view.getWidth() / 7, view.getHeight() / 4, view.getWidth() / 7 * 6, view.getHeight() / 4 * 3);
                leftEdgeRect = new RectF(0, view.getHeight() / 7, view.getWidth() / 4, view.getHeight() / 7 * 6);
                rightEdgeRect = new RectF(view.getWidth() / 4 * 3, view.getHeight() / 7, view.getWidth(), view.getHeight() / 7 * 6);
            }
            if (centerRect.contains(e.getX(), e.getY())) {
                switchBar();
            } else if (leftEdgeRect.contains(e.getX(), e.getY())) {
                int currentPage = ((LinearLayoutManager) mDocumentPager.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (currentPage > 0) {
                    mDocumentPager.smoothScrollToPosition(currentPage - 1);
                }
            } else if (rightEdgeRect.contains(e.getX(), e.getY())) {
                int currentPage = ((LinearLayoutManager) mDocumentPager.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (currentPage < mViewModel.getDocumentBinding().getPageCount() - 1) {
                    mDocumentPager.smoothScrollToPosition(currentPage + 1);
                }
            }
        }
    };

    private void setPageNumberText() {
        int currentPage = ((LinearLayoutManager) mDocumentPager.getLayoutManager()).findFirstVisibleItemPosition();
        if (currentPage >= 0) {
            mTvPageNumber.setText((currentPage + 1) + "/" + (mViewModel.getDocumentBinding().getPageCount()));
        }
    }

    private void switchBar() {
        if (mToolbar.getVisibility() == View.VISIBLE || mBottomBar.getVisibility() == View.VISIBLE) {
            hideBar();
        } else {
            showBar();
        }
    }

    private ObjectAnimator mShowToolbarAnimator;
    private Animator.AnimatorListener mShowToolbarAnimatorListener = new SimpleAnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mToolbar.setVisibility(View.VISIBLE);
            if (mHideToolbarAnimator != null && mHideToolbarAnimator.isRunning()) {
                mHideToolbarAnimator.cancel();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mToolbar.setTranslationY(0);
            mToolbar.setVisibility(View.VISIBLE);
        }
    };
    private ObjectAnimator mHideToolbarAnimator;
    private Animator.AnimatorListener mHideToolbarAnimatorListener = new SimpleAnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (mShowToolbarAnimator != null && mShowToolbarAnimator.isRunning()) {
                mShowToolbarAnimator.cancel();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mToolbar.setVisibility(View.GONE);
            mToolbar.setTranslationY(0);
        }
    };

    private ObjectAnimator mShowBottomBarAnimator;
    private Animator.AnimatorListener mShowBottomBarAnimatorListener = new SimpleAnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mBottomBar.setVisibility(View.VISIBLE);
            if (mHideBottomBarAnimator != null && mHideBottomBarAnimator.isRunning()) {
                mHideBottomBarAnimator.cancel();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBottomBar.setTranslationY(0);
            mBottomBar.setVisibility(View.VISIBLE);
        }
    };
    private ObjectAnimator mHideBottomBarAnimator;
    private Animator.AnimatorListener mHideBottomBarAnimatorListener = new SimpleAnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (mShowBottomBarAnimator != null && mShowBottomBarAnimator.isRunning()) {
                mShowBottomBarAnimator.cancel();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBottomBar.setVisibility(View.GONE);
            mBottomBar.setTranslationY(0);
        }
    };

    private void showBar() {
        if (mShowToolbarAnimator != null && mShowToolbarAnimator.isRunning()) {
            mShowToolbarAnimator.cancel();
        }
        int distance = mToolbar.getTop() + mToolbar.getHeight();
        mShowToolbarAnimator = ObjectAnimator.ofFloat(mToolbar, "translationY", -distance, 0);
        mShowToolbarAnimator.addListener(mShowToolbarAnimatorListener);
        mShowToolbarAnimator.start();

        if (mShowBottomBarAnimator != null && mShowBottomBarAnimator.isRunning()) {
            mShowBottomBarAnimator.cancel();
        }
        distance = ((ViewGroup) mBottomBar.getParent()).getHeight() - mBottomBar.getTop();
        mShowBottomBarAnimator = ObjectAnimator.ofFloat(mBottomBar, "translationY", distance, 0);
        mShowBottomBarAnimator.addListener(mShowBottomBarAnimatorListener);
        mShowBottomBarAnimator.start();
    }

    private void hideBar() {
        if (mHideToolbarAnimator != null && mHideToolbarAnimator.isRunning()) {
            mHideToolbarAnimator.cancel();
        }
        mHideToolbarAnimator = ObjectAnimator.ofFloat(mToolbar, "translationY", 0, -mToolbar.getBottom());
        mHideToolbarAnimator.addListener(mHideToolbarAnimatorListener);
        mHideToolbarAnimator.start();

        if (mHideBottomBarAnimator != null && mHideBottomBarAnimator.isRunning()) {
            mHideBottomBarAnimator.cancel();
        }
        int distance = ((ViewGroup) mBottomBar.getParent()).getHeight() - mBottomBar.getTop();
        mHideBottomBarAnimator = ObjectAnimator.ofFloat(mBottomBar, "translationY", 0, distance);
        mHideBottomBarAnimator.addListener(mHideBottomBarAnimatorListener);
        mHideBottomBarAnimator.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int position = 0;
        if (mDocumentPager != null && mDocumentPager.getLayoutManager() != null) {
            position = ((LinearLayoutManager) mDocumentPager.getLayoutManager()).findFirstVisibleItemPosition();
        }
        if (position < 0) {
            position = 0;
        }
        setPagerLayoutManager();
        mDocumentPager.scrollToPosition(position);
    }

}
