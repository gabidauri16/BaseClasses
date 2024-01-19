package com.example.baseviewmodel.main.xmlViews

import android.widget.Toast
import com.example.baseviewmodel.common.base.BaseFragment
import com.example.baseviewmodel.databinding.FragmentFirstBinding
import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.MainActions
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
                viewModel.onAction(MainActions.GetBothDataAction)
            }
            btnSecond.setOnClickListener {
                viewModel.onAction(MainActions.GoToSecondScreenAction)
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
        collectAction<MainActions.GoToSecondScreenAction> {
            Toast.makeText(requireContext(), it::class.java.simpleName, Toast.LENGTH_SHORT).show()
//            findNavController().navigate(R.id.SecondFragment)
        }
    }
}