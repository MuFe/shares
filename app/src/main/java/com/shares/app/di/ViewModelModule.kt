package com.shares.app.di




import com.shares.app.MainAcvitityViewModel
import com.shares.app.ui.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(),get()) }
    viewModel { HomeViewModel(get(),get()) }
    viewModel { DataViewModel(get(),get()) }
    viewModel { MyViewModel(get(),get()) }
    viewModel { PayViewModel(get(),get()) }
    viewModel { LineViewModel() }
    viewModel { MainAcvitityViewModel(get(),get()) }
}

