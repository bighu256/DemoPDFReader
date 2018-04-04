package com.chaoxing.pdfreader;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chaoxing.pdfreader.util.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private MainViewModel mViewModel;
    private TextView mTvPath;
    private RecyclerView mFileList;
    private FileAdapter mAdapter;
    private File mRootDirectory;
    private File mCurrentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_folder_white_36dp);
        toolbar.setTitle("Files");
        setSupportActionBar(toolbar);


        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        initView();
        initDirectory();
        attemptOpenDirectory();
    }

    private void initView() {
        mTvPath = findViewById(R.id.tv_path);
        mFileList = findViewById(R.id.file_list);
        mFileList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new FileAdapter();
        mFileList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File file) {
                if (file.isDirectory()) {
                    openDirectory(file);
                } else {
                    openFile(file);
                }
            }
        });
    }

    private void initDirectory() {
        mViewModel.getFiles().observe(this, files -> {
            mAdapter.setFiles(files);
            mAdapter.notifyDataSetChanged();
        });
    }

    private void attemptOpenDirectory() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        openRootDirectory();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        attemptOpenDirectory();
                    } else {
                        Toast.makeText(MainActivity.this, "获取\"存储空间\"权限失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openRootDirectory() {
        mRootDirectory = Environment.getExternalStorageDirectory();
        openDirectory(mRootDirectory);
    }

    private void openDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            mCurrentDirectory = directory;
            if (mCurrentDirectory.equals(mRootDirectory)) {
                mTvPath.setText("/");
            } else {
                mTvPath.setText(mCurrentDirectory.getAbsolutePath().substring(mRootDirectory.getAbsolutePath().length()));
            }
            mViewModel.openDirectory(directory);
        }
    }

    private void openFile(File file) {
        if (Utils.getFileExtension(file).equalsIgnoreCase("pdf")) {
            Intent intent = new Intent(this, PDFDocumentActivity.class);
            intent.setData(Uri.fromFile(file));
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        File parentFile = null;
        if (mCurrentDirectory != null) {
            parentFile = mCurrentDirectory.getParentFile();
        }

        if (parentFile == null || parentFile.getAbsolutePath().length() < mRootDirectory.getAbsolutePath().length()) {
            super.onBackPressed();
        } else {
            openDirectory(parentFile);
        }
    }

}
