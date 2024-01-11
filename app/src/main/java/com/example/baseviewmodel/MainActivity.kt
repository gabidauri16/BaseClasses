package com.example.baseviewmodel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baseviewmodel.ui.theme.BaseViewModelTheme
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
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
                    val loading = remember { mutableStateOf(false) }
                    val context = LocalContext.current

                    val firstState = remember { viewModel.stateList[0] }
                    val secondState = remember { viewModel.stateList[1] }
                    val message = remember { mutableStateOf("") }
                    val messageVisibility = remember { mutableStateOf(false) }
                    LaunchedEffect(key1 = true) {
                        viewModel.defaultAction.consumeEach {
                            Log.d("emissionss", it.toString())
                            when (it) {
                                is BaseViewModel.DefaultAction.Message -> {
                                    message.value = it.msg
                                    messageVisibility.value = true
//                                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT)
//                                        .show()
                                }

                                is BaseViewModel.DefaultAction.Loading -> {
                                    loading.value = it.loading
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
//                        if (loading.value) {
//                            Box {
                        AnimatedVisibility(
                            visible = loading.value,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 200.dp)
                            )
                        }
                        AnimatedVisibility(visible = messageVisibility.value) {
                            Text(text = message.value, modifier = Modifier.align(Alignment.Center))
                            Thread.sleep(1000)
                            messageVisibility.value = false
                        }
                        ScreenContent(firstState, secondState, viewModel)
//                            }
//                        } else {
//                            ScreenContent(firstState, secondState, viewModel)
//                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ScreenContent(
        firstState: MutableStateFlow<ViewState<Any>>,
        secondState: MutableStateFlow<ViewState<Any>>,
        viewModel: MainVM
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val loading = remember { mutableStateOf(false) }
            val firstString = remember { mutableStateOf("") }
            LaunchedEffect(key1 = true) {
                firstState.value.takeAsStateFlowOf<FirstModel>()?.collect {
                    firstString.value = it.data?.firstData ?: ""
                    loading.value = it.loading
                }
                // todo: show loader until all the states stop loading
                // todo: also make it closable to show loader or not
                // todo: create BaseScreen
            }
            Greeting(
//                name = firstState.value.takeAsStateOf<FirstModel>()?.firstData
//                    ?: ""
                name = firstString.value ?: ""
            )
            Greeting(
                name = secondState.value.takeAsStateOf<SecondModel>()?.secondData
                    ?: ""
            )
            Button(onClick = {
                viewModel.getFirstData()
                viewModel.getSecondData()
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