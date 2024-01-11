package com.example.baseviewmodel

class MainVM(
    private val someRepository: SomeRepository
) : BaseViewModel(
    listOf(
        ViewState<FirstModel>(),
        ViewState<SecondModel>()
    )
) {
    init {
        getFirstData()
        getSecondData()
    }

    fun getFirstData() {
        tryInViewModelScope {
            call(someRepository.getFirstTestData(), 0)
        }
    }

    fun getSecondData() {
        tryInViewModelScope {
            call(someRepository.getSecondTestData(), 1)
        }
    }
}

data class FirstModel(
    val firstData: String
)

data class SecondModel(
    val secondData: String
)
