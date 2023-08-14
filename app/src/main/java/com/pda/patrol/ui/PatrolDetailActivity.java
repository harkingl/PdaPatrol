package com.pda.patrol.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.util.GlideUtil;

public class PatrolDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PatrolDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitlebarLayout;
    private View mSelectItemLayout;
    private ImageView mImgIv;
    private TextView mIdTv;
    private TextView mAddressTv;
    private TextView mTypeTv;
    private RfidItem mItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_detail);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.detail_title_bar);
        mIdTv = findViewById(R.id.detail_id_tv);
        mImgIv = findViewById(R.id.detail_img_iv);
        mAddressTv = findViewById(R.id.detail_address_tv);
        mTypeTv = findViewById(R.id.detail_type_tv);
    }

    private void initData() {
        mItem = (RfidItem) getIntent().getSerializableExtra("rfid_info");

        if(mItem != null) {
            GlideUtil.loadImage(mImgIv, mItem.img, null);
            mIdTv.setText("巡检点" + mItem.id);
            mAddressTv.setText("安装网点：" + mItem.address);
            mTypeTv.setText("巡检点类型：" + mItem.type);
        }
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("巡检点详情", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        }
    }
}
