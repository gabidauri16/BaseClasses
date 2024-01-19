package com.example.baseviewmodel.common.base

/** Base action class to use in onAction() function
 * @see BaseViewModel.onAction
 * */
interface Action {
    data class Message(val msg: String) : Action
    data class Loading(val loading: Boolean = true) : Action
}