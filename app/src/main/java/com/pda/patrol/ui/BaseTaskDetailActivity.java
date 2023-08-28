package com.pda.patrol.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.DateUtil;

import java.util.List;

public class BaseTaskDetailActivity extends BaseActivity {
    protected TaskInfo mInfo;
    protected List<TaskInfo> mTaskList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInfo = (TaskInfo) getIntent().getSerializableExtra("task_info");
    }

    /**
     * 获取当前检测点的任务列表
     */
    protected void getTaskListById() {
        new GetTaskListRequest(this, -1, mInfo.inspectionId, false, 1, 20).schedule(false, new RequestListener<PagedListEntity<TaskInfo>>() {
            @Override
            public void onSuccess(PagedListEntity<TaskInfo> result) {
                mTaskList = result.getList();
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    protected void selectFridDialog(final Context context) {
        if(mTaskList == null || mTaskList.size() == 0) {
            getTaskListById();
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
        String startTime = DateUtil.convertTimeFormat(mInfo.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
        String endTime = DateUtil.convertTimeFormat(mInfo.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
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
