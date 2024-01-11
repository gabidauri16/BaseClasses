package com.example.baseviewmodel

interface SomeRepository {
    suspend fun getFirstTestData(): Result<FirstModel>
    suspend fun getSecondTestData(): Result<SecondModel>
}