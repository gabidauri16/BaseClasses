package com.example.baseviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ViewState<T : Any?>(val data: T? = null, val loading: Boolean = false)

open class BaseViewModel(states: List<Any>) : ViewModel() {
    val defaultAction = Channel<DefaultAction>()
    val loading = mutableStateOf(true)

    val stateList = states.map { it.createState() }

    private inline fun <reified T : Any?> T.createState(): MutableStateFlow<ViewState<T>> {
        return MutableStateFlow(ViewState())
    }

    fun tryInViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        try {
            viewModelScope.launch {
                defaultAction.send(DefaultAction.Loading())
                block.invoke(this)
                defaultAction.send(DefaultAction.Loading(false))
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                DefaultAction.Message(e.message ?: "some error")
                defaultAction.send(DefaultAction.Loading(false))
            }
        }
    }

    fun isAnyStateLoading() {
        stateList.forEach {
            it.value.loading
        }
    }

    suspend inline fun <reified T : Any?> call(
        response: Result<T>,
        stateIndex: Int
    ) {
        try {
            if (response.isSuccess) {
                stateList[stateIndex].postChange {
                    copy(
                        data = response.getOrNull(),
                        loading = false
                    )
                }
            } else {
                stateList[stateIndex].postChange { copy(data = null, loading = false) }
                defaultAction.send(
                    DefaultAction.Message(response.exceptionOrNull()?.message ?: "some error")
                )
//                defaultAction.send(DefaultAction.Loading(false))
            }
        } catch (e: Exception) {
            stateList[stateIndex].postChange { copy(loading = false) }
            defaultAction.send(DefaultAction.Message(e.message ?: "some error"))
//            defaultAction.send(DefaultAction.Loading(false))
        }
    }

    override fun onCleared() {
        super.onCleared()
        defaultAction.close()
    }

    open class DefaultAction {
        data class Message(val msg: String) : DefaultAction()
        data class Loading(val loading: Boolean = true) : DefaultAction()
    }
}

fun <T> ViewState<Any>.takeAsStateOf(): T? = try {
    (this.data as T)
} catch (e: Exception) {
    null
}

fun <T> ViewState<Any>.takeAsStateFlowOf(): StateFlow<ViewState<T>>? = try {
    (MutableStateFlow(this) as StateFlow<ViewState<T>>)
} catch (e: Exception) {
    null
}


/** post change to a mutableState of something,
 *  for example data class that contains TextField values */
inline fun <T> MutableState<T>.postChange(copy: T.() -> T) {
    this.value = copy(this.value)
}

/** post change to a mutableState of something,
 *  for example data class that contains TextField values */
inline fun <T> MutableStateFlow<T>.postChange(copy: T.() -> T) {
    this.value = copy(this.value)
}