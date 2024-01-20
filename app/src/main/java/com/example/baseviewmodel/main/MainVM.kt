package com.example.baseviewmodel.main

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.example.baseviewmodel.common.base.Action
import com.example.baseviewmodel.common.base.ViewState
import com.example.baseviewmodel.common.base.processDeathSurvivors.BaseSurvivorViewModel
import com.example.baseviewmodel.repo.SomeRepository
import kotlinx.parcelize.Parcelize

/** inject savedStateHandle into constructor and to pass it to BaseSurvivorViewModel */
class MainVM(private val someRepository: SomeRepository, savedStateHandle: SavedStateHandle) :
    BaseSurvivorViewModel(
        states = mutableListOf(
            ViewState<FirstModel>(),
            ViewState<SecondModel>()
        ), savedStateHandle
    ) {

    init {
//        getBothData()
    }

    override fun onAction(action: Action) {
        when (action) {
            MainActions.GetFirstDataAction -> getFirstData()
            MainActions.GetSecondDataAction -> getSecondData()
            MainActions.GetBothDataAction -> getBothData()
            MainActions.GoToSecondScreenAction -> action.emit()
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

}

@Parcelize
data class FirstModel(
    val firstData: String
) : Parcelable

@Parcelize
data class SecondModel(
    val secondData: String
) : Parcelable
