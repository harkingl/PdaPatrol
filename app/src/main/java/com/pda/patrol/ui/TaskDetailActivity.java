package com.pda.patrol.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollGridView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.util.DateUtil;

public class TaskDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = TaskDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitlebarLayout;
    private TextView mStateTv;
    private TextView mInspectTimeTv;
    private TextView mInspectNormalTv;
    private ImageView mImgIv;
    private TextView mIdTv;
    private TextView mAddressTv;
    private TextView mImgCountTv;
    private NoScrollGridView mImgsGv;
    private TextView mCreatedTimeTv;
    private TextView mEndTimeTv;
    private TextView mInspectorTv;
    private TextView mTaskInspectTimeTv;
    private TextView mInspectResultTv;
    private TextView mAbnormalTypeTv;
    private TextView mAbnormalInfoTv;
    private TaskInfo mInfo;
    private String mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.detail_title_bar);
        mStateTv = findViewById(R.id.detail_task_state_tv);
        mInspectTimeTv = findViewById(R.id.detail_inspect_time_tv);
        mInspectNormalTv = findViewById(R.id.detail_inspect_normal_tv);
        mImgIv = findViewById(R.id.detail_img_iv);
        mIdTv = findViewById(R.id.detail_id_tv);
        mAddressTv = findViewById(R.id.detail_address_tv);
        mImgCountTv = findViewById(R.id.detail_img_count_tv);
        mImgsGv = findViewById(R.id.detail_imgs_gv);
        mCreatedTimeTv = findViewById(R.id.detail_create_time_tv);
        mEndTimeTv = findViewById(R.id.detail_end_time_tv);
        mInspectorTv = findViewById(R.id.detail_inspector_tv);
        mTaskInspectTimeTv = findViewById(R.id.detail_task_inspect_time_tv);
        mInspectResultTv = findViewById(R.id.detail_inspect_result_tv);
        mAbnormalTypeTv = findViewById(R.id.detail_abnormal_type_tv);
        mAbnormalInfoTv = findViewById(R.id.detail_abnormal_info_tv);
    }

    private void initData() {
        mInfo = (TaskInfo) getIntent().getSerializableExtra("task_info");
        if(mInfo == null) {
            return;
        }
        mStateTv.setText("任务" + mInfo.taskStateName);
        mInspectNormalTv.setText(mInfo.isNormal == 1 ? "巡检正常" : "巡检异常");
        mInspectTimeTv.setText("巡检时间：" + DateUtil.convertTimeFormat(mInfo.dealTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
        mIdTv.setText("巡检点" + mInfo.inspectionId);
        mAddressTv.setText("安装网点：" + mInfo.address);
        if(mInfo.fileList != null && mInfo.fileList.size() > 0) {
            mImgCountTv.setText(getString(R.string.frid_count, mInfo.fileList.size()));
            mImgCountTv.setVisibility(View.VISIBLE);

            ImageSelectAdapter adapter = new ImageSelectAdapter(this, mInfo.fileList, false);
            mImgsGv.setAdapter(adapter);
            mImgsGv.setVisibility(View.VISIBLE);
        } else {
            mImgCountTv.setVisibility(View.GONE);
        }

        mCreatedTimeTv.setText("创建时间：" + DateUtil.convertTimeFormat(mInfo.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
        mEndTimeTv.setText("截止时间：" + DateUtil.convertTimeFormat(mInfo.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));

        mInspectorTv.setText("巡检人：" + mInfo.nickname);
        mTaskInspectTimeTv.setText("巡检时间：" + DateUtil.convertTimeFormat(mInfo.dealTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
        String result = "";
        if(mInfo.isNormal == 1) {
            result = "正常";
        } else if(mInfo.isNormal == 2) {
            result = "异常";
        }

        mInspectResultTv.setText("巡检结果：" + result);
        if(mInfo.isNormal == 2) {
            mAbnormalTypeTv.setText("异常类型：" + mInfo.abnormalResult);
            mAbnormalInfoTv.setText("异常信息：" + mInfo.abnormalInfo);
            mAbnormalTypeTv.setVisibility(View.VISIBLE);
            mAbnormalInfoTv.setVisibility(View.VISIBLE);
        }
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("任务详情", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        }
    }
}
