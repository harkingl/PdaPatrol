package com.pda.patrol.baseclass.component;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public interface ITitleBarLayout {

    /**
     * 设置左边标题的点击事件
     * 
     * Set the click event of the left header
     *
     * @param listener
     */
    void setOnLeftClickListener(View.OnClickListener listener);

    /**
     * 设置右边标题的点击事件
     * 
     * Set the click event of the right title
     *
     * @param listener
     */
    void setOnRightClickListener(View.OnClickListener listener);

    /**
     * 设置标题
     * 
     * set Title
     *
     */
    void setTitle(String title, Position position);

    /**
     * 返回左边标题区域
     * 
     * Return to the left header area
     *
     * @return
     */
    LinearLayout getLeftGroup();

    /**
     * 返回右边标题区域
     * 
     * Return to the right header area
     *
     * @return
     */
    LinearLayout getRightGroup();

    /**
     * 返回左边标题的图片
     * 
     * Returns the image for the left header
     *
     * @return
     */
    ImageView getLeftIcon();

    /**
     * 设置左边标题的图片
     * 
     * Set the image for the left header
     *
     * @param resId
     */
    void setLeftIcon(int resId);

    /**
     * 返回右边标题的图片
     * 
     * Returns the image with the right header
     *
     * @return
     */
    ImageView getRightIcon();

    /**
     * 设置右边标题的图片
     * 
     * Set the image for the title on the right
     *
     * @param resId
     */
    void setRightIcon(int resId);

    /**
     * 返回左边标题的文字
     * 
     * Returns the text of the left header
     *
     * @return
     */
    TextView getLeftTitle();

    /**
     * 返回中间标题的文字
     * 
     * Returns the text of the middle title
     *
     * @return
     */
    TextView getMiddleTitle();

    /**
     * 返回右边标题的文字
     * 
     * Returns the text of the title on the right
     *
     * @return
     */
    TextView getRightTitle();

    /**
     * 标题区域的枚举值
     * 
     * enumeration value of the header area
     */
    enum Position {
        /**
         * 左边标题
         * 
         * left title
         */
        LEFT,
        /**
         * 中间标题
         * 
         * middle title
         */
        MIDDLE,
        /**
         * 右边标题
         * 
         * right title
         */
        RIGHT
    }

}
