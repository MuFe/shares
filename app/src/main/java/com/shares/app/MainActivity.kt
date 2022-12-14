package com.shares.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jaeger.library.StatusBarUtil
import com.shares.app.databinding.ActivityMainBinding
import com.shares.app.ui.DataService
import com.shares.app.ui.MainHost
import com.shares.app.util.PreferenceUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), MainHost {
    private lateinit var mBinding: ActivityMainBinding
    private val mVm: MainAcvitityViewModel by viewModel()
    val isHide = MutableLiveData<Boolean>()
    val index = MutableLiveData<Int>()
    val up = MutableLiveData<Boolean>()
    val price = MutableLiveData<String>()
    val rate = MutableLiveData<String>()
    private val mPreferenceUtil: PreferenceUtil by inject()
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        setContentView(mBinding.root)
        up.value=false
        index.value=1
        price.value=""
        rate.value=""
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        if (mPreferenceUtil.isLogin()) {
            navGraph.setStartDestination(R.id.navigation_data)
            navController.graph = navGraph
            mVm.getUserInfo()
        } else {
            navGraph.setStartDestination(R.id.navigation_login)
            navController.graph = navGraph
        }
        StatusBarUtil.setTranslucentForImageViewInFragment(this,0,null)
        StatusBarUtil.setLightMode(this)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.label == getString(R.string.title_login)||destination.label==getString(R.string.title_register)||destination.label==getString(R.string.title_pay)) {
                isHide.value = true
            } else {
                isHide.value = false
            }
            when (destination.label) {
                getString(R.string.title_home) -> {
                    index.postValue(0)
                }
                getString(R.string.title_data) -> {
                    index.postValue(1)
                }
                getString(R.string.title_my) -> {
                    index.postValue(2)
                }

            }
        }
        if(mPreferenceUtil.getCheck1()){
            val it= Intent(this, DataService::class.java)
            it.action="data_service"
//        DaemonEnv.startServiceMayBind(DataService::class.java)
//        requireActivity().startService(it)
            startForegroundService(it)
        }
    }


    fun goHome() {
        navController.navigate(R.id.navigation_home, bundleOf())
    }

    fun goData() {
        navController.navigate(R.id.navigation_data, bundleOf())
    }


    fun goMy() {
        navController.navigate(R.id.navigation_my, bundleOf())
    }



    override fun changTime() {

    }

    override fun getTime(): MutableLiveData<String> {
        return mVm.timeDe
    }

    override fun change(tprice: String, trate: String, tup: Boolean) {
        price.value=tprice
        rate.value=trate
        up.value=tup
    }

    override fun resetNavToHome() {
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination( R.id.navigation_data)
        navController.graph = navGraph
        mVm.getUserInfo()

    }
    override fun resetNavToLogin() {
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination( R.id.navigation_login)
        navController.graph = navGraph
        val it= Intent(this, DataService::class.java)
        it.action="data_service"
        stopService(it)
    }

}