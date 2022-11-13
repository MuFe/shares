package com.shares.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage
import com.shares.app.R
import com.shares.app.base.BaseModel
import com.shares.app.data.ChartData
import com.shares.app.databinding.FragmentLoginBinding
import com.shares.app.databinding.FragmentOneBinding
import com.shares.app.util.NetworkUtil
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LineFragment(val mType: Int) : BaseFragment() {
    private lateinit var mBinding: FragmentOneBinding
    private lateinit var mVm: LineViewModel
    private lateinit var kTimeData: KLineDataManage
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mVm= ViewModelProviders.of(requireActivity())[LineViewModel::class.java]
        mBinding = FragmentOneBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = mVm
        //初始化
        kTimeData = KLineDataManage(requireContext())
        //初始化
        mBinding.combinedchart.initChart(false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.event.observe(viewLifecycleOwner, { event ->

        })
        mVm.listData.observe(viewLifecycleOwner, { listData ->
            //上证指数代码000001.IDX.SH
            when(mType){
                0->{
                    kTimeData.parseKlineData(mVm.parseData(15*60*1000L,listData), "000001.IDX.SH", false)
                }
                1->{
                    kTimeData.parseKlineData(mVm.parseData(60*60*1000L,listData), "000001.IDX.SH", false)
                }
                2->{
                    val temp=mVm.dayListData.value.orEmpty()+mVm.parseDayData(listData)
                    kTimeData.parseKlineData(temp, "000001.IDX.SH", false)
                }
                3->{
                    val temp=mVm.weekListData.value.orEmpty()+mVm.parseDayData(listData)
                    kTimeData.parseKlineData(temp, "000001.IDX.SH", false)
                }
                4->{
                    val temp=mVm.monthListData.value.orEmpty()+mVm.parseDayData(listData)
                    kTimeData.parseKlineData(temp, "000001.IDX.SH", false)
                }
                5->{
                    val temp=mVm.yearListData.value.orEmpty()+mVm.parseDayData(listData)
                    kTimeData.parseKlineData(temp, "000001.IDX.SH", false)
                }
            }
            mBinding.combinedchart.setDataToChart(kTimeData)
        })
    }

    override fun getBaseModel(): BaseModel {
        return mVm
    }
}