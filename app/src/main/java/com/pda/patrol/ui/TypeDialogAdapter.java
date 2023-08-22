package com.pda.patrol.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.TypeInfo;

import java.util.List;

/***
 * 地址列表adapter
 */
public class TypeDialogAdapter extends BaseListItemAdapter<TypeInfo> {
    private TypeInfo mSelectedItem;
    public TypeDialogAdapter(Context context, List<TypeInfo> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.type_dialog_item, null);
            holder.nameTv = convertView.findViewById(R.id.dialog_item_type_tv);
            holder.cb = convertView.findViewById(R.id.dialog_item_cb);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TypeInfo item = items.get(position);
//        GlideUtil.loadImage(holder.iv, item.img, null);
        holder.nameTv.setText(item.value);
        if(mSelectedItem != null && item == mSelectedItem) {
            holder.cb.setSelected(true);
        } else {
            holder.cb.setSelected(false);
        }
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mSelectedItem = item;
                }
            }
        });

        return convertView;
    }

    public TypeInfo getSelectedItem() {
        return mSelectedItem;
    }

    class ViewHolder {
        public TextView nameTv;
        public CheckBox cb;
    }
}
