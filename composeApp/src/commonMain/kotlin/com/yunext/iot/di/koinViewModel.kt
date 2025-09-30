package com.yunext.iot.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.parametersOf

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get<T>()
    }
}

@Composable
inline fun <reified T : ViewModel, reified P> koinViewModelP1(param: P): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get { parametersOf(param) }
    }
}

@Composable
inline fun <reified T : ViewModel, reified P1, reified P2> koinViewModelP2(
    param1: P1,
    param2: P2
): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get { parametersOf(param1, param2) }
    }
}

@Composable
inline fun <reified T : ViewModel, reified P1, reified P2, reified P3> koinViewModelP3(
    param1: P1,
    param2: P2,
    param3: P3
): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get { parametersOf(param1, param2, param3) }
    }
}

@Composable
inline fun <reified T : ViewModel> koinViewModelAny(
    vararg params: Any,
): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get { parametersOf(*params) }
    }
}
