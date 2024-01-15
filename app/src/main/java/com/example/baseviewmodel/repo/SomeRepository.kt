package com.example.baseviewmodel.repo

import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.SecondModel

interface SomeRepository {
    suspend fun getFirstTestData(): Result<FirstModel>
    suspend fun getSecondTestData(): Result<SecondModel>
}