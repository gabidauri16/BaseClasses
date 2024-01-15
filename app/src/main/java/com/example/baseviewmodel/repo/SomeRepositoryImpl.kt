package com.example.baseviewmodel.repo

import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.SecondModel
import kotlinx.coroutines.delay

class SomeRepositoryImpl : SomeRepository {
    var firstIsSuccess = true
    var secondIsSuccess = true

    override suspend fun getFirstTestData(): Result<FirstModel> {
        delay(500)
        return if (firstIsSuccess) {
            Result.success(FirstModel("first Successful Data"))
        } else {
            Result.failure(Throwable("First Data Call Failed"))
        }.also { firstIsSuccess = !firstIsSuccess }
    }

    override suspend fun getSecondTestData(): Result<SecondModel> {
        delay(1000)
        return if (secondIsSuccess) {
            Result.success(SecondModel("Second Successful Data"))
        } else {
            Result.failure(Throwable("Second Data Call Failed"))
        }.also { secondIsSuccess = !secondIsSuccess }
    }
}