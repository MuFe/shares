package com.shares.app.ui

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
        val content=requireContext().resources.getString(R.string.register_tip1)
        val builder= SpannableString(content)

        val sp= ForegroundColorSpan(requireContext().resources.getColor(R.color.bottom_check))
        builder.setSpan(sp,5,content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(object: ClickableSpan(){
            override fun onClick(p0: View) {
                navigate(R.id.navigation_web, bundleOf("url" to R.raw.pdf))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText=true
                ds.setColor(requireContext().resources.getColor(R.color.bottom_check))
            }

        },5,content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding.tip.movementMethod= LinkMovementMethod.getInstance()
        mBinding.tip.setText(builder)
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