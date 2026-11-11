package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SamplePlantData
import com.example.model.DefectCategory
import com.example.model.DefectRecord
import com.example.model.DefectSeverity
import com.example.model.InspectionChecklistItem
import com.example.model.LabTestLog
import com.example.model.LineStatus
import com.example.model.MetricState
import com.example.model.ProductionLine
import com.example.model.ToleranceParam
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FleetUiState(
    val lines: List<ProductionLine> = SamplePlantData.getInitialLines(),
    val selectedLineId: String = "EX-01",
    val filterStatus: LineStatus? = null,
    val isLiveTelemetryActive: Boolean = true,
    val checklist: Map<String, List<InspectionChecklistItem>> = mapOf(
        "EX-01" to SamplePlantData.getInitialChecklist("EX-01"),
        "EX-04" to SamplePlantData.getInitialChecklist("EX-04"),
        "RG-02" to SamplePlantData.getInitialChecklist("RG-02"),
        "SL-01" to SamplePlantData.getInitialChecklist("SL-01"),
        "SLT-03" to SamplePlantData.getInitialChecklist("SLT-03")
    ),
    val defects: List<DefectRecord> = SamplePlantData.getInitialDefects(),
    val labLogs: List<LabTestLog> = SamplePlantData.getInitialLabLogs(),
    val statusMessage: String? = null
)

class FleetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null

    init {
        startTelemetrySimulation()
    }

    val selectedLine: ProductionLine?
        get() = _uiState.value.lines.find { it.id == _uiState.value.selectedLineId }

    fun selectLine(lineId: String) {
        _uiState.update { it.copy(selectedLineId = lineId) }
    }

    fun setFilterStatus(status: LineStatus?) {
        _uiState.update { it.copy(filterStatus = status) }
    }

    fun toggleLiveTelemetry() {
        val newState = !_uiState.value.isLiveTelemetryActive
        _uiState.update { it.copy(isLiveTelemetryActive = newState) }
        if (newState) {
            startTelemetrySimulation()
        } else {
            simulationJob?.cancel()
            simulationJob = null
        }
    }

    private fun startTelemetrySimulation() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (isActive) {
                delay(3000)
                if (!_uiState.value.isLiveTelemetryActive) break

                _uiState.update { state ->
                    val updatedLines = state.lines.map { line ->
                        if (line.speedMpm > 0) {
                            // Advance meters by ~ (speed / 20)
                            val addedMeters = (line.speedMpm / 20).coerceAtLeast(1)
                            val newMeters = (line.currentLengthMeters + addedMeters)
                                .coerceAtMost(line.targetLengthMeters)

                            // Subtle sensor fluctuations on metrics
                            val updatedMetrics = line.metrics.map { param ->
                                val jitter = (Random.nextDouble(-0.2, 0.2) * 10).toInt() / 10.0
                                val newActual = ((param.actual + jitter) * 10).toInt() / 10.0
                                val updatedTraverse = if (param.traversePoints.isNotEmpty()) {
                                    param.traversePoints.map { pt ->
                                        val ptJitter = (Random.nextDouble(-0.15, 0.15) * 10).toInt() / 10.0
                                        ((pt + ptJitter) * 10).toInt() / 10.0
                                    }
                                } else param.traversePoints

                                param.copy(
                                    actual = newActual.coerceAtLeast(0.1),
                                    traversePoints = updatedTraverse
                                )
                            }

                            line.copy(
                                currentLengthMeters = newMeters,
                                metrics = updatedMetrics
                            )
                        } else {
                            line
                        }
                    }
                    state.copy(lines = updatedLines)
                }
            }
        }
    }

    fun updateMetricValue(lineId: String, metricId: String, newActual: Double) {
        _uiState.update { state ->
            val updatedLines = state.lines.map { line ->
                if (line.id == lineId) {
                    val updatedMetrics = line.metrics.map { metric ->
                        if (metric.id == metricId) {
                            metric.copy(actual = newActual)
                        } else metric
                    }
                    // Re-calculate line status based on metrics
                    val hasFail = updatedMetrics.any { it.state == MetricState.FAIL }
                    val hasWarn = updatedMetrics.any { it.state == MetricState.WARNING }
                    val newStatus = when {
                        line.status == LineStatus.HOLD_OFFLINE -> LineStatus.HOLD_OFFLINE
                        hasFail -> LineStatus.OUT_OF_SPEC
                        hasWarn -> LineStatus.CALIBRATION_WARNING
                        else -> LineStatus.IN_SPEC
                    }
                    line.copy(metrics = updatedMetrics, status = newStatus)
                } else line
            }
            state.copy(lines = updatedLines)
        }
    }

    fun toggleChecklistItem(lineId: String, itemId: String, passed: Boolean) {
        _uiState.update { state ->
            val lineList = state.checklist[lineId] ?: emptyList()
            val updated = lineList.map { item ->
                if (item.id == itemId) item.copy(passed = passed) else item
            }
            state.copy(
                checklist = state.checklist + (lineId to updated)
            )
        }
    }

    fun approveRoll(lineId: String) {
        _uiState.update { state ->
            val updatedLines = state.lines.map { line ->
                if (line.id == lineId) {
                    line.copy(
                        status = LineStatus.IN_SPEC,
                        qualityYield = 99.8,
                        lastAuditTimestamp = "JUST NOW (APPROVED)"
                    )
                } else line
            }
            state.copy(
                lines = updatedLines,
                statusMessage = "Line $lineId Roll Sign-Off Approved! CoC Certificate generated."
            )
        }
    }

    fun quarantineLot(lineId: String, reason: String) {
        val targetLine = _uiState.value.lines.find { it.id == lineId } ?: return
        val newNcr = DefectRecord(
            id = "NCR-2026-${Random.nextInt(1000, 9999)}",
            lineId = lineId,
            rollId = targetLine.activeRoll,
            batchLot = targetLine.batchLot,
            category = DefectCategory.GAUGE_BAND,
            severity = DefectSeverity.CRITICAL,
            footagePosition = targetLine.currentLengthMeters,
            totalFootage = targetLine.targetLengthMeters,
            scrapMeters = 300,
            inspectorId = targetLine.inspectorId,
            timestamp = "Just Now",
            status = "QUARANTINED",
            rootCause = reason,
            containmentAction = "Batch quarantined into Red Quarantine Bay; Production halted pending die recalibration."
        )

        _uiState.update { state ->
            val updatedLines = state.lines.map { line ->
                if (line.id == lineId) {
                    line.copy(
                        status = LineStatus.OUT_OF_SPEC,
                        speedMpm = 0,
                        activeNcrsCount = line.activeNcrsCount + 1
                    )
                } else line
            }
            state.copy(
                lines = updatedLines,
                defects = listOf(newNcr) + state.defects,
                statusMessage = "QUARANTINE ORDER ISSUED: Line $lineId halted. $newNcr.id created."
            )
        }
    }

    fun logDefect(
        lineId: String,
        category: DefectCategory,
        severity: DefectSeverity,
        footage: Int,
        scrapMeters: Int,
        rootCause: String,
        containment: String
    ) {
        val targetLine = _uiState.value.lines.find { it.id == lineId }
        val newNcr = DefectRecord(
            id = "NCR-2026-${Random.nextInt(1000, 9999)}",
            lineId = lineId,
            rollId = targetLine?.activeRoll ?: "R-01",
            batchLot = targetLine?.batchLot ?: "LOT-2026",
            category = category,
            severity = severity,
            footagePosition = footage,
            totalFootage = targetLine?.targetLengthMeters ?: 5000,
            scrapMeters = scrapMeters,
            inspectorId = targetLine?.inspectorId ?: "QC-7749",
            timestamp = "Just Now",
            status = if (severity == DefectSeverity.CRITICAL) "QUARANTINED" else "UNDER_INVESTIGATION",
            rootCause = rootCause,
            containmentAction = containment
        )

        _uiState.update { state ->
            val updatedLines = state.lines.map { line ->
                if (line.id == lineId) {
                    line.copy(
                        activeNcrsCount = line.activeNcrsCount + 1,
                        status = if (severity == DefectSeverity.CRITICAL) LineStatus.OUT_OF_SPEC else line.status
                    )
                } else line
            }
            state.copy(
                lines = updatedLines,
                defects = listOf(newNcr) + state.defects,
                statusMessage = "NCR Logged: ${newNcr.id} for $lineId."
            )
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
