package com.shares.app.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.shares.app.R
import com.shares.app.adapter.LevelPopAdapter
import com.shares.app.databinding.DialogLevelBinding

class LevelPopupWindow(private val mOnSelectListener: (Int) -> Unit): PopupWindow() {
    private lateinit var mBinding: DialogLevelBinding
    private var adapter: LevelPopAdapter? = null
    fun setContent(context: Context, lifecycleOwner: LifecycleOwner) {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_level, null, false
        );
        mBinding.lifecycleOwner = lifecycleOwner
        mBinding.vm = this
        setFocusable(true)
        adapter = LevelPopAdapter(lifecycleOwner) {
            mOnSelectListener(it)
        }
        mBinding.recycler.adapter = adapter
        height = ViewGroup.LayoutParams.WRAP_CONTENT


        contentView = mBinding.root
    }

    fun showWithList(view: View, data: List<String>, index:Int) {
        width = view.measuredWidth
        adapter?.select!!.value=index
        adapter?.items?.clear()
        adapter?.items?.addAll(data)
        adapter?.notifyDataSetChanged()
        showAsDropDown(view)
    }
}