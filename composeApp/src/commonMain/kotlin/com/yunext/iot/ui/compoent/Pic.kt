package com.yunext.iot.ui.compoent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed

data class PicComposable(val content: @Composable (String, Boolean) -> Unit)

typealias PicItem = String

val PicItem.picRes: String
    get() = this

data class PicData(val small: List<PicItem>, val big: PicItem)

//@Composable
//fun PicLayout(
//    modifier: Modifier = Modifier,
//    data: PicData,
//    big: @Composable (PicItem) -> Unit = {
//
//    },
//    small: @Composable (PicItem) -> Unit = {
//
//    }
//) {
////    val bigC = @Composable { item: PicItem ->
////        big(item.picRes)
////    }
////    val smallsC = @Composable { item: PicItem ->
////        small(item.picRes)
////    }
////    val list = mutableStateListOf(
////        data.small.fastMapIndexed { index, s ->
////        small(s.picRes)
////    } + big(data.big.picRes)
////    )
////    Layout(contents = list, modifier = modifier) {
////        layout(layoutWidth, layoutHeight) {
////
////        }
////    }
////    Layout(contents = list,modifier = modifier) { measurables, constraints ->
////        // measurables contains one element corresponding to each of our layout children.
////        // constraints are the constraints that our parent is currently measuring us with.
////        val childConstraints =
////            Constraints(
////                minWidth = constraints.minWidth / 2,
////                minHeight = constraints.minHeight / 2,
////                maxWidth =
////                if (constraints.hasBoundedWidth) {
////                    constraints.maxWidth / 2
////                } else {
////                    Constraints.Infinity
////                },
////                maxHeight =
////                if (constraints.hasBoundedHeight) {
////                    constraints.maxHeight / 2
////                } else {
////                    Constraints.Infinity
////                }
////            )
////        // We measure the children with half our constraints, to ensure we can be double
////        // the size of the children.
////        val placeables = measurables.map { it.measure(childConstraints) }
////        val layoutWidth = (placeables.maxByOrNull { it.width }?.width ?: 0) * 2
////        val layoutHeight = (placeables.maxByOrNull { it.height }?.height ?: 0) * 2
////        // We call layout to set the size of the current layout and to provide the positioning
////        // of the children. The children are placed relative to the current layout place.
////        layout(layoutWidth, layoutHeight) {
////            placeables.forEach {
////                it.placeRelative(layoutWidth - it.width, layoutHeight - it.height)
////            }
////        }
////    }
//}