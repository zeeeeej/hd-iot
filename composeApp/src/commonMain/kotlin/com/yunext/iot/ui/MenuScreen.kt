package com.yunext.iot.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunext.iot.ui.menu.MenuVO
import com.yunext.iot.ui.menu.MenuList
import com.yunext.iot.ui.menu.MenuList_DEFAULT
import com.yunext.iot.ui.menu.MenuTypeVO
import com.yunext.iot.ui.uart.ComInfoBlock
import com.yunext.iot.ui.uart.ComInfoVO
import com.yunext.iot.ui.uart.ComVO
import com.yunext.iot.ui.uart.UartComSpit

@Composable
fun MenuScreen(
    modifier: Modifier,
    selectedMenu: MenuTypeVO,
    onMenuChanged: (MenuVO) -> Unit,
    // com
    comList: List<ComVO>,
    comInfoList: List<ComInfoVO>,
    selectComDialog:Boolean,
    onComSelect: (ComVO) -> Unit,
    onComInfoAdd: () -> Unit,
    onComInfoDelete: (ComInfoVO) -> Unit,
    onComInfoConnect: (ComInfoVO) -> Unit,
    onComInfoDisconnect: (ComInfoVO) -> Unit,
    onComInfoList: () -> Unit,

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






        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 100.dp, max = 200.dp)
        ) {
            ComInfoBlock(
                modifier = Modifier,
                list = comInfoList,
                onAdd = onComInfoAdd,
                onDelete = onComInfoDelete,
                onConnect = onComInfoConnect,
                onDisconnect = onComInfoDisconnect, onList = onComInfoList)


//            // 端口
//            UartCom(
//                modifier = Modifier.fillMaxWidth()
//                    .padding(16.dp)
//                    .heightIn(min = 100.dp, max = 200.dp)
//                ,
//                list = comList,
//                com = com,
//                onSelected = onComChanged,
//                onRefresh = onRefreshCom,
//                onSwitch = onSwitchCom
//            )
        }

        AnimatedVisibility(
           selectComDialog, modifier = Modifier.padding(horizontal = 12.dp)
               .wrapContentSize()
               .align(Alignment.CenterHorizontally)
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
                   list = comList,
                   onSelect = onComSelect
               )
           }

       }

        Text("v0.0.1")
    }

}