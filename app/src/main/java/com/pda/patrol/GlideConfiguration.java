package com.pda.patrol;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;

@GlideModule
public class GlideConfiguration extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //自定义缓存目录，磁盘缓存给150M 另外一种设置缓存方式
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "GlideImgCache", 150 * 1024 * 1024));
        //配置图片缓存格式 默认格式为8888
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888));
    }

    /**
     * 禁止解析Manifest文件
     * 主要针对V3升级到v4的用户，可以提升初始化速度，避免一些潜在错误
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
