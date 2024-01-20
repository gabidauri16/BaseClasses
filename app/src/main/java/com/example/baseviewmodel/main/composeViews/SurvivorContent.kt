package com.example.baseviewmodel.main.composeViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.baseviewmodel.common.base.Action
import com.example.baseviewmodel.common.base.BaseActionsHandler
import com.example.baseviewmodel.main.FirstModel
import com.example.baseviewmodel.main.MainActions
import com.example.baseviewmodel.main.MainVM
import com.example.baseviewmodel.main.SecondModel
import kotlinx.parcelize.RawValue
import org.koin.androidx.compose.koinViewModel

/** usage example when screen's ViewModel is BaseSurvivorViewModel that survives process death */
@Composable
fun ContentWithBaseSurvivorViewModel() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val viewModel: MainVM = koinViewModel()

        val firstState = viewModel.getData<FirstModel>(stateIndex = 0)
        val secondState = viewModel.getData<SecondModel>(stateIndex = 1)
        BaseActionsHandler(action = viewModel.action)
        ScreenContentWithBaseSurvivorViewModel(
            firstState,
            secondState,
            onAction = viewModel::onAction
        )
    }
}

/** usage example when screen's ViewModel is BaseSurvivorViewModel that survives process death */
@Composable
fun ScreenContentWithBaseSurvivorViewModel(
    firstState: @RawValue FirstModel?,
    secondState: @RawValue SecondModel?,
    onAction: (Action) -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting(name = firstState?.firstData ?: "")
        Greeting(name = secondState?.secondData ?: "")
        Button(onClick = { onAction(MainActions.GetBothDataAction) }) { Text(text = "CallAgain") }
    }
}