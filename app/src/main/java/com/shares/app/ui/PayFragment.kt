package com.shares.app.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alipay.sdk.app.EnvUtils
import com.jaeger.library.StatusBarUtil
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.databinding.FragmentHomeBinding
import com.shares.app.databinding.FragmentLoginBinding
import com.shares.app.databinding.FragmentPayBinding
import com.shares.app.extension.toast
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.koin.androidx.viewmodel.ext.android.viewModel

class PayFragment : BaseFragment() {
    private lateinit var mBinding: FragmentPayBinding
    private val mVm: PayViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPayBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        mVm.getData()
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                is PayViewModel.ViewModelEvent.StartAliEvent->{
                    mVm.startAliPay(event.msg,requireActivity())
                }
                is PayViewModel.ViewModelEvent.PayEvent->{
                    if(event.fail){
                        requireContext().toast(R.string.pay_error)
                    }else{
                        mVm.getUserInfo()
                    }
                }
            }
        })
    }

    fun startWxPay(){
        val api= WXAPIFactory.createWXAPI(requireContext(), null, false)
        val request = PayReq();
        request.appId = "wxd930ea5d5a258f4f";

        request.partnerId = "1900000109";

        request.prepayId= "1101000000140415649af9fc314aa427";

        request.packageValue = "Sign=WXPay";

        request.nonceStr= "1101000000140429eb40476f8896f4c9";

        request.timeStamp= "1398746574";

        request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
        api.sendReq(request);
    }


    override fun getBaseModel(): BaseModel {
        return mVm
    }
}