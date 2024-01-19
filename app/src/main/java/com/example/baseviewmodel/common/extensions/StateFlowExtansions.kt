package com.example.baseviewmodel.common.extensions

import com.example.baseviewmodel.common.base.BaseViewModel
import com.example.baseviewmodel.common.base.ViewState
import kotlinx.coroutines.flow.MutableStateFlow

/** casts state from the BaseViewModel.statesFlowList to it's real data Type and returns it with it's real value.
 * @see BaseViewModel.stateList
 * */
@Suppress("UNCHECKED_CAST")
fun <T> MutableStateFlow<ViewState<Any>>.takeAs(): MutableStateFlow<ViewState<T>>? = try {
    this as MutableStateFlow<ViewState<T>>
} catch (e: Exception) {
    null
}