package com.yunext.iot.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.ui.uart.ComVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface ComInfoVo {
    data class Selected(val com: ComVO, val state: Boolean = false) : ComInfoVo
    data object UnSelected : ComInfoVo
}

data class HomeState(
    val comList: List<ComVO> = emptyList(),
    val com: ComInfoVo = ComInfoVo.UnSelected,
) {
    companion object {
        val EMPTY = HomeState()
    }
}

class HomeVM(private val uartRepo: UartRepository) : ViewModel() {

    private val comListFlow: MutableStateFlow<List<ComVO>> = MutableStateFlow(emptyList())
    private val comFlow: MutableStateFlow<ComInfoVo> = MutableStateFlow(ComInfoVo.UnSelected)

    val state: Flow<HomeState> = combine(comListFlow, comFlow) { comList, com ->
        HomeState(comList, com)
    }

    fun refreshCom() {
        viewModelScope.launch {
            try {
                comListFlow.value = uartRepo.list().map {
                    ComVO(it.path)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectCom(com: ComVO) {
        this.comFlow.value = ComInfoVo.Selected(com)
    }

    fun switchCom() {
        val comVO = comFlow.value
        viewModelScope.launch {
            when (comVO) {
                is ComInfoVo.Selected -> {
                    if (comVO.state) {
                        uartRepo.open(comVO.com.path,460800)
                    } else {
                        uartRepo.close(comVO.com.path)
                    }
                }

                ComInfoVo.UnSelected -> {

                }
            }
        }
    }
}