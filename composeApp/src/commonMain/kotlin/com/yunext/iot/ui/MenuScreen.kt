package com.yunext.iot.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.iot.ui.compoent.randomBG
import com.yunext.iot.ui.menu.MenuVO
import com.yunext.iot.ui.menu.MenuList
import com.yunext.iot.ui.menu.MenuList_DEFAULT
import com.yunext.iot.ui.menu.MenuTypeVO
import com.yunext.iot.ui.uart.ComVO
import com.yunext.iot.ui.uart.UartCom
import com.yunext.iot.ui.vm.ComInfoVo

@Composable
fun MenuScreen(
    modifier: Modifier,
    selectedMenu: MenuTypeVO,
    onMenuChanged: (MenuVO) -> Unit,
    // com
    comList: List<ComVO>,
    com: ComInfoVo,
    onComChanged: (ComVO) -> Unit,
    onRefreshCom: () -> Unit,
    onSwitchCom: () -> Unit

) {
    var list: List<MenuVO> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        list = MenuList_DEFAULT
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        MenuList(
            modifier = Modifier//.randomBG()
                .weight(1f)
//                .fillMaxSize()
            ,
            selected = selectedMenu,
            list = list, onMenuChanged = onMenuChanged
        )


        // 端口
        UartCom(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 100.dp, max = 200.dp)
             ,
            list = comList,
            com = com,
            onSelected = onComChanged,
            onRefresh = onRefreshCom,
            onSwitch = onSwitchCom
        )

        Text("v0.0.1")
    }

}