package com.example.robocv.di

import RoboCvDbRepositoryImpl
import MainActivityViewModel
import com.example.robocv.data.RoboCvDbRepository
import com.example.robocv.domain.main.RoboCvDbInteractor
import com.example.robocv.domain.main.impl.RoboCvDbInteractorImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module{

    single<RoboCvDbRepository>{
        RoboCvDbRepositoryImpl(get())
    }
    single<RoboCvDbInteractor>{
        RoboCvDbInteractorImpl(get())
    }
    viewModel {
        MainActivityViewModel(get())
    }
}