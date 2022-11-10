package com.shares.app.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jaeger.library.StatusBarUtil
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.databinding.FragmentHomeBinding
import com.shares.app.databinding.FragmentLoginBinding
import com.shares.app.databinding.FragmentMyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFragment : BaseFragment() {
    private lateinit var mBinding: FragmentMyBinding
    private val mVm: MyViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMyBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        mVm.getUserInfo()
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->
            when (event) {


            }
        })
    }


    override fun getBaseModel(): BaseModel {
        return mVm
    }
}