package com.shares.app

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jaeger.library.StatusBarUtil
import com.shares.app.databinding.ActivityMainBinding
import com.shares.app.ui.MainHost
import com.shares.app.util.KeyboardUtil
import com.shares.app.util.PreferenceUtil
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), MainHost {
    private lateinit var mBinding: ActivityMainBinding
    val isHide = MutableLiveData<Boolean>()
    val index = MutableLiveData<Int>()
    val flow = MutableLiveData<Boolean>()
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
        flow.value=false
        up.value=false
        price.value=""
        rate.value=""
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        if (mPreferenceUtil.isLogin()) {
            navGraph.startDestination = R.id.navigation_home
            navController.setGraph(navGraph)
        } else {
            navGraph.startDestination = R.id.navigation_login
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

    override fun showFlow(isShow: Boolean) {
       flow.value=isShow
    }

    override fun change(tprice: String, trate: String, tup: Boolean) {
        price.value=tprice
        rate.value=trate
        up.value=tup
    }

    override fun resetNavToHome() {
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.startDestination = R.id.navigation_home
        navController.graph = navGraph

    }
    override fun resetNavToLogin() {
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.startDestination = R.id.navigation_login
        navController.graph = navGraph
    }
}