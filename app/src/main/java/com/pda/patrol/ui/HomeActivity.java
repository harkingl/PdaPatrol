package com.pda.patrol.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.HomeCountInfo;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.entity.TypeInfo;
import com.pda.patrol.entity.UserInfo;
import com.pda.patrol.request.GetHomeCountRequest;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private ImageView mHeadIv;
    private TextView mNameTv;
    private View mInstallView;
    private View mInspectPointView;
    private TextView mInspectCountTv;
    private View mInspectHistoryView;
    private TextView mHistoryCountTv;
    private TextView mAllTaskTv;
    private NoScrollListView mTaskListView;
    private List<TaskInfo> mList;
    private TaskListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        initData();
    }

    private void initView() {
        mHeadIv = findViewById(R.id.home_user_head_iv);
        mNameTv = findViewById(R.id.home_user_name_tv);
        mInstallView = findViewById(R.id.home_install_top_ll);
        mInspectPointView = findViewById(R.id.home_inspect_point_ll);
        mInspectCountTv = findViewById(R.id.home_inspect_count_tv);
        mInspectHistoryView = findViewById(R.id.home_inspect_history_ll);
        mHistoryCountTv = findViewById(R.id.home_history_count_tv);
        mAllTaskTv = findViewById(R.id.home_task_all_tv);
        mTaskListView = findViewById(R.id.home_task_lv);

        mNameTv.setOnClickListener(this);
        mInstallView.setOnClickListener(this);
        mInspectPointView.setOnClickListener(this);
        mInspectHistoryView.setOnClickListener(this);
        mAllTaskTv.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "---onNewIntent---");
    }

    private void initData() {
        UserInfo info = UserInfo.getInstance();
        if(TextUtils.isEmpty(info.getName())) {
            mNameTv.setText("你好");
        } else {
            mNameTv.setText(getString(R.string.home_user_name, info.getName()));
        }

        mList = new ArrayList<>();
        mAdapter = new TaskListAdapter(this, mList, 0);
        mTaskListView.setAdapter(mAdapter);

        getTaskList();
        getHomeCount();
    }

    private void getTaskList() {
        new GetTaskListRequest(this, 0, "", false, 1, 10).schedule(false, new RequestListener<PagedListEntity<TaskInfo>>() {
            @Override
            public void onSuccess(PagedListEntity<TaskInfo> result) {
                if(result.getList() != null) {
                    mList.addAll(result.getList());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    private void getHomeCount() {
        new GetHomeCountRequest(this).schedule(false, new RequestListener<HomeCountInfo>() {
            @Override
            public void onSuccess(HomeCountInfo result) {
                mInspectCountTv.setText(getString(R.string.home_install_count, result.inspectionCount));
                mHistoryCountTv.setText(getString(R.string.home_install_history_count, result.taskCount));
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == mAllTaskTv) {
            gotoTaskPage();
        } else if(view == mInstallView) {
            gotoInstallPage();
        } else if(view == mInspectHistoryView) {
            gotoHistoryPage();
        } else if(view == mNameTv) {
            showLogoutDialog(this);
        } else if(view == mInspectPointView) {
            gotoInspectPage();
        }
    }

    private void gotoTaskPage() {
        Intent i = new Intent(this, TaskListActivity.class);
        i.putExtra("curr_index", 0);
        startActivity(i);
    }

    private void gotoInstallPage() {
        Intent i = new Intent(this, FindRfidActivity.class);
        startActivity(i);
    }

    private void gotoInspectPage() {
        Intent i = new Intent(this, InspectPointActivity.class);
        startActivity(i);
    }

    private void gotoHistoryPage() {
        Intent i = new Intent(this, TaskListActivity.class);
        i.putExtra("curr_index", 2);
        startActivity(i);
    }

    private void showLogoutDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.InputDialogStyle);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_logout);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        ImageView closeIv = (ImageView) window.findViewById(R.id.dialog_common_view_close_btn);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView logoutTv = window.findViewById(R.id.dialog_logout_tv);
        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                dialog.dismiss();
                finish();
            }
        });

        TextView cancelTv = window.findViewById(R.id.dialog_cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
