package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollGridView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.DateUtil;
import com.pda.patrol.util.ScreenUtil;

import java.util.List;

public class PatrolDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PatrolDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitlebarLayout;
    private View mSelectItemLayout;
    private ImageView mImgIv;
    private TextView mIdTv;
    private TextView mAddressTv;
    private TextView mTypeTv;
    private TextView mRfidCountTv;
    private NoScrollGridView mRfidGv;
    private TextView mTaskCountTv;
    private ImageView mTaskMoreIv;
    private TextView mTaskTimeTv;
    private TextView mInstallerTv;
    private TextView mInstallTimeTv;
    private NoScrollGridView mInstallImgsGv;
    private TextView mRemarkTv;
    private View mHistoryLayout;
    private String mId;
    private List<TaskInfo> mTaskList;
    private InspectionDetail mDetail;

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
        mRfidCountTv = findViewById(R.id.detail_rfid_count_text);
        mRfidGv = findViewById(R.id.detail_rfid_gv);
        mTaskCountTv = findViewById(R.id.detail_task_count_text);
        mTaskMoreIv = findViewById(R.id.detail_task_more_iv);
        mTaskTimeTv = findViewById(R.id.detail_task_time_tv);
        mInstallerTv = findViewById(R.id.detail_installer_tv);
        mInstallTimeTv = findViewById(R.id.detail_install_time_tv);
        mInstallImgsGv = findViewById(R.id.detail_install_imgs_gv);
        mHistoryLayout = findViewById(R.id.detail_inspection_history_ll);

        mTaskMoreIv.setOnClickListener(this);
        mHistoryLayout.setOnClickListener(this);
    }

    private void initData() {
        mDetail = (InspectionDetail) getIntent().getSerializableExtra("inspect_detail");

//        if(mItem != null) {
//            GlideUtil.loadImage(mImgIv, mItem.img, null);
//            mIdTv.setText("巡检点" + mItem.id);
//            mAddressTv.setText("安装网点：" + mItem.address);
//            mTypeTv.setText("巡检点类型：" + mItem.type);
//        }

        setView(mDetail);
//        mId = "1688841125726523392";
//        new GetInspectionDetailRequest(this, mId).schedule(false, new RequestListener<InspectionDetail>() {
//            @Override
//            public void onSuccess(InspectionDetail result) {
//                setView(result);
//            }
//
//            @Override
//            public void onFailed(Throwable e) {
//                ToastUtil.toastLongMessage(e.getMessage());
//            }
//        });

        new GetTaskListRequest(this, 0, mId, false, 1, 20).schedule(false, new RequestListener<PagedListEntity<TaskInfo>>() {
            @Override
            public void onSuccess(PagedListEntity<TaskInfo> result) {
                initTastView(result.getList());
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
        mIdTv.setText("巡检点" + detail.name);
        mAddressTv.setText("安装网点：" + detail.address);
        mInstallerTv.setText(detail.nickname);
        mInstallTimeTv.setText(DateUtil.convertTimeFormat(detail.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
//        if(detail.fileList != null && detail.fileList.size() > 0) {
//            ImageSelectAdapter adapter = new ImageSelectAdapter(this, detail.fileList, false);
//            mInstallImgsGv.setAdapter(adapter);
//            mInstallImgsGv.setVisibility(View.VISIBLE);
//        } else {
//            mInstallImgsGv.setVisibility(View.GONE);
//        }

        if(detail.fileList != null && detail.fileList.size() > 0) {
            ImageSelectAdapter adapter = new ImageSelectAdapter(this, detail.fileList, false);
            mInstallImgsGv.setAdapter(adapter);
            mInstallImgsGv.setVisibility(View.VISIBLE);
        }

        initRfidView(detail.rfidList);
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("巡检点详情", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    private void initRfidView(List<RfidItem> list) {
        if(list == null || list.size() == 0) {
            return;
        }
        mRfidCountTv.setText(getString(R.string.frid_count, list.size()));

        // item之间的间隔
        int padding = ScreenUtil.dip2px(15);
        int itemWidth = ScreenUtil.dip2px(64);

        int gridviewWidth = itemWidth*list.size() + padding*(list.size()-1);
        LinearLayout.LayoutParams sceneParams = new LinearLayout.LayoutParams(
                gridviewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRfidGv.setLayoutParams(sceneParams);
        mRfidGv.setNumColumns(list.size());
        mRfidGv.setColumnWidth(itemWidth);
        mRfidGv.setHorizontalSpacing(padding);
        mRfidGv.setVerticalSpacing(padding);
        mRfidGv.setStretchMode(GridView.NO_STRETCH);
        DetailRfidAdapter adapter = new DetailRfidAdapter(this, list);
        mRfidGv.setAdapter(adapter);
    }

    private void initTastView(List<TaskInfo> list) {
        if(list != null && list.size() > 0) {
            mTaskCountTv.setText(getString(R.string.frid_count, list.size()));
            TaskInfo info = list.get(0);
            String startTime = DateUtil.convertTimeFormat(info.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
            String endTime = DateUtil.convertTimeFormat(info.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
            mTaskTimeTv.setText(startTime + "- " + endTime);
            mTaskList = list;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mHistoryLayout) {
            gotoHistoryPage(2);
        } else if(view == mTaskMoreIv) {
//            gotoHistoryPage(0);
        }
    }

    private void gotoHistoryPage(int index) {
        Intent i = new Intent(this, TaskListActivity.class);
        i.putExtra("inspect_id", mDetail.id);
        i.putExtra("curr_index", index);
        startActivity(i);
    }
}
