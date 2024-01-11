package com.example.baseviewmodel

import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    factory { MainVM(get()) }
    single { SomeRepositoryImpl() } bind SomeRepository::class

}