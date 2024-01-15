package com.example.baseviewmodel.main.xmlViews

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.baseviewmodel.R
import com.example.baseviewmodel.base.BaseFragment
import com.example.baseviewmodel.base.BaseViewModel
import com.example.baseviewmodel.base.launchStarted
import com.example.baseviewmodel.databinding.FragmentFirstBinding
import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.MainVM
import com.example.baseviewmodel.main.SecondModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FirstFragment :
    BaseFragment<FragmentFirstBinding, MainVM>(inflater = FragmentFirstBinding::inflate) {
    private val viewModel by viewModel<MainVM>()
    override fun provideViewModel(): MainVM = viewModel

    override fun setupView() {
        withBinding {
            buttonFirst.setOnClickListener {
                viewModel.onAction(MainVM.GetBothDataAction)
            }
            btnSecond.setOnClickListener {
                viewModel.onAction(MainVM.GoToSecondScreenAction)
            }
        }
    }

    override fun setupObservers() {
        collectNullable<FirstModel>(0) {
            binding.textviewFirst.text = this?.firstData.orEmpty()
        }
        collectNullable<SecondModel>(1) {
            binding.tvSecond.text = this?.secondData.orEmpty()
        }
        collectAction<MainVM.GoToSecondScreenAction> {
            Toast.makeText(requireContext(), it::class.java.simpleName, Toast.LENGTH_SHORT).show()
//            findNavController().navigate(R.id.SecondFragment)
        }
    }
}