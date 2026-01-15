package com.yunext.iot.ui.workspace

import ZhongGuoSe
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import color
import com.yunext.iot.di.koinViewModel
import com.yunext.iot.domain.project.HDProject
import com.yunext.iot.domain.protocol.HDProtocolCmd
import com.yunext.iot.domain.protocol.PicInfo
import com.yunext.iot.ui.protocol.AppOTASender
import com.yunext.iot.ui.protocol.ExtraPropertyGetSender
import com.yunext.iot.ui.protocol.ExtraPropertyVo
import com.yunext.iot.ui.protocol.HeartBeatSender
import com.yunext.iot.ui.protocol.PicDeleteSender
import com.yunext.iot.ui.protocol.PicInfoSender
import com.yunext.iot.ui.protocol.PicPullSender
import com.yunext.iot.ui.protocol.PicSnapSender
import com.yunext.iot.ui.protocol.ProjectVO
import com.yunext.iot.ui.protocol.PropertyGetSender
import com.yunext.iot.ui.protocol.PropertyVo
import com.yunext.iot.ui.protocol.ProtocolVO
import com.yunext.iot.ui.protocol.SystemOTASender
import com.yunext.iot.ui.protocol.defaultAddress
import com.yunext.iot.ui.protocol.listExtraProperty
import com.yunext.iot.ui.protocol.listProperty
import com.yunext.iot.ui.vm.SerialProtocolVM
import kotlinx.coroutines.launch

typealias SerialProtocolScreenAction = (ProtocolVO, ByteArray) -> Unit
typealias SerialProtocolScreenAppOTAAction = (ProtocolVO, ByteArray) -> Unit

/**
 * Menu-串口协议
 */
@Composable
fun SerialProtocolScreen(modifier: Modifier, onSend: SerialProtocolScreenAction,
                         onSendAppOta: SerialProtocolScreenAppOTAAction
                         ) {
    val viewModel = koinViewModel<SerialProtocolVM>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedProject: ProjectVO by rememberSaveable(/*stateSaver = ProjectVOMapSaver*/) {
        mutableStateOf(ProjectVO.EMPTY)
    }

    val defaultAddress: UByte by remember(selectedProject) {
        derivedStateOf {
            selectedProject.proj.defaultAddress
        }
    }

    var editAddress: UByte by remember() { mutableStateOf(0.toUByte()) }

    var selectedProtocol: ProtocolVO by rememberSaveable(/*stateSaver = ProtocolVOMapSaver*/) {
        mutableStateOf(ProtocolVO.EMPTY)
    }

    val address: UByte by remember(defaultAddress, editAddress) {
        derivedStateOf {
            if (editAddress == 0.toUByte()) defaultAddress else editAddress
        }
    }

    LaunchedEffect(Unit) {
        if (selectedProject == ProjectVO.EMPTY) {
            viewModel.listProject()
        }
    }
    LaunchedEffect(state.projectList) {
        if (selectedProject == ProjectVO.EMPTY) {
            if (state.projectList.isNotEmpty()) {
                selectedProject = state.projectList[0]
                viewModel.listProtocol(state.projectList[0])
            }
        }
    }

    Box(modifier = Modifier/*.background(ZhongGuoSe.月白.color)*/.padding(16.dp)) {

        Column {
            ProjectList(modifier = Modifier.fillMaxWidth(), onSelected = {
                val key = selectedProject
                if (key == ProjectVO.EMPTY) {
                    selectedProtocol = ProtocolVO.EMPTY
                    selectedProject = it
                    editAddress = 0.toUByte()
                    viewModel.listProtocol(it)
                } else {
                    if (key.key == it.key) {
                        return@ProjectList
                    } else {
                        selectedProtocol = ProtocolVO.EMPTY
                        editAddress = 0.toUByte()
                        selectedProject = it
                        viewModel.listProtocol(it)
                    }
                }
            }, list = state.projectList, selected = { it.key == selectedProject.key })

            ProtocolList(
                modifier = Modifier.fillMaxWidth().weight(1f),
                onSelectProtocol = { p, _ ->
//                    val old = selectedProtocol
//                    if (old == ProtocolVO.EMPTY) {
//                        selectedProtocol = p
//                    } else {
//                        if (old.cmd != p.cmd) {
//                            selectedProtocol = p
//
//                        } else {
//                            selectedProtocol = ProtocolVO.EMPTY
//                        }
//                    }
                },
                list = state.protocolList,
                selected = { it.cmd == selectedProtocol.cmd },
                address = address,
                onSend = onSend,
                onSendAppOta = onSendAppOta,
            )


        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {

            Text(" - ", Modifier.clickable {
                editAddress--
            })
            Text("address:0x")
            Text(address.toHexString())
            Text(" + ", Modifier.clickable {
                editAddress++
            })
        }
    }
}

@Composable
private fun ProjectList(
    modifier: Modifier,
    list: List<ProjectVO>,
    selected: (ProjectVO) -> Boolean,
    onSelected: (ProjectVO) -> Unit
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(list, {
            it.key
        }) {
            ProjectItem(
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 12.dp)
                    .clickable {
                        onSelected(it)
                    }, it, selected = selected(it)
            )
        }
    }
}

