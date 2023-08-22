package com.pda.patrol.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.TaskStateItem;

import java.util.List;

public class TaskStateAdapter extends BaseListItemAdapter<TaskStateItem> {
    private int selectIndex;
    public TaskStateAdapter(Context context, List<TaskStateItem> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.task_state_item, null);
            holder.nameTv = convertView.findViewById(R.id.task_state_item_name_tv);
            holder.sliderView = convertView.findViewById(R.id.task_state_item_slider);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskStateItem info = items.get(position);
        holder.nameTv.setText(info.name);
        if(position == selectIndex) {
            holder.nameTv.setTextColor(context.getColor(R.color.text_color_1));
            holder.sliderView.setVisibility(View.VISIBLE);
        } else {
            holder.nameTv.setTextColor(context.getColor(R.color.text_color_4));
            holder.sliderView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int index) {
        this.selectIndex = index;
    }

    class ViewHolder {
        public TextView nameTv;
        public View sliderView;
    }
}
