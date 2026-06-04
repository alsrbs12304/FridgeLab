package com.mgpark.fridgelab.ui.camera

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state: StateFlow<CameraState> = _state.asStateFlow()

    fun handleIntent(intent: CameraIntent) {
        when (intent) {
            is CameraIntent.OnError ->
                _state.update { it.copy(errorMessage = intent.message) }

            CameraIntent.OnErrorShown ->
                _state.update { it.copy(errorMessage = null) }
        }
    }
}
