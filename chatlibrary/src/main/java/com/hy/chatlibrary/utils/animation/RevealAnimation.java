package com.hy.chatlibrary.utils.animation;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * @author:MtBaby
 * @date:2020/04/20 10:11
 * @desc:揭露动画
 */
public class RevealAnimation {
    private boolean preShowing;//动画是否正在执行
    private boolean isShow;//揭露对象是否显示
    private OnTargViewVisibleListener mOnTargViewVisibleListener;

    public boolean isShow() {
        return isShow;
    }

    public void launchRevealAnimation(final View tagView, View clickView) {
        if (preShowing) return;
        //求出第2个和第3个参数
        int[] vLocation = new int[2];
        clickView.getLocationInWindow(vLocation);
        int centerX = vLocation[0] + clickView.getWidth() / 2;
        int centerY = vLocation[1] + clickView.getHeight() / 2;

        //求出要揭露 View 的对角线，来作为扩散圆的最大半径
        int hypotenuse = (int) Math.hypot(tagView.getWidth(), tagView.getHeight());

        if (isShow) {//隐藏 揭露对象
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator circularReveal = ViewAnimationUtils.createCircularReveal(tagView, centerX, centerY, hypotenuse, 0);
                circularReveal.setDuration(1000);
                circularReveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        preShowing = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tagView.setVisibility(View.GONE);
                        preShowing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                circularReveal.start();
            } else {
                tagView.setVisibility(View.GONE);
            }
            if (mOnTargViewVisibleListener!=null){
                mOnTargViewVisibleListener.onVisibleChange(false);
            }
//            StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
            isShow = false;
        } else {//显示 揭露对象
//            StatusBarUtil.setStatueColor(this, R.color.mi_chat_black_bg, true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator circularReveal = ViewAnimationUtils.createCircularReveal(tagView, centerX, centerY, 0, hypotenuse == 0 ? 3000 : hypotenuse);
                circularReveal.setDuration(1000);
                circularReveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        preShowing = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        preShowing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                //注意：这里显示 mPuppet 调用并没有在监听方法里，并且是在动画开始前调用。
                tagView.setVisibility(View.VISIBLE);
                circularReveal.start();
            } else {
                tagView.setVisibility(View.VISIBLE);
            }
            if (mOnTargViewVisibleListener!=null){
                mOnTargViewVisibleListener.onVisibleChange(true);
            }
            isShow = true;
        }
    }

    public void setOnTargViewVisibleListener(OnTargViewVisibleListener mOnTargViewVisibleListener) {
        this.mOnTargViewVisibleListener = mOnTargViewVisibleListener;
    }

    public static interface OnTargViewVisibleListener {
        void onVisibleChange(boolean isVisible);
    }
}
