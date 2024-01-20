package com.example.baseviewmodel.di

import com.example.baseviewmodel.main.MainVM
import com.example.baseviewmodel.repo.SomeRepository
import com.example.baseviewmodel.repo.SomeRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModel { MainVM(get(), get()) }
//    viewModel { SecondVM() }
    single { SomeRepositoryImpl() } bind SomeRepository::class
}