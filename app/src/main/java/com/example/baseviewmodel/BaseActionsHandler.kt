package com.example.baseviewmodel

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow

/** handles showing message and showing loader. */
@Composable
fun BaseActionsHandler(
    action: SharedFlow<BaseViewModel.Action>,
    showLoader: Boolean = true
) {
    val context = LocalContext.current
    val loadingState = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        action.collect {
            when (it) {
                is BaseViewModel.Action.Message -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                is BaseViewModel.Action.Loading -> {
                    if (showLoader) loadingState.value = it.loading

                }
            }
        }
    }
    Loader(loading = loadingState.value)
}

@Composable
fun Loader(loading: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = loading,
            modifier = Modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 200.dp)
            )
        }
    }
}