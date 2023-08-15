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
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.request.GetInspectionDetailRequest;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.ToastUtil;

import java.util.List;

public class PatrolDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PatrolDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitlebarLayout;
    private View mSelectItemLayout;
    private ImageView mImgIv;
    private TextView mIdTv;
    private TextView mAddressTv;
    private TextView mTypeTv;
    private View mTobeExecutedLayout;
    private TextView mGotoInspectTv;
    private TextView mCreatedTimeTv;
    private TextView mEndTimeTv;
    private TextView mInstallerTv;
    private TextView mInstallTimeTv;
    private TextView mRemarkTv;
    private View mHistoryLayout;
    private RfidItem mItem;
    private String mId;

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
        mTobeExecutedLayout = findViewById(R.id.detail_tobe_executed_ll);
        mGotoInspectTv = findViewById(R.id.detail_goto_inspect_tv);
        mCreatedTimeTv = findViewById(R.id.detail_create_time_tv);
        mEndTimeTv = findViewById(R.id.detail_end_time_tv);
        mInstallerTv = findViewById(R.id.detail_installer_tv);
        mInstallTimeTv = findViewById(R.id.detail_install_time_tv);
        mHistoryLayout = findViewById(R.id.detail_inspection_history_ll);

        mGotoInspectTv.setOnClickListener(this);
        mHistoryLayout.setOnClickListener(this);
    }

    private void initData() {
        mItem = (RfidItem) getIntent().getSerializableExtra("rfid_info");

//        if(mItem != null) {
//            GlideUtil.loadImage(mImgIv, mItem.img, null);
//            mIdTv.setText("巡检点" + mItem.id);
//            mAddressTv.setText("安装网点：" + mItem.address);
//            mTypeTv.setText("巡检点类型：" + mItem.type);
//        }

        mId = "1689471672656072704";
        new GetInspectionDetailRequest(this, mId).schedule(false, new RequestListener<InspectionDetail>() {
            @Override
            public void onSuccess(InspectionDetail result) {
                setView(result);
            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });

        new GetTaskListRequest(this, 0, mId).schedule(false, new RequestListener<List<TaskInfo>>() {
            @Override
            public void onSuccess(List<TaskInfo> result) {
                if(result != null && result.size() > 0) {
                    TaskInfo info = result.get(0);
                    mTobeExecutedLayout.setVisibility(View.VISIBLE);
                    mCreatedTimeTv.setText(info.crt);
                    mEndTimeTv.setText(info.endTime);
                }
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    private void setView(InspectionDetail detail) {
        if(detail == null) {
            return;
        }
//        GlideUtil.loadImage(mImgIv, detail..img, null);
        mIdTv.setText("巡检点" + mId);
        mAddressTv.setText("安装网点：" + detail.address);
        mInstallerTv.setText(detail.nickname);
        mInstallTimeTv.setText(detail.crt);
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

    private void getTaskList() {
        new GetTaskListRequest(this, -1, "").schedule(false, new RequestListener<List<TaskInfo>>() {
            @Override
            public void onSuccess(List<TaskInfo> result) {

            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }
}
