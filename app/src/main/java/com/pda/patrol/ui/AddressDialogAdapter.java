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
            holder.cb.setImageResource(R.drawable.checkbox_select);
        } else {
            holder.cb.setImageResource(R.drawable.checkbox_default);
        }
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectedItem == null || mSelectedItem != item) {
                    mSelectedItem = item;
                    notifyDataSetChanged();
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
        public ImageView cb;
    }
}
