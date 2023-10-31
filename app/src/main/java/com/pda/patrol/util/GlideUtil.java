package com.pda.patrol.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.pda.patrol.MainApplication;
import com.pda.patrol.baseclass.CornerTransform;

import java.io.File;

public class GlideUtil {

    public static void loadImage(ImageView imageView, String filePath, RequestListener listener) {
        Glide.with(MainApplication.getInstance())
                .load(filePath)
                .disallowHardwareConfig()
                .listener(listener)
                .into(imageView);
    }

    public static void loadCircleImage(ImageView imageView, String filePath, RequestListener listener) {
        RequestOptions options = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(MainApplication.getInstance())
                .load(filePath)
                .apply(options)
                .listener(listener)
                .into(imageView);
    }

    public static void loadImageWithPlaceHolder(ImageView imageView, String filePath, int defaultRes) {
        Glide.with(MainApplication.getInstance())
                .load(filePath)
                .apply(new RequestOptions().error(defaultRes))
                .into(imageView);
    }

    public static void clear(ImageView imageView) {
        Glide.with(MainApplication.getInstance()).clear(imageView);
    }

    public static void loadUserIcon(ImageView imageView, Object uri, int defaultResId, int radius) {
        Glide.with(MainApplication.getInstance())
                .load(uri)
                .placeholder(defaultResId)
                .apply(new RequestOptions().centerCrop().error(defaultResId))
                .into(imageView);
    }

    public static void loadLocalImageWithCorner(ImageView imageView, String filePath, int itemSize, int radius, RequestListener listener) {
//        CornerTransform transform = null;
//        if (radius > 0) {
//            transform = new CornerTransform(MainApplication.getInstance(), ScreenUtil.dip2px(radius));
//        }
//        Glide.with(MainApplication.getInstance())
//                .load("")
////                .load(new File(Environment
////                        .getExternalStorageDirectory() + "/patrol/", filePath))
////                .skipMemoryCache(true)
////                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .listener(listener)
//                .into(imageView);


        CornerTransform transform = null;
        if (radius > 0) {
            transform = new CornerTransform(MainApplication.getInstance(), ScreenUtil.dip2px(radius));
        }

        RequestOptions options = new RequestOptions()
                .centerCrop();
        if (transform != null) {
            options = options.transform(transform);
        }
        Glide.with(MainApplication.getInstance())
                .load(new File(filePath))
                .override(itemSize, itemSize)
                .apply(options)
                .listener(listener)
                .into(imageView);
    }

    public static void loadCornerImage(ImageView imageView, String filePath, int itemSize, RequestListener listener, int radius) {
        CornerTransform transform = null;
        if (radius > 0) {
            transform = new CornerTransform(MainApplication.getInstance(), ScreenUtil.dip2px(radius));
        }

        RequestOptions options = new RequestOptions()
                .centerCrop();
        if (transform != null) {
            options = options.transform(transform);
        }
        Glide.with(MainApplication.getInstance())
                .load(filePath)
                .override(itemSize, itemSize)
                .apply(options)
                .listener(listener)
                .into(imageView);
    }

    public static void loadGifImage(ImageView imageView, int resId) {
        Glide.with(MainApplication.getInstance())
                .asGif()
                .load(resId)
                .into(imageView);
    }
}
