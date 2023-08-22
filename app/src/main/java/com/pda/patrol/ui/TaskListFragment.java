package com.pda.patrol.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseFragment;
import com.pda.patrol.baseclass.component.PagedItemListView;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.request.GetTaskListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends BaseFragment {
    private static final int PAGE_SIZE = 5;
    private PagedItemListView mListView;

    private TaskListAdapter mAdapter;
    private List<TaskInfo> mList;
    private int mTaskState = -1;
    private int mPageNo = 1;

    public TaskListFragment(int taskState) {
        this.mTaskState = taskState;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initData();
    }

    private void initView(View view) {
        mListView = view.findViewById(R.id.task_list_lv);
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new TaskListAdapter(getActivity(), mList, mTaskState);
        mListView.setAdapter(mAdapter);
        mListView.setLoadMoreListener(new PagedItemListView.LoadMoreListener() {
            @Override
            public void OnLoadMore(int currentPage) {
                mPageNo = currentPage;
                getTaskList();
            }
        });

        getTaskList();
    }

    private void getTaskList() {
        new GetTaskListRequest(getActivity(), mTaskState, "", false, mPageNo, PAGE_SIZE).schedule(false, new RequestListener<PagedListEntity<TaskInfo>>() {
            @Override
            public void onSuccess(PagedListEntity<TaskInfo> result) {
                mListView.onLoadDone();
//                mListView.setCurrentPage(result.getPageNo());
                mListView.setTotalPageNumber(result.getPageCount());
                if(result.getList() != null) {
                    mList.addAll(result.getList());
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
}
