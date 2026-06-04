package com.mgpark.fridgelab.ui.camera

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** ProcessCameraProvider 준비를 코루틴으로 대기한다. */
suspend fun Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(this)
        )
    }

/** ImageCapture(JPEG) 결과 ImageProxy를 JPEG 바이트 배열로 변환한다. */
fun ImageProxy.toJpegByteArray(): ByteArray {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return bytes
}
