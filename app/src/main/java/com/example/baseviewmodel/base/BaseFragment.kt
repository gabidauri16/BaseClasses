package com.example.baseviewmodel.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

typealias Inflater<T> = (inflater: LayoutInflater, view: ViewGroup?, attach: Boolean) -> T

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val inflater: Inflater<VB>,
) :
    Fragment() {
    private lateinit var viewModel: VM
    abstract fun provideViewModel(): VM

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = this.inflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = provideViewModel()
        setupView()
        observeActions()
        setupObservers()
    }

    private fun observeActions() {
        collectAction<BaseViewModel.Action.Loading> { onLoadingAction(it.loading) }
        collectAction<BaseViewModel.Action.Message> { onMessageAction(it.msg) }
    }

    protected open fun onMessageAction(msg: String) {}
    protected open fun onLoadingAction(loading: Boolean) {}

    abstract fun setupView()

    abstract fun setupObservers()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun withBinding(block: VB.() -> Unit) = with(binding) { block(this) }

    /** collect action by type reference */
    protected inline fun <reified T : BaseViewModel.Action> collectAction(crossinline block: (T) -> Unit) {
        launchStarted {
            provideViewModel().action.collect {
                if (it is T) {
                    block(it as T)
                }
            }
        }
    }

    /** collect nonNullable StateFlow of corresponding state by it's index */
    protected fun <T> collect(stateIndex: Int, block: T.() -> Unit) {
        launchStarted {
            viewModel.stateFlowList[stateIndex].takeAs<T>()?.collect {
                it.data?.let { dataType -> block(dataType) }
            }
        }
    }

    /** collect nullable StateFlow of corresponding state by its index */
    protected fun <T> collectNullable(stateIndex: Int, block: T?.() -> Unit) {
        launchStarted {
            viewModel.stateFlowList[stateIndex].takeAs<T>()?.collect {
                block(it.data)
            }
        }
    }
}

/** launch in Started State */
fun LifecycleOwner.launchStarted(block: suspend CoroutineScope.() -> (Unit)): Job {
    return this.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}