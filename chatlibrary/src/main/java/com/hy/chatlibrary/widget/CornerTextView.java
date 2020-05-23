package com.hy.chatlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.hrw.chatlibrary.R;

/**
 * @author:MtBaby
 * @date:2020/05/14 9:16
 * @desc:
 */
public class CornerTextView extends AppCompatTextView {
    float mRadius;
    boolean autoBackGround;
    int mStrokeColor;
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF();

    public CornerTextView(Context context) {
        this(context, null);
    }

    public CornerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerTextView, 0, 0);
        mRadius = typedArray.getDimension(R.styleable.CornerTextView_radius, 0);
        autoBackGround = typedArray.getBoolean(R.styleable.CornerTextView_auto_background, false);
        typedArray.recycle();
        initView();
    }

    public void setAutoBackGroundText(CharSequence text) {
        this.setText(text, BufferType.NORMAL);
    }

    private void initView() {
        // 初始化画笔
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);               // 设置画笔为无锯齿
//        mPaint.setStrokeWidth(strokeWidth);      // 线宽
        // 设置边框线的颜色, 如果声明为边框跟随文字颜色且当前边框颜色与文字颜色不同时重新设置边框颜色
//        if (mFollowTextColor && strokeColor != getCurrentTextColor())
//            strokeColor = getCurrentTextColor();
        // 设置背景
//        setBackground(DrawableUtil.getPressedSelector(enableColor, contentColor, pressedColor, cornerRadius));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (autoBackGround) {
            // 设置画笔颜色
            mPaint.setColor(mStrokeColor);
            // 画空心圆角矩形
            mRectF.left = mRectF.top = 0;
            mRectF.right = getMeasuredWidth();
            mRectF.bottom = getMeasuredHeight();
            System.out.println("CornerTextView：" + mRectF);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mStrokeColor = ColorUtils.getColorBySeed(text.toString());
        String label =text.toString();
        if (label.length()>2)label = label.substring(label.length()-2);
        super.setText(label, type);
    }
}
