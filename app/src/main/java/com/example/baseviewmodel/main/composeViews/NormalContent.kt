package com.example.baseviewmodel.main.composeViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.baseviewmodel.common.base.Action
import com.example.baseviewmodel.common.base.BaseActionsHandler
import com.example.baseviewmodel.common.base.ViewState
import com.example.baseviewmodel.common.extensions.takeAs
import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.MainActions
import com.example.baseviewmodel.main.MainVM
import com.example.baseviewmodel.main.SecondModel
import org.koin.androidx.compose.koinViewModel


/** usage example when screen's ViewModel is BaseViewModel */
@Composable
fun ContentWithBaseViewModel() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val viewModel: MainVM = koinViewModel()

//            val firstState = remember { viewModel.stateList[0] }
//            val secondState = remember { viewModel.stateList[1] }
        BaseActionsHandler(action = viewModel.action)
//            ScreenContent(firstState, secondState, onAction = viewModel::onAction)
    }
}

/** usage example when screen's ViewModel is BaseViewModel */
@Composable
fun ScreenContentWithBaseViewModel(
    firstState: MutableState<ViewState<Any>>,
    secondState: MutableState<ViewState<Any>>,
    onAction: (Action) -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting(name = firstState.takeAs<FirstModel>()?.firstData ?: "")
        Greeting(name = secondState.takeAs<SecondModel>()?.secondData ?: "")
        Button(onClick = { onAction(MainActions.GetBothDataAction) }) { Text(text = "CallAgain") }
    }
}