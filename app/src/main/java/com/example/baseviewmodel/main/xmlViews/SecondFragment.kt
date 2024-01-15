package com.example.baseviewmodel.main.xmlViews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.baseviewmodel.R
import com.example.baseviewmodel.base.BaseFragment
import com.example.baseviewmodel.databinding.FragmentSecondBinding
import com.example.baseviewmodel.main.MainVM
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