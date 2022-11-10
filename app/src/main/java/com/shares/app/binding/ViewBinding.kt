package com.shares.app.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


@BindingAdapter("isGone")
fun View.setIsGone(isGone: Boolean) {
    visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("isInvisible")
fun View.setIsInvisible(isInvisible: Boolean) {
    visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}


@BindingAdapter(
    value = ["backGround"],
    requireAll = false
)
fun setBackGround(
    view: View,
    source: Int = 0
) {
    if (source != 0) {
        view.background = view.context.resources.getDrawable(source)
    }else{
        view.background=null
    }
}

@BindingAdapter(
    value = ["txtColor"],
    requireAll = false
)
fun setTxtColor(
    view: TextView,
    source: Int = 0
) {
    if (source != 0) {
        view.setTextColor(view.context.resources.getColor(source))
    }
}

@BindingAdapter(
    value = ["imageUrl", "placeholder", "error", "fallback", "loadWidth", "loadHeight", "cacheEnable", "radius", "isRound"],
    requireAll = false
)
fun setImageUrl(
    view: ImageView,
    source: Any? = null,
    placeholder: Drawable? = null,
    error: Drawable? = null,
    fallback: Drawable? = null,
    imageWidth: Int? = -1,
    imageHeight: Int? = -1,
    cacheEnable: Boolean? = true,
    radius: Int? = 0,
    isRound: Boolean? = false,
) {
    // 计算位图尺寸，如果位图尺寸固定，加载固定大小尺寸的图片，如果位图未设置尺寸，那就加载原图，Glide加载原图时，override参数设置 -1 即可。
    val widthSize = (if ((imageWidth ?: 0) > 0) imageWidth else view.width) ?: -1
    val heightSize = (if ((imageHeight ?: 0) > 0) imageHeight else view.height) ?: -1
    val re = Glide.with(view.context)
        .asDrawable()
        .load(source)
        .placeholder(placeholder)
        .error(error)
        .fallback(fallback)
        .override(widthSize, heightSize)
        .transition(DrawableTransitionOptions.withCrossFade())
    if (isRound == true && widthSize > 0) {
        re.transform(CircleCrop()).into(view)
    } else if (radius != null) {
        re.transform(CenterCrop(), RoundedCorners(radius)).into(view)
    } else {
        re.into(view)
    }
}


@BindingAdapter(
    value = ["layWidth","layHeight"],
    requireAll = false
)
fun setLayout(
    view: View,
    width: Int = 0,
    height: Int = 0,
) {
    val lay=view.layoutParams
    if (width != 0) {
        lay.width=width
    }
    if (height != 0) {
        lay.height=height
    }
}