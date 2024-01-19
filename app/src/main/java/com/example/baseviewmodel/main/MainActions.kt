package com.example.baseviewmodel.main

import com.example.baseviewmodel.common.base.Action

sealed interface MainActions : Action {
    data object GetFirstDataAction : MainActions
    data object GetSecondDataAction : MainActions
    data object GetBothDataAction : MainActions
    data object GoToSecondScreenAction : MainActions
}