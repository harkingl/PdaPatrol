package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.hc.OpenTask;
import com.pda.patrol.request.GetInspectionDetailRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;
import com.xlzn.hcpda.uhf.UHFReader;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;

import java.util.ArrayList;
import java.util.List;

public class InspectScanActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = InspectScanActivity.class.getSimpleName();
    private static final int DEFAULT_DOWN_COUNT = 60;

    private TitleBarLayout mTitlebarLayout;
    private TextView mTipTv;
    private View mRfidLayout;
    private ImageView mRfidImg;
    private TextView mRfidIdTv;
    private TextView mRfidTypeTv;
    private View mNextLayout;
    private TextView mNextTv;
    private ArrayList<RfidItem> mList;
    private List<String> epcs;
    private String mId;
    private InspectionDetail mDetail;

    private int mCount = DEFAULT_DOWN_COUNT;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_scan);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.inspect_scan_title_bar);
        mTipTv = findViewById(R.id.inspect_scan_tip_tv);
        mRfidLayout = findViewById(R.id.inspect_scan_rfid_ll);
        mRfidImg = findViewById(R.id.rfid_item_img_iv);
        mRfidIdTv = findViewById(R.id.rfid_item_id_tv);
        mRfidTypeTv = findViewById(R.id.rfid_item_type_tv);
        mNextLayout = findViewById(R.id.inspect_scan_next_ll);
        mNextTv = findViewById(R.id.inspect_scan_next_tv);

        mNextTv.setOnClickListener(this);
    }

    private void initData() {
        mId = getIntent().getStringExtra("inspect_id");

        epcs = new ArrayList<>();
        mList = new ArrayList<>();

        new GetInspectionDetailRequest(this, mId).schedule(false, new RequestListener<InspectionDetail>() {
            @Override
            public void onSuccess(InspectionDetail result) {
                setView(result);
//                mHandler.sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("执行巡检", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    private void setView(InspectionDetail detail) {
        if(detail == null) {
            return;
        }
        if(detail.rfidList != null) {
            mList.addAll(detail.rfidList);
        }
        mDetail = detail;
    }

    private void initRfidView(RfidItem item) {
        GlideUtil.loadImage(mRfidImg, item.img, null);
        mRfidIdTv.setText("RFID 编号. " + item.no);
        mRfidTypeTv.setText("设备类型：" + item.type);
        mRfidLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    private void open() {
        new OpenTask(this).execute();
    }

    private void close() {
        UHFReader.getInstance().disConnect();
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mNextTv) {
            doNext();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "onKeyDown:  " + keyCode);
        if (event.getRepeatCount() == 0 && keyCode == 293 || keyCode == 290 || keyCode == 287|| keyCode == 286) {
            read();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void read() {
        UHFReaderResult<String> readerResult = UHFReader.getInstance().read("00000000", 1, 2, 6, null);;

        if (readerResult.getResultCode() != UHFReaderResult.ResultCode.CODE_SUCCESS) {
//            ToastUtil.toastLongMessage("读取失败");
            return;
        }
        String data = readerResult.getData();
        LogUtil.d(TAG, "Result data：" + data);
        if(!TextUtils.isEmpty(data) && !epcs.contains(data)) {
            epcs.add(data);
            // 通过handler去请求
            Message msg = mHandler.obtainMessage(WHAT_REQUEST_DATA);
            msg.obj = data;
           mHandler.sendMessage(msg);
        }

    }

    private static final int WHAT_REQUEST_DATA = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WHAT_REQUEST_DATA:
                    getData((String)msg.obj);
                    break;
            }
        }
    };

    private void getData(String epc) {
        if(TextUtils.isEmpty(epc) || mList.size() == 0) {
            return;
        }
        for(RfidItem item : mList) {
            if(epc.equals(item.epc)) {
                initRfidView(item);
                mHandler.removeMessages(WHAT_REQUEST_DATA);
                scanFinish();
                mNextLayout.setVisibility(View.VISIBLE);
                break;
            }
        }
//        initRfidView(mList.get(0));
//        mHandler.removeMessages(WHAT_REQUEST_DATA);
//        scanFinish();
//        mNextLayout.setVisibility(View.VISIBLE);
    }

    private void scanFinish() {
        mTipTv.setText("巡检点扫描完成");
        close();
    }

    private void doNext() {
        Intent i = new Intent(this, ExecuteInspectActivity.class);
        i.putExtra("inspect_detail", mDetail);
        startActivity(i);
        finish();
    }
}
