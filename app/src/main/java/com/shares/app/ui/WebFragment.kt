package com.shares.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.danjdt.pdfviewer.PdfViewer
import com.danjdt.pdfviewer.utils.PdfPageQuality
import com.danjdt.pdfviewer.view.PdfViewerRecyclerView
import com.jaeger.library.StatusBarUtil
import com.shares.app.databinding.FragmentWebBinding


class WebFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentWebBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWebBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        PdfViewer.Builder(mBinding.root)
            .view(PdfViewerRecyclerView(requireContext()))
            .quality(PdfPageQuality.QUALITY_1080)
            .build()
            .load(requireArguments().getInt("url"))
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        return mBinding.root
    }

    fun goBack() {
        navigateUp()
    }


}
