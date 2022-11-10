package com.shares.app.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.ObservableArrayList
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import androidx.annotation.LayoutRes

abstract class BaseBindingAdapter<M, B : ViewDataBinding?>() : RecyclerView.Adapter<BaseViewHolder>(){
    var items: ObservableArrayList<M>
        protected set


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent?.context), getLayoutResId(viewType), parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding: B? = DataBindingUtil.getBinding(holder!!.itemView)
        onBindItem(binding, items[position],holder)
    }

    @LayoutRes
    protected abstract fun getLayoutResId(viewType: Int): Int
    protected abstract fun onBindItem(binding: B?, item: M,holder: BaseViewHolder)

    init {
        items = ObservableArrayList()
    }
}