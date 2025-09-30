package com.yunext.iot.ui.work

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yunext.iot.ui.compoent.randomBG
import com.yunext.iot.ui.menu.MenuTypeVO

@Composable
fun DebugScreen(modifier: Modifier){
    Box(modifier = Modifier.randomBG()){
        Text("DebugScreen")
    }
}