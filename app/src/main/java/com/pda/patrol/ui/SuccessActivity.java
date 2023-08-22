package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.TitleBarLayout;

import java.util.ArrayList;

public class SuccessActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SuccessActivity.class.getSimpleName();
    private static final int FROM_INSTALL = 0;
    private static final int FROM_INSPECT = 1;

    private TitleBarLayout mTitlebarLayout;
    private TextView mTipTv;
    private TextView mContinueTv;
    private TextView mGoHomeTv;
    // 当前成功页面来源：0-安装 1-巡检（默认0）
    private int mFrom;
    private String mTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.success_title_bar);
        mTipTv = findViewById(R.id.success_tip_tv);
        mContinueTv = findViewById(R.id.success_continue_tv);
        mGoHomeTv = findViewById(R.id.success_home_tv);

        mContinueTv.setOnClickListener(this);
        mGoHomeTv.setOnClickListener(this);
    }

    private void initData() {
        mFrom = getIntent().getIntExtra("from", FROM_INSTALL);
        mTip = getIntent().getStringExtra("success_tip");

       if(!TextUtils.isEmpty(mTip)) {
           mTipTv.setText(mTip);
       }
       mContinueTv.setText(mFrom == FROM_INSPECT ? "继续巡检" : "继续安装");
    }

    private void configTitleBar() {
        String title = mFrom == FROM_INSPECT ? "执行巡检" : "安装巡检点";
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle(title, ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            if(mFrom == FROM_INSTALL) {
                continueNext();
            }
            finish();
        } else if(view == mContinueTv) {
            continueNext();
        } else if(view == mGoHomeTv) {
            gotoHomePage();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void continueNext() {
        Intent i = null;
        if(mFrom == FROM_INSPECT) {
            i = new Intent(this, TaskListActivity.class);
        } else {
            i = new Intent(this, FindRfidActivity.class);
        }
        startActivity(i);
        finish();
    }

    private void gotoHomePage() {

    }
}
