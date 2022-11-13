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
import com.shares.app.databinding.FragmentLoginBinding
import com.shares.app.databinding.FragmentRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment() {
    private lateinit var mBinding: FragmentRegisterBinding
    private val mVm: LoginViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRegisterBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                LoginViewModel.ViewModelEvent.FinishLoginEvent->{
                    (activity as? MainHost)?.resetNavToHome()
                }
                else->{

                }
            }
        })
    }

    override fun getBaseModel(): BaseModel {
        return mVm
    }
}