@Composable
private fun ProjectItem(modifier: Modifier, projectVO: ProjectVO, selected: Boolean) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            projectVO.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) ZhongGuoSe.合欢红.color else ZhongGuoSe.橄榄绿.color,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun ProtocolList(
    modifier: Modifier,
    list: List<ProtocolVO>,
    selected: (ProtocolVO) -> Boolean,
    address: UByte,
    onSelectProtocol: (ProtocolVO, ByteArray) -> Unit,
    onSend: SerialProtocolScreenAction,
    onSendAppOta: SerialProtocolScreenAppOTAAction
) {
//    var expandedItemId by remember { mutableStateOf<UByte?>(null) }
    var shows: Set<UByte> by remember {
        mutableStateOf(emptySet())
    }

    var picInfos: List<PicInfo> by remember {
        mutableStateOf(emptyList())
    }


    val rememberCoroutineScope = rememberCoroutineScope()

//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        contentPadding = PaddingValues(8.dp)
//    ) {
//
//        items(list, { it.cmd }, span = { item ->
//
//            if (item.cmd in shows) {
//                GridItemSpan(2)
//            } else {
//                GridItemSpan(1)
//            }
//        }) {
//            ProtocolItem(
//                modifier = Modifier.fillMaxWidth(),
//                item = it,
//                selected = { it.cmd in shows },
//                address = address,
//                onSendAppFile = {
//                    data->
//                    onSendAppOta.invoke(it, data)
//                },
//                onSend = { data ->
//                    onSend.invoke(it, data)
//                }, onItemSelected = {
////                    if (expandedItemId == null || expandedItemId != it.cmd) {
////                        expandedItemId = it.cmd
////                    } else {
////                        expandedItemId = null
////                    }
////                    onSelectProtocol(it, byteArrayOf(0))
//                    rememberCoroutineScope.launch {
//                        if (it.cmd in shows) {
//                            shows = shows - it.cmd
//                        } else {
//                            shows = shows + it.cmd
//                        }
//                    }
//                }, onParser = { infos ->
//                    picInfos = infos
//                }, picInfos = picInfos
//            )
//        }
//    }

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list, {
            it.cmd
        }) {
            ProtocolItem(
                modifier = Modifier.fillMaxWidth(),
                item = it,
                selected = { it.cmd in shows },
                address = address,
                onSendAppFile = {
                        data->
                    onSendAppOta.invoke(it, data)
                },
                onSend = { data ->
                    onSend.invoke(it, data)
                }, onItemSelected = {
//                    if (expandedItemId == null || expandedItemId != it.cmd) {
//                        expandedItemId = it.cmd
//                    } else {
//                        expandedItemId = null
//                    }
//                    onSelectProtocol(it, byteArrayOf(0))
                    rememberCoroutineScope.launch {
                        if (it.cmd in shows) {
                            shows = shows - it.cmd
                        } else {
                            shows = shows + it.cmd
                        }
                    }
                }, onParser = { infos ->
                    picInfos = infos
                }, picInfos = picInfos
            )
        }
    }
}

private val Shape by lazy {
    RoundedCornerShape(2.dp)
}

/**
 * [onItemSelected]Deprecated
 */
