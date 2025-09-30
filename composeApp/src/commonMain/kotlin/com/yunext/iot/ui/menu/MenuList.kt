package com.yunext.iot.ui.menu

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class MenuVO(val title: String, val type: MenuTypeVO)

enum class MenuTypeVO {
    Main, Serial, Master, Setting, DEBUG
}

val MenuTypeVO.text: String
    get() = when (this) {
        MenuTypeVO.Main -> "主页"
        MenuTypeVO.Serial -> "串口协议"
        MenuTypeVO.Master -> "温控器模拟器"
        MenuTypeVO.Setting -> "设置"
        MenuTypeVO.DEBUG -> "DEBUG"
    }

val MenuList_DEFAULT by lazy {
    MenuTypeVO.entries.map {
        MenuVO(title = it.text, it)
    }
}

@Composable
internal fun MenuList(
    modifier: Modifier,
    list: List<MenuVO>,
    selected: MenuTypeVO,
    onMenuChanged: (MenuVO) -> Unit
) {

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list, { it.toString() }) { m ->
            MenuItem(
                modifier = Modifier
//                    .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp))
//                    .padding(vertical = 8.dp, horizontal = 12.dp)
                , menu = m, onSelected = {
                    onMenuChanged(m)
                }, areSelected = {
                    selected == m.type
                })
        }
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier,
    menu: MenuVO,
    onSelected: () -> Unit,
    areSelected: () -> Boolean
) {
    Box(
        modifier = modifier

            .clip(RoundedCornerShape(12.dp))
//            .drawBehind {
//                drawRoundRect(color = randomZhongGuoSe().color.copy(alpha = .5f))
//            }
            .border(
                1.dp,
                color = if (areSelected()) Color.Red else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = {
                onSelected()
            })
            .padding(vertical = 8.dp, horizontal = 12.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = menu.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (areSelected()) Color.Red else Color.Gray
            ), textAlign = TextAlign.Center
        )
    }

}