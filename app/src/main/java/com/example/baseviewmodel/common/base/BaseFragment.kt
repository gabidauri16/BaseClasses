package com.example.baseviewmodel.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.baseviewmodel.common.base.processDeathSurvivors.BaseSurvivorViewModel
import com.example.baseviewmodel.common.extensions.launchStarted
import com.example.baseviewmodel.common.extensions.takeAs

typealias Inflater<T> = (inflater: LayoutInflater, view: ViewGroup?, attach: Boolean) -> T

abstract class BaseFragment<VB : ViewBinding, VM : BaseVM>(
    private val inflater: Inflater<VB>
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
        collectAction<Action.Loading> { onLoadingAction(it.loading) }
        collectAction<Action.Message> { onMessageAction(it.msg) }
    }

    protected open fun onMessageAction(msg: String) {}
    protected open fun onLoadingAction(loading: Boolean) {}

    abstract fun setupView()

    abstract fun setupObservers()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected inline fun withBinding(block: VB.() -> Unit) = with(binding) { block(this) }
    protected inline fun withVM(block: VM.() -> Unit) = with(provideViewModel()) { block(this) }

    /** collect action by type reference */
    protected inline fun <reified T : Action> collectAction(crossinline block: (T) -> Unit) {
        withVM {
            launchStarted {
                when (this@withVM) {
                    is BaseViewModel -> {
                        action.collect { if (it is T) block(it) }
                    }

                    is BaseSurvivorViewModel -> {
                        action.collect { if (it is T) block(it) }
                    }
                }
            }
        }
    }

    /** collect nonNullable StateFlow of corresponding state by it's index */
    protected fun <T> collect(stateIndex: Int, block: T.() -> Unit) {
        withVM {
            launchStarted {
                when (this@withVM) {
                    is BaseViewModel -> {
                        stateFlowList[stateIndex].takeAs<T>()?.collect {
                            it.data?.let { dataType -> block(dataType) }
                        }
                    }

                    is BaseSurvivorViewModel -> {
                        getState<T>(stateIndex).collect { it.data?.let(block) }
                    }
                }
            }
        }
    }

    /** collect nullable StateFlow of corresponding state by its index */
    protected fun <T> collectNullable(stateIndex: Int, block: T?.() -> Unit) {
        withVM {
            launchStarted {
                when (this@withVM) {
                    is BaseViewModel -> {
                        stateFlowList[stateIndex].takeAs<T>()?.collect { block(it.data) }
                    }

                    is BaseSurvivorViewModel -> {
                        getState<T>(stateIndex).collect { block(it.data) }
                    }
                }
            }
        }
    }
}