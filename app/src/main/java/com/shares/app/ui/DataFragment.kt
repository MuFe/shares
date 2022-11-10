package com.shares.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.jaeger.library.StatusBarUtil
import com.shares.app.R
import com.shares.app.adapter.SimpleFragmentPagerAdapter
import com.shares.app.base.BaseModel
import com.shares.app.databinding.FragmentDataBinding
import com.shares.app.util.NetworkUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DataFragment : BaseFragment() {
    private lateinit var mBinding: FragmentDataBinding
    private val mVm: DataViewModel by viewModel()
    private val networkUtil: NetworkUtil by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDataBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        mVm.format.value=requireContext().resources.getString(R.string.data_tip15)
        val temp=ViewModelProviders.of(requireActivity())[LineViewModel::class.java]
        temp.loadNetWork(networkUtil)
        temp.loadDay()
        val fragments = arrayOf<Fragment>(
            LineFragment(0),
            LineFragment(1),
            LineFragment(2),
            LineFragment(3),
            LineFragment(4),
            LineFragment(5),
        )
        val titles = arrayOf("15分","1时", "日K", "周K", "月K","年K")
        mBinding.viewPager.setOffscreenPageLimit(fragments.size)
        mBinding.viewPager.setAdapter(
            SimpleFragmentPagerAdapter(
                parentFragmentManager,
                fragments,
                titles
            )
        )

        mBinding.tab.setupWithViewPager(mBinding.viewPager)
        mVm.startGetData()
        mVm.startGetDataFull()
        mVm.getUserInfo()
        temp.delayGet()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                DataViewModel.ViewModelEvent.ChangeEvent->{
                    (requireActivity() as MainHost).change(mVm.now.value.orEmpty(),mVm.change.value.orEmpty(),mVm.isPlus.value!!)
                }
            }
        })
        mVm.selectIndex.observe(viewLifecycleOwner,{event->
            (requireActivity() as MainHost).showFlow(event==0)
        })
    }

    override fun getBaseModel(): BaseModel {
        return mVm
    }
}