package com.pda.patrol.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.BaseFragment;
import com.pda.patrol.baseclass.component.NoScrollGridView;
import com.pda.patrol.entity.TaskStateItem;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends BaseActivity {
    private NoScrollGridView mStateGv;
    private ViewPager2 mViewPager;
    private List<TaskStateItem> mStateList;
    private TaskStateAdapter mStateAdapter;
    private List<BaseFragment> fragments;
    private FragmentAdapter fragmentAdapter;
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_list);

        initView();
        initData();
    }

    private void initView() {
        mStateGv = findViewById(R.id.task_state_gv);
        mViewPager = findViewById(R.id.task_view_pager);
    }

    private void initData() {
        mCurrentIndex = getIntent().getIntExtra("curr_index", 0);
        mStateList = new ArrayList<>();
        mStateList.add(new TaskStateItem(0, "待执行"));
        mStateList.add(new TaskStateItem(-1, "全部"));
        mStateList.add(new TaskStateItem(1, "已完成"));
        mStateList.add(new TaskStateItem(2, "已逾期"));
        mStateAdapter = new TaskStateAdapter(this, mStateList);
        mStateAdapter.setSelectIndex(mCurrentIndex);
        mStateGv.setAdapter(mStateAdapter);
        mStateGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStateAdapter.setSelectIndex(i);
                mStateAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(i, false);
            }
        });

        fragments = new ArrayList<>();
        for(TaskStateItem item : mStateList) {
            fragments.add(new TaskListFragment(item.taskState));
        }
        fragmentAdapter = new FragmentAdapter(this);
        fragmentAdapter.setFragmentList(fragments);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setCurrentItem(mCurrentIndex, false);
    }
}
