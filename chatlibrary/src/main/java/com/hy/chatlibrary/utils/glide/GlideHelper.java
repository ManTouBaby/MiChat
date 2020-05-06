package com.hy.chatlibrary.utils.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

/**
 * @author:MtBaby
 * @date:2020/04/20 11:58
 * @desc:
 */
public class GlideHelper {
    static float density = 3.0f;

    /**
     * 自适应宽度加载图片。保持图片的长宽比例不变，通过修改imageView的高度来完全显示图片。
     */
    public static void loadIntoUseFitWidth(Context context, final String imageUrl, final ImageView imageView) {
        float density = context.getResources().getDisplayMetrics().density;
        GlideApp.with(context)
                .load(imageUrl)
//                .fitCenter()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(new RoundedCorners((int) (density * 4)))
//                .transition(new DrawableTransitionOptions().crossFade(200))//渐显效果
                .into(imageView);
    }

    /**
     * 自适应宽度加载图片。保持图片的长宽比例不变，通过修改imageView的高度来完全显示图片。
     */
    public static void loadIntoUseNoCorner(Context context, final String imageUrl, final ImageView imageView) {
        GlideApp.with(context)
                .load(imageUrl)
//                .fitCenter()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .transform(new RoundedCorners(20))
//                .transition(new DrawableTransitionOptions().crossFade(200))//渐显效果
                .into(imageView);
    }
}
