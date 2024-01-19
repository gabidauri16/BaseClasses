package com.example.baseviewmodel.main.xmlViews

import androidx.fragment.app.Fragment
import com.example.baseviewmodel.common.base.BaseFragment
import com.example.baseviewmodel.databinding.FragmentSecondBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment :
    BaseFragment<FragmentSecondBinding, SecondVM>(FragmentSecondBinding::inflate) {
    private val viewModel by viewModel<SecondVM>()
    override fun provideViewModel(): SecondVM = viewModel

    override fun setupView() {

    }

    override fun setupObservers() {

    }
}