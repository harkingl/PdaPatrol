package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.util.DateUtil;
import com.pda.patrol.util.GlideUtil;

import java.util.List;

/***
 * 已逾期详情
 */
public class TaskOverdueDetailActivity extends BaseTaskDetailActivity implements View.OnClickListener {
    private static final String TAG = TaskOverdueDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitlebarLayout;
    private TextView mStateTv;
    private TextView mTaskTimeTv;
    private ImageView mRfidImgIv;
    private TextView mRfidNoTv;
    private TextView mRfidTypeTv;
    private ImageView mMoreRfidIv;
    private TextView mInspectPointTv;
    private TextView mAddressTv;
    private TextView mInspectorTv;
    private TextView mTaskTaskTimeTv;
    private TextView mGotoInspectTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_todo_detail);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.detail_title_bar);
        mStateTv = findViewById(R.id.detail_task_state_tv);
        mTaskTimeTv = findViewById(R.id.detail_task_time_tv);
        mRfidImgIv = findViewById(R.id.detail_rfid_img_iv);
        mRfidNoTv = findViewById(R.id.detail_rfid_no_tv);
        mRfidTypeTv = findViewById(R.id.detail_rfid_type_tv);
        mMoreRfidIv = findViewById(R.id.detail_rfid_more_iv);
        mInspectPointTv = findViewById(R.id.detail_inspect_point_tv);
        mAddressTv = findViewById(R.id.detail_address_tv);
        mInspectorTv = findViewById(R.id.detail_inspector_tv);
        mTaskTaskTimeTv = findViewById(R.id.detail_task_task_time_tv);
        mGotoInspectTv = findViewById(R.id.detail_goto_inspect_tv);

        mMoreRfidIv.setOnClickListener(this);
        mGotoInspectTv.setOnClickListener(this);
    }

    private void initData() {
        if(mInfo == null) {
            return;
        }
        String startTime = DateUtil.convertTimeFormat(mInfo.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
        String endTime = DateUtil.convertTimeFormat(mInfo.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
        mTaskTimeTv.setText("任务时间："  + startTime + "- " + endTime);
        initRfidView(mInfo.rfidList);

        mInspectPointTv.setText("巡检点：巡检点" + mInfo.inspectionId);
        mAddressTv.setText("安装网点：" + mInfo.address);
        mInspectorTv.setText("计划巡检人：" + mInfo.nickname);
        mTaskTaskTimeTv.setText("任务时间："  + startTime + "- " + endTime);

       getTaskListById();
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("任务详情", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    private void initRfidView(List<RfidItem> list) {
//        if(list == null || list.size() == 0) {
//            return;
//        }
//        RfidItem item = list.get(0);
//
//        GlideUtil.loadImage(mRfidImgIv, item.img, null);
//        mRfidNoTv.setText("RFID 编号. " + item.no);
//        mRfidTypeTv.setText("设备类型：" + item.type);
        GlideUtil.loadImageWithPlaceHolder(mRfidImgIv, mInfo.rfidUrl, R.drawable.ic_rfid_img1);
        mRfidNoTv.setText("RFID 编号. " + mInfo.rfidNo);
        mRfidTypeTv.setText("设备类型：" + mInfo.rfidTypeName);
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mMoreRfidIv) {
            selectFridDialog(this);
        } else if(view == mGotoInspectTv) {
            gotoInspectPage();
        }
    }

    private void gotoInspectPage() {
        Intent i = new Intent(this, InspectScanActivity.class);
        i.putExtra("inspect_id", mInfo.inspectionId);
        i.putExtra("task_id", mInfo.id);
        startActivity(i);
    }
}
