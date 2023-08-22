package com.pda.patrol.ui;

import android.content.Intent;
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
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.request.GetInspectionDetailRequest;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.DateUtil;
import com.pda.patrol.util.ToastUtil;

import java.util.ArrayList;
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
    private NoScrollGridView mInstallImgsGv;
    private TextView mRemarkTv;
    private View mHistoryLayout;
    private RfidItem mItem;
    private String mId;
    private ArrayList<String> mImgList;

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
        mInstallImgsGv = findViewById(R.id.detail_install_imgs_gv);
        mHistoryLayout = findViewById(R.id.detail_inspection_history_ll);

        mGotoInspectTv.setOnClickListener(this);
        mHistoryLayout.setOnClickListener(this);
    }

    private void initData() {
        mItem = (RfidItem) getIntent().getSerializableExtra("rfid_info");
        mImgList = (ArrayList<String>) getIntent().getSerializableExtra("imgs");

//        if(mItem != null) {
//            GlideUtil.loadImage(mImgIv, mItem.img, null);
//            mIdTv.setText("巡检点" + mItem.id);
//            mAddressTv.setText("安装网点：" + mItem.address);
//            mTypeTv.setText("巡检点类型：" + mItem.type);
//        }

        mId = "1688841125726523392";
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

        new GetTaskListRequest(this, 0, mId, false, 1, 1).schedule(false, new RequestListener<PagedListEntity<TaskInfo>>() {
            @Override
            public void onSuccess(PagedListEntity<TaskInfo> result) {
                if(result.getList() != null && result.getList().size() > 0) {
                    TaskInfo info = result.getList().get(0);
                    mTobeExecutedLayout.setVisibility(View.VISIBLE);
                    mCreatedTimeTv.setText(DateUtil.convertTimeFormat(info.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
                    mEndTimeTv.setText(DateUtil.convertTimeFormat(info.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
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
        mInstallTimeTv.setText(DateUtil.convertTimeFormat(detail.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
//        if(detail.fileList != null && detail.fileList.size() > 0) {
//            ImageSelectAdapter adapter = new ImageSelectAdapter(this, detail.fileList, false);
//            mInstallImgsGv.setAdapter(adapter);
//            mInstallImgsGv.setVisibility(View.VISIBLE);
//        } else {
//            mInstallImgsGv.setVisibility(View.GONE);
//        }

        ImageSelectAdapter adapter = new ImageSelectAdapter(this, mImgList, false);
        mInstallImgsGv.setAdapter(adapter);
        mInstallImgsGv.setVisibility(View.VISIBLE);
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
        } else if(view == mHistoryLayout) {
            gotoHistoryPage(2);
        } else if(view == mGotoInspectTv) {
            gotoHistoryPage(0);
        }
    }

    private void gotoHistoryPage(int index) {
        Intent i = new Intent(this, TaskListActivity.class);
        i.putExtra("curr_index", index);
        startActivity(i);
    }
}
