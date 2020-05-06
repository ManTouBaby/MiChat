package com.hy.chatlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.hrw.chatlibrary.R;

/**
 * @author:MtBaby
 * @date:2020/04/01 9:15
 * @desc:
 */
public class CornerImageView extends AppCompatImageView {
    private final float density;
    private Paint paint;
    private Paint paintBorder;
    private Bitmap mSrcBitmap;
    /**
     * 圆角的弧度
     */
    private float mRadius;
    private boolean mIsCircle;

    public CornerImageView(final Context context) {
        this(context, null);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView, 0, 0);
        mRadius = ta.getDimension(R.styleable.CornerImageView_radius, 0);
        mIsCircle = ta.getBoolean(R.styleable.CornerImageView_circle, false);
        int srcResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0);
        if (srcResource != 0)
            mSrcBitmap = BitmapFactory.decodeResource(getResources(), srcResource);
        ta.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
    }


    @Override
    public void onDraw(Canvas canvas) {
        int width = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        int height = canvas.getHeight() - getPaddingTop() - getPaddingBottom();
        Bitmap image = drawableToBitmap(getDrawable());
        if (image == null) return;
//        System.out.println("执行绘制动作：" + getWidth() + " " + getHeight());
        if (mIsCircle) {
            Bitmap reSizeImage = reSizeImageC(image, width, height);
            canvas.drawBitmap(createCircleImage(reSizeImage, width, height), getPaddingLeft(), getPaddingTop(), null);
        } else {
            Bitmap reSizeImage = reSizeImage(image, width, height);
            Bitmap resultImage = createRoundImage(reSizeImage, width, height);
            canvas.drawBitmap(resultImage, getPaddingLeft(), getPaddingTop(), null);
        }

    }


    /**
     * 画圆角
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    private Bitmap createRoundImage(Bitmap source, int width, int height) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rect, mRadius, mRadius, paint);
        // 核心代码取两个图片的交集部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * 画圆
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int width, int height) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2,
                paint);
        // 核心代码取两个图片的交集部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, (width - source.getWidth()) / 2, (height - source.getHeight()) / 2, paint);
        return target;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("CornerImageView执行测量");
        Bitmap image = drawableToBitmap(getDrawable());
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthmode == MeasureSpec.EXACTLY && heightmode == MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(width, height);
            return;
        }
        if (image != null) {
            int height = image.getHeight();
            int width = image.getWidth();
            float scale = (float) height / (float) width;
            if (scale > 2.5) {//高度至少是宽度的2.5倍，设置一个固定的宽高
                int viewWidth = (int) (110 * density);
                int viewHeight = (int) (258 * density);
                setMeasuredDimension(viewWidth, viewHeight);
            } else if (scale > 2.0) {//高度至少是宽度的2倍,设置一个固定的宽度，高度按比例缩放
                float widthScale = (float) width / 110;
                int viewWidth = (int) (110 * density);
                int viewHeight = (int) ((int) ((float) height / widthScale) * density);
                setMeasuredDimension(viewWidth, viewHeight);
            } else if (scale > 1.0) {////高度至少是宽度的1倍,设置一个固定的宽度，高度按比例缩放
                float widthScale = (float) width / 128.0f;
                int viewWidth = (int) (128.0f * density);
                int viewHeight = (int) ((int) ((float) height / widthScale) * density);
                setMeasuredDimension(viewWidth, viewHeight);
            } else if (scale > 0.5) {//高度至少是宽度的0.5倍,设置宽度固定，高度按比例缩小
                float widthScale = (float) width / 152.0f;
                int viewWidth = (int) (152.0f * density);
                int viewHeight = (int) ((int) ((float) height / widthScale) * density);
                setMeasuredDimension(viewWidth, viewHeight);
            } else {
                float widthScale = (float) width / 180.0f;
                int viewWidth = (int) (180.0f * density);
                int viewHeight = (int) ((int) ((float) height / widthScale) * density);
                setMeasuredDimension(viewWidth, viewHeight);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            if (mSrcBitmap != null) {
                return mSrcBitmap;
            } else {
                return null;
            }
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 重设Bitmap的宽高
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap reSizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算出缩放比
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 矩阵缩放bitmap
        Matrix matrix = new Matrix();
        float scale = Math.min(scaleWidth, scaleHeight);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//        return Bitmap.createBitmap(bitmap, 0, 0, (int) (width / scaleWidth), (int) (height / scaleHeight));
    }

    /**
     * 重设Bitmap的宽高
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap reSizeImageC(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int x = (newWidth - width) / 2;
        int y = (newHeight - height) / 2;
        if (x > 0 && y > 0) {
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, null, true);
        }

        float scale = 1;

        if (width > height) {
            // 按照宽度进行等比缩放
            scale = ((float) newWidth) / width;

        } else {
            // 按照高度进行等比缩放
            // 计算出缩放比
            scale = ((float) newHeight) / height;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

}
