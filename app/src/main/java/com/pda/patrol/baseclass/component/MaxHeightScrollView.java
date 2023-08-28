package com.pda.patrol.baseclass.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.pda.patrol.R;
import com.pda.patrol.util.ScreenUtil;

/**
 * 支持最大高度的layout
 */
public class MaxHeightScrollView extends ScrollView {

    private float mMaxHeight;

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConstraintHeightView);
        try {
            mMaxHeight = ta.getDimension(R.styleable.ConstraintHeightView_maxHeight, 0);
            init();
        } finally {
            ta.recycle();
        }
    }

    private void init() {
        if(mMaxHeight <= 0) {
            // 默认最大高度为屏幕高度的0.6
            mMaxHeight = (int) (ScreenUtil.getScreenHeight(this.getContext())*0.6);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        //限制高度小于lv高度,设置为限制高度
        if (mMaxHeight <= specSize && mMaxHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(mMaxHeight).intValue(),
                    specMode);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(specSize,
                    specMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
