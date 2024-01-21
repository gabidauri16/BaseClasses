package com.example.baseviewmodel.repo

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.SecondModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class SomeRepositoryImpl : SomeRepository {
    var firstIsSuccess = true
    var secondIsSuccess = true

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getFirstTestData(): Result<FirstModel> {
        delay(500)
        return if (firstIsSuccess) {
            Result.success(FirstModel("first Successful Data"))
        } else {
//            Result.failure(Throwable("First Data Call Failed"))
            throw CancellationException()
//            throw HttpException("network error happened" , Throwable("network error throwable"))
//            throw NullPointerException()
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