package com.pda.patrol.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ScreenUtil;
import com.pda.patrol.util.ToastUtil;

import java.util.List;

/***
 * rfid列表adapter
 */
public class ImageSelectAdapter extends BaseListItemAdapter<String> {
    private static final int MAX_COUNT = 3;
    // 是否可以添加图片
    private boolean canAdd = true;
    public ImageSelectAdapter(Context context, List<String> list, boolean canAdd) {
        super(context, list);

        this.canAdd = canAdd;
    }

    @Override
    public int getCount() {
        if(items.size() >= MAX_COUNT) {
            return MAX_COUNT;
        }

        return canAdd ? items.size() + 1 : items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.img_select_item, null);
            holder.iv = convertView.findViewById(R.id.iv);
            holder.addView = convertView.findViewById(R.id.add_ll);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int itemSize = ScreenUtil.dip2px(80);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(itemSize, itemSize);
        convertView.setLayoutParams(params);

        if(canAdd && position == getCount()-1 && items.size() < MAX_COUNT) {
            holder.addView.setVisibility(View.VISIBLE);
            holder.iv.setVisibility(View.GONE);
//            holder.addView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ToastUtil.toastLongMessage("11111");
//                    takePhoto();
//                }
//            });
        } else {
            holder.addView.setVisibility(View.GONE);
            holder.iv.setVisibility(View.VISIBLE);
            if(items.get(position).startsWith("http")) {
                GlideUtil.loadCornerImage(holder.iv, items.get(position), itemSize, null, 8);
            } else {
                GlideUtil.loadLocalImageWithCorner(holder.iv, items.get(position), itemSize, 8, null);
            }
        }

        return convertView;
    }

    private void setImageView(String path, ImageView view) {
        Bitmap src = BitmapFactory.decodeFile(path);
//        int size = ScreenUtil.dip2px(80);
//        Bitmap dst = Bitmap.createBitmap(src, 0, 0, size, size);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), src);
        roundedBitmapDrawable.setCornerRadius(ScreenUtil.dip2px(8)); //设置圆角半径为正方形边长的一半
        roundedBitmapDrawable.setAntiAlias(true);
        view.setImageDrawable(roundedBitmapDrawable);
    }

    class ViewHolder {
        public ImageView iv;
        public View addView;
    }
}
