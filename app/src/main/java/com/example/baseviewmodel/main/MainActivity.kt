package com.example.baseviewmodel.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.baseviewmodel.base.BaseActionsHandler
import com.example.baseviewmodel.base.BaseViewModel
import com.example.baseviewmodel.base.ViewState
import com.example.baseviewmodel.base.takeAs
import com.example.baseviewmodel.ui.theme.BaseViewModelTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaseViewModelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainVM = koinViewModel()

                    val firstState = remember { viewModel.stateList[0] }
                    val secondState = remember { viewModel.stateList[1] }
                    BaseActionsHandler(action = viewModel.action)
                    ScreenContent(firstState, secondState, onAction = viewModel::onAction)
                }
            }
        }
    }

    @Composable
    private fun ScreenContent(
        firstState: MutableState<ViewState<Any>>,
        secondState: MutableState<ViewState<Any>>,
        onAction: (BaseViewModel.Action) -> Unit
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting(name = firstState.takeAs<FirstModel>()?.firstData ?: "")
            Greeting(name = secondState.takeAs<SecondModel>()?.secondData ?: "")
            Button(onClick = {
                onAction(MainVM.GetBothDataAction)
            }) {
                Text(text = "CallAgain")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BaseViewModelTheme {
        Greeting("Android")
    }
}