package com.example.baseviewmodel.main

import com.example.baseviewmodel.base.BaseViewModel
import com.example.baseviewmodel.base.ViewState
import com.example.baseviewmodel.repo.SomeRepository

class MainVM(
    private val someRepository: SomeRepository
) : BaseViewModel(
    states = listOf(
        ViewState<FirstModel>(),
        ViewState<SecondModel>()
    )
) {
    init {
        getBothData()
    }

    override fun onAction(action: Action) {
        when (action) {
            GetFirstDataAction -> getFirstData()
            GetSecondDataAction -> getSecondData()
            GetBothDataAction -> getBothData()
            GoToSecondScreenAction -> emitAction(GoToSecondScreenAction)
        }
    }

    private fun getBothData() {
        launch {
            someRepository.apply {
                call(getFirstTestData(), 0)
                call(getSecondTestData(), 1)
            }
        }
    }

    private fun getFirstData() {
        launch { call(someRepository.getFirstTestData(), 0) }
    }

    private fun getSecondData() {
        launch { call(someRepository.getSecondTestData(), 1) }
    }

    object GetFirstDataAction : Action()
    object GetSecondDataAction : Action()
    object GetBothDataAction : Action()
    object GoToSecondScreenAction : Action()
}

data class FirstModel(
    val firstData: String
)

data class SecondModel(
    val secondData: String
)
