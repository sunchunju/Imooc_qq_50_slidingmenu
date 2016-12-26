package com.imooc.slidingmenu.com.imooc.slidingmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.imooc.slidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by suncj1 on 2016/12/23.
 */

public class SlidingMenu extends HorizontalScrollView{

    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;

    private int mMenuWidth;
    // dp
    private int mMenuRightPadding = 50;

    private boolean once;

    private boolean isOpen;

    /**
     * 未使用自定义属性时，调用
     *
     * @param context
     * @param attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * 当使用了自定义属性时，会调用此构造方法
     *
     * @param context

     * @param attrs
     * @param defStyle
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        //获取自定义属性
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyle, 0);

        //获取自定义属性的个数
        int n = array.getIndexCount();

        //循环获取每个属性
        for (int i = 0;i< n; i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = array.getDimensionPixelSize(attr,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                break;
            }
        }

        array.recycle();

        //获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        //利用系统API将dp转换成px
       // mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

    }

    /**
     * 设置子View的宽和高 设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!once){
            mWapper = (LinearLayout)getChildAt(0); //最外层的一个
            mMenu = (ViewGroup) mWapper.getChildAt(0); //mWapper的第一个控件
            mContent = (ViewGroup) mWapper.getChildAt(1); //mWapper的第二个控件

            //显示设置mMenu的宽度
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            //显示设置mContent的宽度
            mContent.getLayoutParams().width = mScreenWidth;

            once = true;
        }

    }

    /**
     * 通过设置偏移量，将menu隐藏
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                //如果menu隐藏的宽度超过一半，则将其隐藏，否则显示
                int scrollX = getScrollX();
                if (scrollX > mMenuWidth/2){
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false; //菜单隐藏
                }else {
                    this.smoothScrollTo(0, 0);
                    isOpen = true; //菜单显示
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){

        if (isOpen)return;
        this.smoothScrollTo(0,0);
        isOpen = true;
    }

    /***
     * 关闭菜单
     */
    public void closeMenu(){
        if (!isOpen)return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }

    /***
     * 切换菜单
     */
    public void toggle(){
        if (isOpen){
            closeMenu();
        }else {
            openMenu();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        /***
         * 区别1： 内容区域1.0~0.7缩放的效果
         * scale : 1.0~0.0
         * 0.7+0.3*scale
         *
         * 区别2：菜单的偏移量需要修改
         *
         * 区别3：菜单的显示时有缩放及透明度的变化
         * 缩放：0.7~1.0
         * 1.0-scale*0.3
         * 透明度：0.6~1.0
         * 0.6+0.4*（1-scale）
         */
        float scale = l* 1.0f/mMenuWidth; //1~0
        float rightScale = 0.7f+0.3f*scale;
        float leftScale = 1.0f - scale * 0.3f;
        float leftAlpha = 0.6f + 0.4f*(1-scale);

        //l为menu默认从左向右的偏移量
        ViewHelper.setTranslationX(mMenu, l * 0.7f);
        //左侧菜单显示时的缩放，注意横竖都需要缩放
        ViewHelper.setScaleX(mMenu,leftScale);
        ViewHelper.setScaleY(mMenu,leftScale);
//        ViewHelper.setPivotX(mMenu,- 250); //设置缩放中心点
        //左侧菜单透明度的变化
        ViewHelper.setAlpha(mMenu,leftAlpha);

        //设置内容区域缩放中心点为左侧
        ViewHelper.setPivotX(mContent,0);
        ViewHelper.setPivotY(mContent,mContent.getHeight()/2);
        ViewHelper.setScaleX(mContent,rightScale);
        ViewHelper.setScaleY(mContent,rightScale);
    }
}
