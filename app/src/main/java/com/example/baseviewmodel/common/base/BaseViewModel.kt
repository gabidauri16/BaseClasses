package com.example.baseviewmodel.common.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseviewmodel.common.extensions.postChange
import kotlinx.coroutines.CoroutineExceptionHandler
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
abstract class BaseViewModel(
    val states: List<Any>
) : ViewModel() {
    protected val _action = MutableSharedFlow<Action>()
    val action = _action.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Action.Message(throwable.message ?: "An unexpected error occurred").emit()
    }

    /** for Compose */
    val stateList = states.map { it.createState() }

    /** for XML */
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
    protected fun launch(
        emitLoadingAction: Boolean = true,
        emitErrorMsgAction: Boolean = false,
        onStart: (() -> Unit)? = null,
        onFinish: (() -> Unit)? = null,
        onException: ((Exception) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch {
            try {
                launch(coroutineContext + coroutineExceptionHandler) {
                    onStart?.invoke()
                    if (emitLoadingAction) emitAction(Action.Loading())
                    block.invoke(this)
                    if (emitLoadingAction) emitAction(Action.Loading(false))
                    onFinish?.invoke()
                }
            } catch (e: Exception) {
                onException?.invoke(e)
                if (emitLoadingAction) Action.Loading(false).emit()
                if (emitErrorMsgAction) Action.Message(e.message ?: "some error").emit()
            }
        }
    }

    /** makes serviceCall and updates corresponding State.
     *
     *  parameter: response - takes serviceCall that returns Result<YourServiceCallResponseModel>.
     *  parameter: stateIndex - takes index of corresponding ViewState from States.
     *  @see states
     * */
    protected suspend inline fun <reified T : Any?> call(
        response: Result<T>, stateIndex: Int,
        onError: (Throwable?) -> Unit = {},
        onSuccess: (Result<T>) -> Unit = {},
    ) {
        if (response.isSuccess) {
            onSuccess.invoke(response)
            stateList[stateIndex].postChange { copy(data = response.getOrNull()) }
            stateFlowList[stateIndex].update { it.copy(data = response.getOrNull()) }
        } else {
            onError.invoke(response.exceptionOrNull())
            stateList[stateIndex].postChange { copy(data = null) }
            stateFlowList[stateIndex].update { it.copy(data = null) }
            response.exceptionOrNull()?.message?.let { emitAction(Action.Message(it)) }
        }
    }

    /** launches a coroutine and emits Action */
    protected fun <T : Action> T.emit() = viewModelScope.launch { _action.emit(this@emit) }

    /** use inside coroutineScope to emit Action */
    protected suspend fun emitAction(action: Action) = _action.emit(action)

}


// todo: consider state loss after process death?