package com.example.baseviewmodel.di

import com.example.baseviewmodel.main.MainVM
import com.example.baseviewmodel.main.xmlViews.SecondVM
import com.example.baseviewmodel.repo.SomeRepository
import com.example.baseviewmodel.repo.SomeRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    factory { MainVM(get()) }
    factory { SecondVM() }
    single { SomeRepositoryImpl() } bind SomeRepository::class
}