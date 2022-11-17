package com.shares.app.adapter



import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.shares.app.R
import com.shares.app.base.BaseBindingAdapter
import com.shares.app.base.BaseViewHolder
import com.shares.app.databinding.AdapterLevelPopBinding


class LevelPopAdapter(val lifecycleOwner: LifecycleOwner, private val mOnSelectListener: (Int) -> Unit) : BaseBindingAdapter<String, AdapterLevelPopBinding>() {
    val select= MutableLiveData<Int>()
    init {
        select.value=0
    }
    override fun getLayoutResId(position: Int): Int {
        return R.layout.adapter_level_pop
    }

    fun click(data:String,index:Int){
        select.value=index
        mOnSelectListener(index)
    }



    override fun onBindItem(binding: AdapterLevelPopBinding?, item: String,holder: BaseViewHolder) {
        if (binding != null) {
            if(binding.lifecycleOwner==null){
                binding.lifecycleOwner=lifecycleOwner
            }
            binding.setVariable(BR.data, item)
            binding.setVariable(BR.vm, this)
            binding.setVariable(BR.itemIndex, holder.adapterPosition)
            binding.executePendingBindings()
        }
    }

}
