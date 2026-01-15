package com.yunext.iot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yunext.iot.ui.compoent.Effect
import com.yunext.iot.ui.menu.MenuTypeVO
import com.yunext.iot.ui.protocol.ProtocolVO
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.workspace.DebugScreen
import com.yunext.iot.ui.workspace.MainScreen
import com.yunext.iot.ui.workspace.MasterScreen
import com.yunext.iot.ui.workspace.SerialProtocolScreen
import com.yunext.iot.ui.workspace.SerialProtocolScreenAction
import com.yunext.iot.ui.workspace.SerialProtocolScreenAppOTAAction
import com.yunext.iot.ui.workspace.SettingScreen

/**
 * 工作部分
 */
@Composable
fun WorkSpace(
    modifier: Modifier,
    menuType: MenuTypeVO,
    comInfoList:List<ComInfoVO>,
    receiveData: String,
    sendEffect: Effect<String, ByteArray>,
    onUartSend: (ComInfoVO, String) -> Unit,
    onUartSendByteArray: SerialProtocolScreenAction,
    onUartSendAppOTAByteArray: SerialProtocolScreenAppOTAAction,
    onUartConnect: (ComInfoVO) -> Unit,
    onUartDisconnect: (ComInfoVO) -> Unit,
    onEditRate: (ComInfoVO) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()
    LaunchedEffect(menuType) {
        when (menuType) {
            MenuTypeVO.Main -> navController.navigate(menuType.name)
            MenuTypeVO.Serial -> navController.navigate(menuType.name)
            MenuTypeVO.Master -> navController.navigate(menuType.name)
            MenuTypeVO.Setting -> navController.navigate(menuType.name)
            MenuTypeVO.DEBUG -> navController.navigate(menuType.name)
        }
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MenuTypeVO.Main.name
    ) {
        composable(route = MenuTypeVO.Main.name) {
            MainScreen(
                Modifier,
                receiveData = receiveData,
                onSend = onUartSend,
                onConnect = onUartConnect,
                onDisconnect = onUartDisconnect,
                comInfoList = comInfoList,
                sendEffect = sendEffect,
                onEditRate = onEditRate
            )
        }
        composable(route = MenuTypeVO.Serial.name) {
            SerialProtocolScreen(Modifier, onSend = {p,d->
                onUartSendByteArray.invoke(p,d)
            }, onSendAppOta = {p,d->
                onUartSendAppOTAByteArray.invoke(p,d)
            })
        }
        composable(route = MenuTypeVO.Master.name) {
            MasterScreen(Modifier)
        }
        composable(route = MenuTypeVO.Setting.name) {
            SettingScreen(Modifier)
        }

        composable(route = MenuTypeVO.DEBUG.name) {
            DebugScreen(Modifier)
        }
    }

}