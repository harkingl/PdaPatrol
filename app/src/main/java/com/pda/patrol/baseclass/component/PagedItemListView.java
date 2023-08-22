package com.pda.patrol.baseclass.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.pda.patrol.R;


public class PagedItemListView extends ListView implements OnScrollListener{
   
 	private volatile int currentPage = 0;
	private volatile int totalPageNumber = 1;
	private volatile int recordCount = 0;
 
	private boolean isLoading = false;
	private LoadMoreListener loadMoreListener;
	private OnListScrollListener mListOnScrollListener;
	private View footerView;
	private ScrollStateChangedListener mScrollStateChangedListener;

	public PagedItemListView(Context context) {
		super(context);
		init(context);
	}

	public void setScrollStateChangedListener(ScrollStateChangedListener mScrollStateChangedListener) {
		this.mScrollStateChangedListener = mScrollStateChangedListener;
	}

	public PagedItemListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PagedItemListView(Context context, AttributeSet attrs,
                             int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		footerView = LayoutInflater.from(context).inflate(
				R.layout.foot_loading_item, null);
 		addFooterView(footerView, null, false);
		super.setOnScrollListener(this);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mListOnScrollListener != null) {
			mListOnScrollListener.onListScrollListener(firstVisibleItem);
		}

		if (totalPageNumber <= currentPage && getFooterViewsCount() >= 1) {
			removeFooterView(view);
			return;
		} else if (totalPageNumber > currentPage
				&& getLastVisiblePosition() >= totalItemCount - 2
				&& !isLoading) {
			isLoading = true;
			if (loadMoreListener != null) {
				loadMoreListener.OnLoadMore(currentPage + 1);
			}
		}
	}

 
	public void onLoadDone() {
		isLoading = false;
		currentPage++;
		if (currentPage >= totalPageNumber && getFooterViewsCount() > 0) {
			removeFooterView(footerView);
		} else if (currentPage < totalPageNumber && getFooterViewsCount() == 0) {
			addFooterView(footerView, null, false);
		}
	}

	public void onLoadFailed() {
		isLoading = false;
		if (currentPage >= totalPageNumber) {
			removeFooterView(footerView);
		}
	}
 
	public LoadMoreListener getLoadMoreListener() {
		return loadMoreListener;
	}

	public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	public void setOnListScrollListener(OnListScrollListener mListOnScrollListener){
		this.mListOnScrollListener = mListOnScrollListener;
	}

	public interface LoadMoreListener {
		public void OnLoadMore(int currentPage);
	}

	public interface OnListScrollListener{
		public void onListScrollListener(int firstItem);
	}

	public int getTotalPageNumber() {
		return totalPageNumber;
	}

	public void setTotalPageNumber(int totalPageNumber) {
		this.totalPageNumber = totalPageNumber;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(null != mScrollStateChangedListener){
			mScrollStateChangedListener.onScrollStateChanged(view, scrollState);
		}
	}
	
	public void removeFooterView() {
        if (null != footerView){
			removeFooterView(footerView);
        }
    }

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public interface ScrollStateChangedListener{
		void onScrollStateChanged(AbsListView view, int scrollState);
	}
}
