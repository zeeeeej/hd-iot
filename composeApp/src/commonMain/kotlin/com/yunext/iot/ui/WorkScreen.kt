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
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.work.DebugScreen
import com.yunext.iot.ui.work.MainScreen
import com.yunext.iot.ui.work.MasterScreen
import com.yunext.iot.ui.work.SerialScreen
import com.yunext.iot.ui.work.SettingScreen

@Composable
fun WorkScreen(
    modifier: Modifier,
    menuType: MenuTypeVO,
    comInfoList:List<ComInfoVO>,
    receiveData: String,
    sendEffect: Effect<String, ByteArray>,
    onUartSend: (ComInfoVO, String) -> Unit,
    onUartConnect: (ComInfoVO) -> Unit,
    onUartDisconnect: (ComInfoVO) -> Unit,
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
            )
        }
        composable(route = MenuTypeVO.Serial.name) {
            SerialScreen(Modifier)
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