@Composable
private fun ProtocolItem(
    modifier: Modifier, item: ProtocolVO,
    selected: () -> Boolean, address: UByte, onSend: (ByteArray) -> Unit,
    onSendAppFile: (ByteArray) -> Unit,
    onItemSelected: () -> Unit,
    picInfos:List<PicInfo>,
    onParser: (List<PicInfo>) -> Unit
) {
    val selectedReal by remember(selected) {
        derivedStateOf(selected)
    }

    Box(
        modifier
            .shadow(
                2.dp,
                Shape,
                ambientColor = ZhongGuoSe.金叶黄.color,
                spotColor = ZhongGuoSe.橄榄绿.color
            )
            .background(ZhongGuoSe.月白.color, Shape)
            .clip(Shape)

            .padding(12.dp), contentAlignment = Alignment.CenterStart

    ) {
        Column() {
            Column(modifier = Modifier.fillMaxWidth().clickable {
//                hide = !hide

                onItemSelected()
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.cmdStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedReal) ZhongGuoSe.墨紫.color else ZhongGuoSe.橄榄绿.color,
                        fontWeight = if (selectedReal) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 18.sp
                    )
                    Text(
                        item.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedReal) ZhongGuoSe.墨紫.color else ZhongGuoSe.橄榄绿.color,
                        fontWeight = if (selectedReal) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 18.sp
                    )
                }
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Normal,
                    text = item.desc,
                    fontSize = 13.sp,
                )
            }

            AnimatedVisibility(
                modifier = Modifier.border(1.dp, color = Color.Gray),
                visible = selectedReal
            ) {
                when (item.cmdReal) {
                    HDProtocolCmd.HeartBeat -> {
                        HeartBeatSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            address = address,
                            ack = 0x00.toUByte(), onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame
                        )
                    }

                    HDProtocolCmd.PropertyGet -> {
                        val propertyList:List<PropertyVo> by remember(item.proj) { mutableStateOf(  item.proj.listProperty()) }

                        PropertyGetSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame,
                            propertyList = propertyList
                        )
                    }

                    HDProtocolCmd.PropertySet -> {}
                    HDProtocolCmd.FactoryReset -> {}
                    HDProtocolCmd.Reboot -> {}
                    HDProtocolCmd.PicSnap06 -> {
                        PicSnapSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            cmd = item.cmdReal,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame
                        )
                    }

                    HDProtocolCmd.PicSnapC6 -> {
                        PicSnapSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            cmd = item.cmdReal,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame
                        )
                    }

                    HDProtocolCmd.PicSnap16 -> {
                        PicSnapSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            cmd = item.cmdReal,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame
                        )

                    }

                    HDProtocolCmd.PicInfo07, HDProtocolCmd.PicInfoC7, HDProtocolCmd.PicInfo17 -> {
                        PicInfoSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            cmd = item.cmdReal,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame, onParser = onParser
                        )
                    }


                    HDProtocolCmd.PicDelete08,HDProtocolCmd.PicDeleteC8,HDProtocolCmd.PicDelete18 -> {
                        PicDeleteSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            cmd = item.cmdReal,
                            picInfos = picInfos,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame
                        )
                    }
                    HDProtocolCmd.PicPull09, HDProtocolCmd.PicPull19, HDProtocolCmd.PicPullC9 -> {
                        PicPullSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            cmd = item.cmdReal,
                            picInfos = picInfos,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame
                        )
                    }

                    HDProtocolCmd.PicPullComplete0A -> {}
                    HDProtocolCmd.PicPullCompleteCA -> {}
                    HDProtocolCmd.PicPullComplete1A -> {}
                    HDProtocolCmd.DoorState -> {}
                    HDProtocolCmd.SystemOTANotice -> {
                        SystemOTASender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame, onSendAppFile = onSendAppFile
                        )
                    }
                    HDProtocolCmd.SystemOTA -> {}
                    HDProtocolCmd.AppOTANotice -> {
                        AppOTASender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame, onSendAppFile = onSendAppFile
                        )
                    }
                    HDProtocolCmd.AppOTA -> {
//                        AppOTASender(
//                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
//                            address = address,
//                            onSend = {
//                                onSend(it)
//                            }, output = item.action.outputFrame, onSendAppFile = onSendAppFile
//                        )
                    }
                    HDProtocolCmd.ExtraPropertyGet -> {
                        val propertyList:List<ExtraPropertyVo> by remember(item.proj) { mutableStateOf(  item.proj.listExtraProperty()) }

                        ExtraPropertyGetSender(
                            modifier = Modifier.fillMaxWidth()/*.height(240.dp)*/,
                            address = address,
                            onSend = {
                                onSend(it)
                            }, output = item.action.outputFrame,
                            propertyList = propertyList
                        )
                    }
                    HDProtocolCmd.ExtraPropertySet -> {}
                    HDProtocolCmd.RandomAddressBroadcast -> {}
                    HDProtocolCmd.ValidateRandomAddressBroadcast -> {}
                    HDProtocolCmd.GetDirection -> {}
                    HDProtocolCmd.CheckDirectionBroadcast -> {}
                    HDProtocolCmd.DoorStatusBroadcastCE -> {}
                    HDProtocolCmd.DoorStatusBroadcastCD -> {}
                    HDProtocolCmd.Event -> {}
                    HDProtocolCmd.PUSH -> {}
                    HDProtocolCmd.PUSHNotice -> {}
                }

            }
        }
    }
}
