package com.pda.patrol.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.baseclass.component.PagedItemListView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.AddressInfo;
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.request.GetAddressListRequest;
import com.pda.patrol.request.GetInspectListRequest;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * 巡检点列表页面
 */
public class InspectPointActivity extends BaseActivity implements View.OnClickListener {
    private static final int PAGE_SIZE = 20;

    private TitleBarLayout mTitlebarLayout;
    private TextView mLatestInstallTv;
    private View mSortSliderView;
    private TextView mScreenTv;
    private View mScreenSliderView;
    private PagedItemListView mListView;
    private List<InspectionDetail> mList;
    private InspectPointListAdapter mAdapter;
    private String mAddress;
    private int mPageNo = 1;
    private PopupWindow mPopupWindow;
    private AddressDialogAdapter mAddressAdapter;
    private List<AddressInfo> mAddressList;
    private String mCurrentAddress;
    // 当前排序类型 0：无排序 -1：正序 1：倒序 默认0：没排序
    private int mCurrSort = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inspect_point);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.inspect_point_title_bar);
        mLatestInstallTv = findViewById(R.id.inspect_point_latest_install_tv);
        mSortSliderView = findViewById(R.id.inspect_point_sort_slider);
        mScreenTv = findViewById(R.id.inspect_point_screen_tv);
        mScreenSliderView = findViewById(R.id.inspect_point_screen_slider);
        mListView = findViewById(R.id.inspect_point_list_lv);

        mLatestInstallTv.setOnClickListener(this);
        mScreenTv.setOnClickListener(this);
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("巡检点", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new InspectPointListAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        mListView.setLoadMoreListener(new PagedItemListView.LoadMoreListener() {
            @Override
            public void OnLoadMore(int currentPage) {
                mPageNo = currentPage;
                getInspectList();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i >=0 && i < mList.size()) {
                    gotoDetailPage(mList.get(i));
                }
            }
        });

        getInspectList();
        getAddressData();
    }

    private void reload() {
        mPageNo = 1;
        mList.clear();
        getInspectList();

        //清除排序状态
        mLatestInstallTv.setTypeface(Typeface.DEFAULT);
        mSortSliderView.setVisibility(View.INVISIBLE);
    }

    private void gotoDetailPage(InspectionDetail detail) {
        Intent i = new Intent(this, PatrolDetailActivity.class);
        i.putExtra("inspect_detail", detail);
        startActivity(i);
    }

    private void getInspectList() {
        new GetInspectListRequest(this, mPageNo, PAGE_SIZE, mAddress, false).schedule(false, new RequestListener<PagedListEntity<InspectionDetail>>() {
            @Override
            public void onSuccess(PagedListEntity<InspectionDetail> result) {
                mListView.onLoadDone();
//                mListView.setCurrentPage(result.getPageNo());
                mListView.setTotalPageNumber(result.getPageCount());
                if(result.getList() != null) {
                    mList.addAll(result.getList());
                    if(mPageNo != 1) {
                        mList.sort(mComparator);
                    }
                    if(mCurrSort == 1) {
                        Collections.reverse(mList);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Throwable e) {
                mListView.onLoadFailed();
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mLatestInstallTv) {
            if(mCurrSort != 0) {
                Collections.reverse(mList);
                mCurrSort *= -1;
            } else {
                mCurrSort = -1;
                mList.sort(mComparator);
            }
            mAdapter.notifyDataSetChanged();
            mLatestInstallTv.setTypeface(Typeface.DEFAULT_BOLD);
            mSortSliderView.setVisibility(View.VISIBLE);
//            mScreenTv.setTypeface(Typeface.DEFAULT);
//            mScreenSliderView.setVisibility(View.INVISIBLE);
        } else if(view == mScreenTv) {
            showPopupWindow();
            mScreenTv.setTypeface(Typeface.DEFAULT_BOLD);
            mScreenSliderView.setVisibility(View.VISIBLE);
//            mLatestInstallTv.setTypeface(Typeface.DEFAULT);
//            mSortSliderView.setVisibility(View.INVISIBLE);
        }
    }

    private void getAddressData() {
        new GetAddressListRequest(this).schedule(false, new RequestListener<List<AddressInfo>>() {
            @Override
            public void onSuccess(List<AddressInfo> result) {
                if(result != null && result.size() > 0) {
                    mAddressList = result;
                }
            }

            @Override
            public void onFailed(Throwable e) {
//                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    private void initPopWindow() {
        if(mPopupWindow != null) {
            return;
        }
        View view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.popwindow_filter_address, null);
        NoScrollListView lv = (NoScrollListView) view.findViewById(R.id.pop_address_lv);
        mAddressAdapter = new AddressDialogAdapter(this, mAddressList);
        lv.setAdapter(mAddressAdapter);
        TextView okTv = view.findViewById(R.id.pop_ok_tv);
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressInfo info = mAddressAdapter.getSelectedItem();
                if(info == null) {
                    ToastUtil.toastLongMessage("请选择安装点");
                    return;
                }
                if(!info.address.equals(mAddress)) {
                    mAddress = info.address;
                    reload();
                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.popupwindow);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mScreenTv.setTypeface(Typeface.DEFAULT);
                mScreenSliderView.setVisibility(View.INVISIBLE);
            }
        });
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//        mPopupWindow.update();
    }

    private void showPopupWindow() {
        if(mAddressList == null || mAddressList.size() == 0) {
            getAddressData();
            return;
        }
        initPopWindow();
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(mSortSliderView,0, 0);
        } else {
            mPopupWindow.dismiss();
        }
    }

    private Comparator<InspectionDetail> mComparator  = new Comparator<InspectionDetail>() {

        @Override
        public int compare(InspectionDetail inspectionDetail, InspectionDetail t1) {
            return inspectionDetail.crt.compareTo(t1.crt);
        }
    };
}
