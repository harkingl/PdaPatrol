package com.pda.patrol.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollGridView;
import com.pda.patrol.baseclass.component.NoScrollListView;
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
    private View mTaskLayout;
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
    private TaskInfo mLatestTaskInfo;

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
        mTaskLayout = findViewById(R.id.detail_task_ll);
        mTaskCountTv = findViewById(R.id.detail_task_count_text);
        mTaskMoreIv = findViewById(R.id.detail_task_more_iv);
        mTaskTimeTv = findViewById(R.id.detail_task_time_tv);
        mInstallerTv = findViewById(R.id.detail_installer_tv);
        mInstallTimeTv = findViewById(R.id.detail_install_time_tv);
        mInstallImgsGv = findViewById(R.id.detail_install_imgs_gv);
        mRemarkTv = findViewById(R.id.detail_remark_tv);
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

    }

    private void setView(InspectionDetail detail) {
        if(detail == null) {
            return;
        }
//        GlideUtil.loadImage(mImgIv, detail..img, null);
        mIdTv.setText("巡检点" + detail.name);
        mAddressTv.setText("安装网点：" + detail.address);
        mInstallerTv.setText("安装人：" + detail.nickname);
        mInstallTimeTv.setText("安装时间：" + DateUtil.convertTimeFormat(detail.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
//        if(detail.fileList != null && detail.fileList.size() > 0) {
//            ImageSelectAdapter adapter = new ImageSelectAdapter(this, detail.fileList, false);
//            mInstallImgsGv.setAdapter(adapter);
//            mInstallImgsGv.setVisibility(View.VISIBLE);
//        } else {
//            mInstallImgsGv.setVisibility(View.GONE);
//        }

        initTaskView();
        if(detail.fileList != null && detail.fileList.size() > 0) {
            ImageSelectAdapter adapter = new ImageSelectAdapter(this, detail.fileList, false);
            mInstallImgsGv.setAdapter(adapter);
            mInstallImgsGv.setVisibility(View.VISIBLE);
        } else {
            mInstallImgsGv.setVisibility(View.GONE);
        }
        String remark = TextUtils.isEmpty(mDetail.remark) ? "备注信息：无" : "备注信息：" + mDetail.remark;
        mRemarkTv.setText(remark);

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

    private void initTaskView() {
        if(mDetail.tasksCount <= 0) {
            mTaskLayout.setVisibility(View.GONE);
        } else {
            mTaskCountTv.setText(getString(R.string.frid_count, mDetail.tasksCount));
            mTaskLayout.setVisibility(View.VISIBLE);

            getTaskList();
        }
    }

    private void getTaskList() {
        new GetTaskListRequest(this, -1, mId, false, 1, 20).schedule(false, new RequestListener<PagedListEntity<TaskInfo>>() {
            @Override
            public void onSuccess(PagedListEntity<TaskInfo> result) {
                mTaskList = result.getList();

                if(mTaskList != null && mTaskList.size() > 0) {
                    for(TaskInfo info : mTaskList) {
                        if(info.taskState == 0) {
                            String startTime = DateUtil.convertTimeFormat(info.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
                            String endTime = DateUtil.convertTimeFormat(info.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
                            mTaskTimeTv.setText(startTime + "-" + endTime);
                            mLatestTaskInfo = info;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mHistoryLayout) {
            gotoHistoryPage(2);
        } else if(view == mTaskMoreIv) {
            selectFridDialog(this);
        }
    }

    private void gotoHistoryPage(int index) {
        Intent i = new Intent(this, TaskListActivity.class);
        i.putExtra("inspect_id", mDetail.id);
        i.putExtra("curr_index", index);
        startActivity(i);
    }

    protected void selectFridDialog(final Context context) {
        if(mTaskList == null || mTaskList.size() == 0 || mLatestTaskInfo == null) {
            getTaskList();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.InputDialogStyle);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_inspect_rfid_list, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_inspect_rfid_list);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        ImageView closeIv = (ImageView) window.findViewById(R.id.dialog_common_view_close_btn);
        TextView timeTv = window.findViewById(R.id.dialog_plan_time_tv);
        String startTime = DateUtil.convertTimeFormat(mLatestTaskInfo.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
        String endTime = DateUtil.convertTimeFormat(mLatestTaskInfo.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
        timeTv.setText("计划巡检时间：" + startTime + "-" + endTime);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView dialogCount = (TextView) window.findViewById(R.id.dialog_count_tv);
        dialogCount.setText(getString(R.string.frid_count, mTaskList.size()));
        NoScrollListView lv = (NoScrollListView) window.findViewById(R.id.dialog_frid_lv);
        lv.setAdapter(new InspectRfidDialogAdapter(this, mTaskList, dialog));
    }
}
