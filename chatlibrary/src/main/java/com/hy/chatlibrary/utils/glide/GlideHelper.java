package com.hy.chatlibrary.utils.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hrw.chatlibrary.R;

/**
 * @author:MtBaby
 * @date:2020/04/20 11:58
 * @desc:
 */
public class GlideHelper {
    static float density = 3.0f;

    public static void loadIntoUseNoCorner(Context context, final String imageUrl, final ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.mipmap.icon_image_error);
        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
//                .fitCenter()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .transform(new RoundedCorners(20))
//                .transition(new DrawableTransitionOptions().crossFade(200))//渐显效果
                .into(imageView);
    }
}
