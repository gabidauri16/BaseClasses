package com.example.baseviewmodel.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ViewState<T : Any?>(val data: T? = null)

/** for each responseModel Type that serviceCall returns and is used in current ViewModel,
 *  this BaseViewModel class takes as a parameter list of ViewStates of each model.
 *  @see BaseViewModel.states
 *
 *  then it creates mutableStates for each viewState from the list.
 *  @see BaseViewModel.stateList
 *
 *  also has helper functions to make requestCall and it handles to save received data in the
 *  corresponding state and also emit actions of loading and showingMessage(or error).
 *
 *  use launch() in combination with call() like this :
 *  launch { call(someRepository.getFirstTestData(), 0) }
 *  @see BaseViewModel.launch
 *  @see BaseViewModel.call
 *  ------
 *  @see takeAs to get corresponding state on the uiSide.
 *
 * */
abstract class BaseViewModel(val states: List<Any>) : ViewModel() {
    protected val _action = MutableSharedFlow<Action>()
    val action = _action.asSharedFlow()

    /** for Compose */
    val stateList = states.map { it.createState() }

    /** for XMl */
    val stateFlowList = states.map { it.createStateFlow() }


    abstract fun onAction(action: Action)

    /** creates MutableState<ViewState<EachModel>> for each element of the states.
     * for Compose */
    private inline fun <reified T : Any?> T.createState(): MutableState<ViewState<T>> =
        mutableStateOf(ViewState())

    /** creates MutableStateFlow<ViewState<EachModel>> for each element of the states.
     * for XMl */
    private inline fun <reified T : Any?> T.createStateFlow(): MutableStateFlow<ViewState<T>> =
        MutableStateFlow(ViewState())

    /** launches coroutine in viewModelScope. used in combination with call function to make requestCalls.
     *  also handles to emit message(or Error) and loading Actions.
     *  @see call
     * */
    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        try {
            viewModelScope.launch {
                _action.emit(Action.Loading())
                block.invoke(this)
                _action.emit(Action.Loading(false))
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _action.emit(Action.Loading(false))
                _action.emit(Action.Message(e.message ?: "some error"))
            }
        }
    }

    /** makes serviceCall and updates corresponding State.
     *
     *  parameter: response - takes serviceCall that returns Result<YourServiceCallResponseModel>.
     *  parameter: stateIndex - takes index of corresponding ViewState from States.
     *  @see states
     * */
    protected suspend inline fun <reified T : Any?> call(response: Result<T>, stateIndex: Int) {
        if (response.isSuccess) {
            stateList[stateIndex].postChange { copy(data = response.getOrNull()) }
            stateFlowList[stateIndex].update { it.copy(data = response.getOrNull()) }
        } else {
            stateList[stateIndex].postChange { copy(data = null) }
            stateFlowList[stateIndex].update { it.copy(data = null) }
            response.exceptionOrNull()?.message?.let { _action.emit(Action.Message(it)) }
        }
    }

    /** emit action */
    fun emitAction(action: Action) = launch { _action.emit(action) }

    open class Action {
        data class Message(val msg: String) : Action()
        data class Loading(val loading: Boolean = true) : Action()
    }
}

/** casts state from the BaseViewModel.statesList to it's real data Type and returns it's value.
 * @see BaseViewModel.stateList
 * */
@Suppress("UNCHECKED_CAST")
fun <T> MutableState<ViewState<Any>>.takeAs(): T? = try {
    this.value.data as T
} catch (e: Exception) {
    null
}

/** casts state from the BaseViewModel.statesFlowList to it's real data Type and returns it with it's real value.
 * @see BaseViewModel.stateList
 * */
@Suppress("UNCHECKED_CAST")
fun <T> MutableStateFlow<ViewState<Any>>.takeAs(): MutableStateFlow<ViewState<T>>? = try {
    this as MutableStateFlow<ViewState<T>>
} catch (e: Exception) {
    null
}

/** post change to a mutableState of something,
 *  for example data class that contains TextField values. */
inline fun <T> MutableState<T>.postChange(copy: T.() -> T) {
    this.value = copy(this.value)
}

// todo: consider state loss after process death?