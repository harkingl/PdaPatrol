package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.hc.OpenTask;
import com.pda.patrol.request.GetInspectionDetailRequest;
import com.pda.patrol.server.okhttp.RequestListener;
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
    private TextView mDownCountTv;
    private TextView mTipTv;
    private View mCountLayout;
    private TextView mCountTv;
    private NoScrollListView mRfidLv;
    private View mDataLayout;
    private View mEmptyLayout;
    private TextView mRescanTv;
    private View mNextLayout;
    private TextView mNextTv;
    private InspectRfidListAdapter mAdapter;
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
        mDownCountTv = findViewById(R.id.inspect_scan_down_count_tv);
        mTipTv = findViewById(R.id.inspect_scan_tip_tv);
        mCountLayout = findViewById(R.id.inspect_scan_count_ll);
        mCountTv = findViewById(R.id.inspect_scan_count_tv);
        mRfidLv = findViewById(R.id.inspect_scan_rfid_lv);
        mDataLayout = findViewById(R.id.inspect_scan_data_ll);
        mEmptyLayout = findViewById(R.id.inspect_scan_empty_ll);
        mRescanTv = findViewById(R.id.inspect_scan_rescan_tv);
        mNextLayout = findViewById(R.id.inspect_scan_next_ll);
        mNextTv = findViewById(R.id.inspect_scan_next_tv);

        mRescanTv.setOnClickListener(this);
        mNextTv.setOnClickListener(this);
    }

    private void initData() {
        mId = getIntent().getStringExtra("inspect_id");
        mDownCountTv.setText(mCount + "");

        epcs = new ArrayList<>();
        mList = new ArrayList<>();
        mAdapter = new InspectRfidListAdapter(this, mList);
        mRfidLv.setAdapter(mAdapter);

//        mRfidLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                onItemSelect(i);
//            }
//        });
//        mId = "1688841125726523392";
        new GetInspectionDetailRequest(this, mId).schedule(false, new RequestListener<InspectionDetail>() {
            @Override
            public void onSuccess(InspectionDetail result) {
                setView(result);
                mHandler.sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
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
            String[] epcs = new String[]{"E28011700000020E2511603A",
                    "E280689400005015AF781C6F",
                    "E28068940000401B3DA3E4A5",
                    "E28068940000401B3DA3E4A6"};
            for(int i = 0; i < detail.rfidList.size(); i++) {
                detail.rfidList.get(i).epc = epcs[i];
            }
            mCountTv.setText("共" + detail.rfidList.size() + "个设备");
            mList.addAll(detail.rfidList);
            mAdapter.notifyDataSetChanged();
        }
        mDetail = detail;
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
        } else if(view == mRescanTv) {
            reScan();
        } else if(view == mNextTv) {
            doNext();
        }
    }

    private void reScan() {
        mCount = DEFAULT_DOWN_COUNT;
        open();
        mDownCountTv.setText(mCount + "");
        mTipTv.setText("正在扫描附近的巡检点...");
        mDataLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        mHandler.sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
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

//        RfidItem item = new RfidItem();
//        item.img = R.drawable.ic_rfid_img1;
//        item.id = data;
//        item.type = "智能BOX";
//        mList.add(item);

//        mAdapter.notifyDataSetChanged();
    }

    private static final int WHAT_DOWN_COUNT = 1;
    private static final int WHAT_REQUEST_DATA = 2;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WHAT_DOWN_COUNT:
                    mCount--;
                    mDownCountTv.setText(mCount + "");
                    if(mCount <= 0) {
                        scanFinish();
                    } else {
                        sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
                    }
                   break;
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
        int count = 0;
        for(RfidItem item : mList) {
            if(epc.equals(item.epc)) {
                item.isScan = true;
            }
            if(item.isScan) {
                count++;
            }
        }
        mAdapter.notifyDataSetChanged();
        if(count == mList.size()) {
            mHandler.removeMessages(WHAT_REQUEST_DATA);
            mHandler.removeMessages(WHAT_DOWN_COUNT);
            scanFinish();
            mNextLayout.setVisibility(View.VISIBLE);
        }
    }

    private void scanFinish() {
        if(epcs.size() == 0) {
            mTipTv.setText("巡检点扫描完成");
            mEmptyLayout.setVisibility(View.VISIBLE);
            mDataLayout.setVisibility(View.GONE);
        } else {
            mTipTv.setText("RFID设备扫描完成");
            mEmptyLayout.setVisibility(View.GONE);
            mDataLayout.setVisibility(View.VISIBLE);
        }
        close();
    }

    private void doNext() {
        Intent i = new Intent(this, ExecuteInspectActivity.class);
        i.putExtra("inspect_detail", mDetail);
        startActivity(i);
        finish();
    }
}
