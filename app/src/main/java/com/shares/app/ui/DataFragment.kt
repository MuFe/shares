package com.shares.app.ui

import android.app.*
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.jaeger.library.StatusBarUtil
import com.shares.app.adapter.SimpleFragmentPagerAdapter
import com.shares.app.base.BaseModel
import com.shares.app.databinding.FragmentDataBinding
import com.shares.app.util.NetworkUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.shares.app.R
import java.util.*


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
        mVm.check1.observe(viewLifecycleOwner,{event->
            setPrice()
        })
        mVm.check2.observe(viewLifecycleOwner,{event->
            if(mVm.changeCheck.value!!){
                setAl(requireContext().resources.getString(R.string.data_notify1),2,0,event)
            }
//
        })
        mVm.check3.observe(viewLifecycleOwner,{event->
            if(mVm.changeCheck.value!!){
                setAl(requireContext().resources.getString(R.string.data_notify2),17,25,event)
            }
//
        })
        mVm.selectIndex.observe(viewLifecycleOwner,{event->
            (requireActivity() as MainHost).showFlow(event==0)
        })
    }

    fun setPrice(){
        val it=Intent(requireActivity(),DataService::class.java)
        requireActivity().startService(it)
    }

    fun setAl(title:String,hour:Int,min:Int,isShow:Boolean){
        val i = Intent(requireActivity(), MyReceiver::class.java)
        i.putExtra("title",title)
        i
        //创建PendingIntent对象
        //创建PendingIntent对象
        val pi: PendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, i, 0)
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())
        if(c.get(Calendar.HOUR_OF_DAY)>hour||(c.get(Calendar.HOUR_OF_DAY)==hour&&c.get(Calendar.MINUTE)>min)){
            c.set(Calendar.MINUTE, min)
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.add(Calendar.MINUTE, 86400)
        }else{
            c.set(Calendar.MINUTE, min)
            c.set(Calendar.HOUR_OF_DAY, hour)
        }
        val am: AlarmManager =  requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pi)
        if(isShow){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //API19以上使用
                am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi)
            } else {
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi)
            }
        }
    }

    override fun getBaseModel(): BaseModel {
        return mVm
    }
}