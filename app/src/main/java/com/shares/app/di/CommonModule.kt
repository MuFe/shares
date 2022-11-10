package com.shares.app.di


import com.shares.app.util.*
import org.koin.dsl.module

val commonModule = module {
    single { PreferenceUtil(get()) }
    factory { KeyboardUtil() }
    factory { NetworkUtil(get(),get()) }


}
