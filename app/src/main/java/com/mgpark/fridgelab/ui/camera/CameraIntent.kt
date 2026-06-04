package com.mgpark.fridgelab.ui.camera

sealed interface CameraIntent {
    /** 카메라 시작/촬영 중 오류 발생. */
    data class OnError(val message: String) : CameraIntent

    /** 오류 메시지를 사용자에게 표시한 뒤 소비 처리. */
    data object OnErrorShown : CameraIntent
}
