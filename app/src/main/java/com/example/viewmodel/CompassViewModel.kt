package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.AppSettings
import com.example.data.models.CompassData
import com.example.data.models.Direction
import com.example.data.models.SensorAccuracy
import com.example.data.preferences.SettingsRepository
import com.example.sensors.CompassSensorManager
import com.example.utils.HapticFeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CompassTab {
    COMPASS,
    VASTU_GUIDE,
    SETTINGS
}

data class CompassUiState(
    val compassData: CompassData = CompassData(),
    val appSettings: AppSettings = AppSettings(),
    val activeTab: CompassTab = CompassTab.COMPASS,
    val showCalibrationDialog: Boolean = false,
    val selectedDirectionDetail: Direction? = null,
    val targetBearing: Float? = null,
    val simulatedAngle: Float = 0f,
    val isSimulatedMode: Boolean = false
)

class CompassViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = CompassSensorManager(application)
    private val settingsRepository = SettingsRepository(application)
    private val hapticHelper = HapticFeedbackHelper(application)

    private val _uiState = MutableStateFlow(CompassUiState())
    val uiState: StateFlow<CompassUiState> = _uiState.asStateFlow()

    private var wasPointingNorth = false

    init {
        // Collect Settings
        viewModelScope.launch {
            settingsRepository.settingsFlow.collectLatest { settings ->
                _uiState.update { it.copy(appSettings = settings) }
            }
        }

        // Collect Sensor Data
        viewModelScope.launch {
            sensorManager.getCompassDataFlow()
                .catch {
                    // Fallback to simulated state on error
                    emit(
                        CompassData(
                            azimuthDegrees = 0f,
                            accuracy = SensorAccuracy.NO_SENSOR,
                            isHardwareSensor = false
                        )
                    )
                }
                .collectLatest { data ->
                    val declination = _uiState.value.appSettings.magneticDeclination
                    val adjustedAzimuth = (data.azimuthDegrees + declination + 360f) % 360f
                    val adjustedData = data.copy(azimuthDegrees = adjustedAzimuth)

                    // Check North pointing for Haptics
                    if (_uiState.value.appSettings.hapticFeedbackEnabled) {
                        if (adjustedData.isPointingNorth) {
                            if (!wasPointingNorth) {
                                hapticHelper.vibrateNorth()
                                wasPointingNorth = true
                            }
                        } else {
                            wasPointingNorth = false
                        }
                    }

                    _uiState.update { state ->
                        state.copy(
                            compassData = adjustedData,
                            isSimulatedMode = !adjustedData.isHardwareSensor
                        )
                    }
                }
        }
    }

    fun setActiveTab(tab: CompassTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun openCalibrationDialog() {
        _uiState.update { it.copy(showCalibrationDialog = true) }
    }

    fun dismissCalibrationDialog() {
        _uiState.update { it.copy(showCalibrationDialog = false) }
    }

    fun selectDirectionDetail(direction: Direction?) {
        _uiState.update { it.copy(selectedDirectionDetail = direction) }
    }

    fun toggleTargetBearingLock(target: Float? = null) {
        _uiState.update { current ->
            val newTarget = if (target != null) {
                target
            } else if (current.targetBearing != null) {
                null
            } else {
                current.compassData.azimuthDegrees
            }
            current.copy(targetBearing = newTarget)
        }
    }

    fun setSimulatedAzimuth(deg: Float) {
        val normalized = (deg % 360f + 360f) % 360f
        _uiState.update { state ->
            val simulatedData = state.compassData.copy(
                azimuthDegrees = normalized,
                accuracy = SensorAccuracy.HIGH,
                isHardwareSensor = false
            )
            state.copy(compassData = simulatedData, simulatedAngle = normalized)
        }
    }

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setKeepScreenOn(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticsEnabled(enabled)
        }
    }

    fun setShowLevel(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowLevel(enabled)
        }
    }

    fun setShowVastu(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowVastu(enabled)
        }
    }

    fun setDeclination(deg: Float) {
        viewModelScope.launch {
            settingsRepository.setDeclination(deg)
        }
    }

    fun triggerClickHaptic() {
        if (_uiState.value.appSettings.hapticFeedbackEnabled) {
            hapticHelper.vibrateClick()
        }
    }
}
