package com.example.baseviewmodel.common.base.processDeathSurvivors

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.baseviewmodel.common.base.Action
import com.example.baseviewmodel.common.base.BaseVM
import com.example.baseviewmodel.common.base.ViewState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

abstract class BaseSurvivorViewModel(
    val states: MutableList<Any>,
    val savedStateHandle: SavedStateHandle
) : ViewModel(), BaseVM {
    protected val _action = MutableSharedFlow<Action>()
    val action = _action.asSharedFlow()

//    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        Action.Message(throwable.message ?: "CoroutineExceptionHandler caught An unexpected error").emit()
//    }

    init {
        states.mapIndexed { index, value -> value.createStateFlow(index) }
    }

    abstract fun onAction(action: Action)

    /** Creates MutableStateFlows, restoring data from SavedStateHandle if available */
    protected inline fun <reified T : Any> T.createStateFlow(index: Int): StateFlow<ViewState<T>> =
        (this::class.simpleName + "$index").let { savedStateHandle.getStateFlow(it, ViewState()) }

    /** saves data to SavedStateHandle */
    @Suppress("UNCHECKED_CAST")
    protected suspend inline fun <reified T : Any?> saveState(stateIndex: Int, value: T?) {
        Log.d("myLog", "saved $value")
        (states[stateIndex]::class.simpleName + "$stateIndex").let {
            savedStateHandle[it] = (savedStateHandle.get<T>(it) as ViewState<T>).copy(data = value)
        }
    }

    /** Getter functions for accessing state values from SavedStateHandle */
    fun <T : Any?> getState(stateIndex: Int): StateFlow<ViewState<T>> {
        (states[stateIndex]::class.simpleName + "$stateIndex").let {
            return savedStateHandle.getStateFlow(it, ViewState<T>())
        }
    }

    /** launches coroutine in viewModelScope. used in combination with call function to make requestCalls.
     *  also handles to emit message(or Error) and loading Actions.
     *  @see call
     * */
    protected fun launch(
        emitLoadingAction: Boolean = true,
        emitErrorMsgAction: Boolean = false,
        propagateCancellationException: Boolean = false,
        onStart: (() -> Unit)? = null,
        onFinish: (() -> Unit)? = null,
        onException: ((Exception) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch {
//            try {
//                launch(coroutineContext + coroutineExceptionHandler) {
//            launch {
            onStart?.invoke()
            if (emitLoadingAction) emitAction(Action.Loading())
            try {
                block.invoke(this)
            } catch (e: Exception) {
                onException?.invoke(e)
                if (emitLoadingAction) emitAction(Action.Loading(false))
                handleException(e, emitErrorMsgAction, propagateCancellationException)
            }
            if (emitLoadingAction) emitAction(Action.Loading(false))
            onFinish?.invoke()
//            }
//            } catch (e: Exception) {
//                onException?.invoke(e)
//                if (emitLoadingAction) Action.Loading(false).emit()
//                if (emitErrorMsgAction) Action.Message(e.message ?: "some error").emit()
//            }
        }
    }

    private fun handleException(
        e: Exception,
        emitErrorMsgAction: Boolean,
        propagateCancellationException: Boolean
    ) {
        e.printStackTrace()
        fun emitMsg(msg: String) { if (emitErrorMsgAction) Action.Message(msg).emit() }
        when (e) {
            is CancellationException -> emitMsg("Cancellation Exception")
            is IOException -> emitMsg(e.message ?: "network Error")
            else -> emitMsg(e.message ?: "some error occurred")
        }
        if (propagateCancellationException) throw CancellationException()
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
            saveState(stateIndex, response.getOrNull())
        } else {
            onError.invoke(response.exceptionOrNull())
            saveState<T>(stateIndex, null)
            response.exceptionOrNull()?.message?.let { emitAction(Action.Message(it)) }
        }
    }

    /** launches a coroutine and emits Action */
    protected fun <T : Action> T.emit() = viewModelScope.launch { _action.emit(this@emit) }

    /** use inside coroutineScope to emit Action */
    protected suspend fun emitAction(action: Action) = _action.emit(action)

    /** collects StateFlow as state and returns it's data */
    @Composable
    fun <T : Any> getData(stateIndex: Int) =
        this.getState<T>(stateIndex).collectAsStateWithLifecycle().value.data
}

