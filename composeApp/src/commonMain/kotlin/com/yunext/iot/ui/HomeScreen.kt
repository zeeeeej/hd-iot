package com.yunext.iot.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunext.iot.di.koinViewModel
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.compoent.Toast
import com.yunext.iot.ui.menu.MenuTypeVO
import com.yunext.iot.ui.vm.HomeState
import com.yunext.iot.ui.vm.HomeVM
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeVM>()
    val state by viewModel.state.collectAsState(HomeState.EMPTY)
    var selectedMenu: MenuTypeVO by remember {
        mutableStateOf(MenuTypeVO.Main)
    }

    var selectComDialog: Boolean by remember {
        mutableStateOf(false)
    }

    var toast by remember { mutableStateOf("") }
    LaunchedEffect(state.globalToastEffect) {
        launch {
            snapshotFlow {
                state.globalToastEffect
            }.distinctUntilChanged()
                .collect {
                    toast = when (val effect = state.globalToastEffect) {
                        Effect.Completed -> ""
                        is Effect.Fail<*> -> ""
                        Effect.Idle -> ""
                        is Effect.Progress<*, *> -> effect.input.toString()
                        is Effect.Success<*, *> -> ""
                    }
                }
        }
    }

    Box(
        modifier = modifier
        //.border(1.dp, color = Color.Red)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
            //.border(1.dp, color = Color.Red)
        ) {
            Box(
                Modifier.width(384.dp).fillMaxHeight()
                    .border(1.dp, color = Color.Gray)
            ) {
                MenuScreen(
                    modifier = Modifier.fillMaxSize(),
                    selectedMenu = selectedMenu,
                    onMenuChanged = {
                        selectedMenu = it.type
                    },
                    comList = state.comList,
                    comInfoList = state.comInfoList,
                    onComSelect = {
                        viewModel.addComInfo(it.path)
                        selectComDialog = false
                    },
                    onComInfoList = {
                        viewModel.listComInfo()
                    },
                    onComInfoAdd = {
                        viewModel.refreshCom()
                        selectComDialog = true
                    },
                    onComInfoDisconnect = {
                        viewModel.disconnectComInfo(it.path)
                    },
                    onComInfoConnect = {
                        viewModel.connectComInfo(it.path)
                    },
                    onComInfoDelete = {
                        viewModel.deleteComInfo(it)
                    },
                    selectComDialog = selectComDialog,
                )
            }

            Spacer(Modifier.width(32.dp))

            Column(
                Modifier.weight(1f).fillMaxHeight()
                //.border(1.dp, color = Color.Gray)
            ) {
                Box(
                    modifier = Modifier
//                        .background(Color.Blue)
//                        .height(450.dp)
//                        .weight(1f)
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .border(1.dp, color = Color.Green)

                ) {
                    WorkScreen(
                        Modifier,
                        menuType = selectedMenu,
                        receiveData = state.receiveData,
                        comInfoList = state.comInfoList,
                        onUartSend = { info, data ->
                            viewModel.send(info, data)
                        },
                        onUartDisconnect = {
                            viewModel.disconnectComInfo(it.path)
                        },
                        onUartConnect = {
                            viewModel.connectComInfo(it.path)
                        }, sendEffect = state.sendUartDataEffect
                    )
                }

                Spacer(Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
//                        .height(150.dp)
                        .fillMaxWidth().border(1.dp, color = Color.Blue)
                ) {
                    LogcatScreen(Modifier)
                }
            }
        }


        /*AnimatedVisibility(
            selectComDialog, modifier = Modifier
                .wrapContentSize()
                .aspectRatio(16 / 9f)
                .align(Alignment.Center)
        ) {
            Box(
                Modifier.clip(RoundedCornerShape(16.dp))
                    .shadow(4.dp)
                    .background(Color.White)
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                // 端口
                UartComSpit(
                    modifier = Modifier,
                    list = state.comList,
                    onSelect = {
                        viewModel.addComInfo(it.path)
                        selectComDialog = false
                    }
                )
            }

        }*/



        Toast(
            Modifier
                .wrapContentSize()
//                .heightIn(min = 90.dp, max = 200.dp)
                .aspectRatio(16 / 9f)
                .align(Alignment.Center), toast
        ) {
            viewModel.clearToast()
        }


    }
}