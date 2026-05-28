package com.mgpark.fridgelab.ui.camera

data class CameraState(
    val isLoading: Boolean = false,
    val capturedImage: ByteArray? = null,
    val errorMessage: String? = null
)
