package com.pda.patrol.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.llw.goodble.ble.BleCore;
import com.llw.goodble.ble.scan.BleScanCallback;
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
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.ScreenUtil;
import com.pda.patrol.util.ToastUtil;

import java.util.List;
import java.util.Locale;

public class PatrolDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PatrolDetailActivity.class.getSimpleName();
    private static final int REQ_PERMISSION_BLUETOOTH = 111;
    private static final int DOWN_COUNT = 20;

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
    private View mSearchLayout;
    private String mId;
    private List<TaskInfo> mTaskList;
    private InspectionDetail mDetail;
    private TaskInfo mLatestTaskInfo;
    private BleCore mBleCore;
    private AlertDialog mDialog;
    private View mDialogLoadingLayout;
    private ImageView mDialogLoadingIv;
    private View mDialogEmptyLayout;
    private TextView mDialogResearchTv;
    private View mDialogDataLayout;
    private TextView mDialogDistanceTv;
    private ImageView mDialogRfidImgIv;
    private TextView mDialogRfidIdTv;
    private TextView mDialogRfidAddressTv;
    private RfidItem mItem;
    private boolean mIsFind = false;

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
        mSearchLayout = findViewById(R.id.detail_search_device_ll);

        mTaskMoreIv.setOnClickListener(this);
        mHistoryLayout.setOnClickListener(this);
        mSearchLayout.setOnClickListener(this);
    }

    private void initData() {
        mDetail = (InspectionDetail) getIntent().getSerializableExtra("inspect_detail");

        if(mItem != null) {
            GlideUtil.loadImage(mImgIv, mItem.img, null);
            mIdTv.setText("巡检点" + mItem.id);
            mAddressTv.setText("安装网点：" + mItem.address);
            mTypeTv.setText("巡检点类型：" + mItem.type);
        }

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
//                LogUtil.e(TAG, e.getMessage());
//                ToastUtil.toastLongMessage(e.getMessage());
//            }
//        });

        mBleCore = BleCore.Companion.getInstance(this);
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

        initTaskView(detail);
        if(detail.fileList != null && detail.fileList.size() > 0) {
            ImageSelectAdapter adapter = new ImageSelectAdapter(this, detail.fileList, false);
            mInstallImgsGv.setAdapter(adapter);
            mInstallImgsGv.setVisibility(View.VISIBLE);
        } else {
            mInstallImgsGv.setVisibility(View.GONE);
        }
        String remark = TextUtils.isEmpty(detail.remark) ? "备注信息：无" : "备注信息：" + detail.remark;
        mRemarkTv.setText(remark);

        initRfidView(detail.rfidList);
        initSearchView(detail.rfidList);
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

    private void initTaskView(InspectionDetail detail) {
        if(detail.tasksCount <= 0) {
            mTaskLayout.setVisibility(View.GONE);
        } else {
            mTaskCountTv.setText(getString(R.string.frid_count, detail.tasksCount));
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
        } else if(view == mSearchLayout) {
            searchDevice();
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

    private void searchDevice() {
        if(!checkPermission()) {
            return;
        }
        startSearch();
    }

    private void startSearch() {
        if(!checkBlueToothOpen()) {
            ToastUtil.toastLongMessage("请打开蓝牙");
            return;
        }
        if(!checkGpsOpen()) {
            ToastUtil.toastLongMessage("请打开GPS");
            return;
        }

        showDialog();
        mBleCore.startScan();
    }

    private void showDialog() {
        if(mItem == null) {
            return;
        }
        // 每次重新创建
        createFridDialog(this);


        mCount = DOWN_COUNT;
        mHandler.sendEmptyMessageDelayed(WHAT_COUNT, 1000);
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        String[] array = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            array = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN};
        } else {
            array = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        }

        ActivityCompat.requestPermissions(this,
                array, REQ_PERMISSION_BLUETOOTH);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_PERMISSION_BLUETOOTH) {
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            startSearch();
        }
    }

    private boolean checkGpsOpen() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean checkBlueToothOpen() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        return adapter.isEnabled();
    }

    private void stopScan() {
        if(mBleCore.isScanning()) {
            mBleCore.stopScan();
        }
        mHandler.removeMessages(WHAT_COUNT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    private static final int WHAT_COUNT = 1;
    private int mCount = DOWN_COUNT;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case WHAT_COUNT:
                    mCount--;
                    if(mCount <= 0 && !mIsFind) {
                        stopScan();
                        mDialogLoadingLayout.setVisibility(View.GONE);
                        mDialogDataLayout.setVisibility(View.GONE);
                        mDialogEmptyLayout.setVisibility(View.VISIBLE);
                        removeMessages(WHAT_COUNT);
                        return;
                    }
                    if(!mIsFind) {
                        mHandler.sendEmptyMessageDelayed(WHAT_COUNT, 1000);
                    }
                    break;
            }
        }
    };

    private void initSearchView(List<RfidItem> list) {
        if(list == null || list.size() == 0) {
            return;
        }
        for(RfidItem item : list) {
            if(!TextUtils.isEmpty(item.blueToothMac)) {
                mItem = item;
                break;
            }
        }

        if(mItem == null) {
            mSearchLayout.setVisibility(View.GONE);
            return;
        }
        mSearchLayout.setVisibility(View.VISIBLE);

        mBleCore.setPhyScanCallback(new BleScanCallback() {
            @Override
            public void onScanFailed(@NonNull String failed) {
//                super.onScanFailed(failed);
                stopScan();
            }

            @Override
            public void onBatchScanResults(@NonNull List<ScanResult> results) {
//                BleScanCallback.super.onBatchScanResults(results);
            }

            @Override
            public void onScanResult(@NonNull ScanResult result) {
                String address = result.getDevice().getAddress();
                if(address.equals(mItem.blueToothMac)) {
                    mIsFind = true;
                    mDialogDistanceTv.setText(String.format(Locale.getDefault(), "%.1f m", getDistByRSSI(result.getRssi())));
                    mDialogLoadingLayout.setVisibility(View.GONE);
                    mDialogEmptyLayout.setVisibility(View.GONE);
                    mDialogDataLayout.setVisibility(View.VISIBLE);
//                    stopScan();
                    mHandler.removeMessages(WHAT_COUNT);
                }
            }
        });
    }

    private void createFridDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.InputDialogStyle);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_search_device, null);
        mDialog = builder.create();
        mDialog.setView(layout);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.dialog_search_device);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        ImageView closeIv = (ImageView) window.findViewById(R.id.dialog_common_view_close_btn);
        mDialogLoadingLayout = window.findViewById(R.id.dialog_loading_ll);
        mDialogLoadingIv = window.findViewById(R.id.dialog_loading_iv);
        mDialogEmptyLayout = window.findViewById(R.id.dialog_empty_ll);
        mDialogResearchTv =window.findViewById(R.id.dialog_research_tv);
        mDialogDataLayout = window.findViewById(R.id.dialog_search_data_ll);
        mDialogDistanceTv = window.findViewById(R.id.dialog_distance_tv);
        mDialogRfidImgIv = window.findViewById(R.id.dialog_rfid_item_img_iv);
        mDialogRfidIdTv = window.findViewById(R.id.dialog_rfid_item_id_tv);
        mDialogRfidAddressTv = window.findViewById(R.id.dialog_rfid_item_address_tv);

        mDialogEmptyLayout.setVisibility(View.GONE);
        mDialogDataLayout.setVisibility(View.GONE);
        mDialogLoadingLayout.setVisibility(View.VISIBLE);

        GlideUtil.loadGifImage(mDialogLoadingIv, R.drawable.ic_circle);
        GlideUtil.loadImage(mDialogRfidImgIv, mItem.img, null);
        mDialogRfidIdTv.setText("设备：" + mItem.no);
        mDialogRfidAddressTv.setText(mItem.blueToothMac);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialogResearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogEmptyLayout.setVisibility(View.GONE);
                mDialogLoadingLayout.setVisibility(View.VISIBLE);
//                startSearch();
                mBleCore.startScan();
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopScan();
                mDialog = null;
            }
        });
    }

    private Double getDistByRSSI(int rssi) {
        int iRSSi = Math.abs(rssi);
        double power = (iRSSi - 50) / (10 * 2.0);
        return Math.pow(10.0, power);
    }
}
