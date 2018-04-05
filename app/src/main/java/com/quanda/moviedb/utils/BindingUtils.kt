package com.quanda.moviedb.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.SystemClock
import android.support.annotation.DrawableRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.quanda.moviedb.BuildConfig
import com.quanda.moviedb.constants.Constants
import com.quanda.moviedb.widgets.PullRefreshRecyclerView
import java.io.File

@BindingAdapter("recyclerAdapter")
fun setRecyclerAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
    view.setHasFixedSize(true)
    view.adapter = adapter
}

@BindingAdapter("onScrollListener")
fun setScrollListener(view: RecyclerView,
        listener: RecyclerView.OnScrollListener?) {
    if (listener != null) view.addOnScrollListener(listener)
}

@BindingAdapter("layoutManager")
fun setLayoutManager(view: RecyclerView,
        layoutManager: RecyclerView.LayoutManager) {
    view.layoutManager = layoutManager
}

@BindingAdapter("recyclerAdapter")
fun setPTRRecyclerAdapter(view: PullRefreshRecyclerView,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
    view.adapter.set(adapter)
}

@BindingAdapter("layoutManager")
fun setPTRLayoutManager(view: PullRefreshRecyclerView,
        layoutManager: RecyclerView.LayoutManager) {
    view.layoutManager.set(layoutManager)
}

@BindingAdapter("onScrollListener")
fun setPTRScrollListener(view: PullRefreshRecyclerView,
        listener: RecyclerView.OnScrollListener) {
    view.onScrollListener.set(listener)
}

@BindingAdapter("refreshListener")
fun setPTRRefreshListener(view: PullRefreshRecyclerView,
        listener: SwipeRefreshLayout.OnRefreshListener) {
    view.onRefreshListener.set(listener)
}

@BindingAdapter("refreshing")
fun setPTRRefreshing(view: PullRefreshRecyclerView,
        isRefreshing: Boolean) {
    view.isRefreshing.set(isRefreshing)
}

@BindingAdapter("glideSrc")
fun setGlideSrc(view: ImageView, @DrawableRes src: Int) {
    if (src != -1) Glide.with(view.context).load(src).into(view)
}

@BindingAdapter("loadUri")
fun loadLocalImage(view: ImageView, uri: Uri) {
    Glide.with(view.context).load(uri).into(view)
}

@BindingAdapter(
        value = ["loadImage", "placeholder", "centerCrop", "cacheSource", "animation"],
        requireAll = false)
fun loadImage(img: ImageView, url: String?, placeHolder: Drawable?,
        centerCrop: Boolean?, isCacheSource: Boolean?, animation: Boolean?) {
    if (TextUtils.isEmpty(url)) {
        img.setImageDrawable(placeHolder)
        return
    }
    val urlWithHost = BuildConfig.BASE_IMAGE_URL + url
    val requestBuilder = Glide.with(img.context).load(urlWithHost)
    val requestOptions = RequestOptions().diskCacheStrategy(
            if (isCacheSource ?: false) DiskCacheStrategy.DATA else DiskCacheStrategy.RESOURCE)
            .placeholder(placeHolder)

    if (animation ?: true) requestOptions.dontAnimate()
    if (centerCrop ?: false) {
        requestOptions.centerCrop()
    } else {
        requestOptions.fitCenter()
    }
    val file = File(urlWithHost)
    if (file.exists()) {
        requestOptions.signature(ObjectKey(file.lastModified().toString()))
    }
    requestBuilder.apply(requestOptions).into(img)
}

@BindingAdapter("clickSafe")
fun setClickSafe(view: View, listener: View.OnClickListener?) {
    view.setOnClickListener(object : View.OnClickListener {
        private var mLastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < Constants.THRESHOLD_CLICK_TIME) {
                return
            }
            listener?.onClick(v)
            mLastClickTime = SystemClock.elapsedRealtime()
        }
    })
}