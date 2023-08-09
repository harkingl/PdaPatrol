package com.pda.patrol.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.AddressInfo;

import java.util.List;

/***
 * 地址列表adapter
 */
public class AddressDialogAdapter extends BaseListItemAdapter<AddressInfo> {
    private AddressInfo mSelectedItem;
    public AddressDialogAdapter(Context context, List<AddressInfo> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.address_dialog_item, null);
            holder.addressTv = convertView.findViewById(R.id.dialog_item_address_tv);
            holder.cb = convertView.findViewById(R.id.dialog_item_cb);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AddressInfo item = items.get(position);
//        GlideUtil.loadImage(holder.iv, item.img, null);
        holder.addressTv.setText(item.address);
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

    public AddressInfo getSelectedItem() {
        return mSelectedItem;
    }

    class ViewHolder {
        public TextView addressTv;
        public CheckBox cb;
    }
}
