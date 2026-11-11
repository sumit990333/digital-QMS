package com.example.model

enum class LineStatus(val title: String) {
    IN_SPEC("PASS / IN-SPEC"),
    CALIBRATION_WARNING("CALIBRATION / WARNING"),
    OUT_OF_SPEC("CRITICAL FAILURE / OUT-OF-SPEC"),
    HOLD_OFFLINE("HOLD / OFFLINE")
}

enum class LineType(val displayName: String) {
    BLOWN_FILM("Blown Film Extrusion"),
    CAST_FILM("Cast Film Extrusion"),
    ROTOGRAVURE("10-Color Rotogravure Press"),
    SOLVENTLESS_LAMINATION("Solventless Laminator"),
    SLITTER_REWINDER("Turret Slitter-Rewinder")
}

enum class MetricState {
    PASS,
    WARNING,
    FAIL
}

data class ToleranceParam(
    val id: String,
    val name: String,
    val actual: Double,
    val target: Double,
    val tolerance: Double, // e.g. 2.0 for +-2.0, or percentage
    val unit: String,
    val isPercentageTolerance: Boolean = false,
    val isLowerBoundOnly: Boolean = false, // e.g. >= 15.0
    val standardRef: String,
    val traversePoints: List<Double> = emptyList() // 5-point cross-web profile
) {
    val state: MetricState
        get() {
            if (isLowerBoundOnly) {
                return when {
                    actual >= target -> MetricState.PASS
                    actual >= target * 0.92 -> MetricState.WARNING
                    else -> MetricState.FAIL
                }
            }
            val allowedDelta = if (isPercentageTolerance) (target * tolerance / 100.0) else tolerance
            val delta = kotlin.math.abs(actual - target)
            return when {
                delta <= allowedDelta -> MetricState.PASS
                delta <= allowedDelta * 1.5 -> MetricState.WARNING
                else -> MetricState.FAIL
            }
        }

    val varianceText: String
        get() {
            val diff = actual - target
            val sign = if (diff >= 0) "+${"%.1f".format(diff)}" else "${"%.1f".format(diff)}"
            val pct = if (target > 0) " (${if (diff >= 0) "+" else ""}${"%.1f".format((diff / target) * 100)}%)" else ""
            return "$sign $unit$pct"
        }

    val displayTarget: String
        get() {
            return if (isLowerBoundOnly) {
                "≥ ${"%.1f".format(target)} $unit"
            } else if (isPercentageTolerance) {
                "${"%.1f".format(target)} $unit (±${"%.0f".format(tolerance)}%)"
            } else {
                "${"%.1f".format(target)} $unit (±${"%.1f".format(tolerance)})"
            }
        }

    val displayActual: String
        get() = "${"%.1f".format(actual)} $unit"
}

data class ProductionLine(
    val id: String,
    val name: String,
    val type: LineType,
    val status: LineStatus,
    val jobOrder: String,
    val clientName: String,
    val activeRoll: String,
    val batchLot: String,
    val inspectorId: String,
    val recipe: String,
    val speedMpm: Int,
    val targetMpm: Int,
    val currentLengthMeters: Int,
    val targetLengthMeters: Int,
    val qualityYield: Double,
    val oee: Double,
    val activeNcrsCount: Int,
    val metrics: List<ToleranceParam>,
    val lastAuditTimestamp: String
)

data class InspectionChecklistItem(
    val id: String,
    val label: String,
    val standard: String,
    val passed: Boolean,
    val notes: String = ""
)

enum class DefectSeverity(val label: String) {
    CRITICAL("CRITICAL / HALT"),
    MAJOR("MAJOR / SCRAP"),
    MINOR("MINOR / SLIT_FLAG")
}

enum class DefectCategory(val displayName: String, val code: String) {
    DIE_LINES("Die Lines / Tooling Drag", "DEF-DL"),
    GELS_FISHEYES("Gels & Fish-Eyes", "DEF-GEL"),
    GAUGE_BAND("Gauge Band / Ridge", "DEF-GB"),
    WRINKLES_TENSION("Wrinkles & Tension Wander", "DEF-WR"),
    DELAMINATION("Delamination / Bond Fault", "DEF-DEL"),
    INK_SMEAR_PINHOLES("Ink Smear & Micro Pinholes", "DEF-INK"),
    DYNE_DECAY("Corona Discharge Decay", "DEF-DYN")
}

data class DefectRecord(
    val id: String,
    val lineId: String,
    val rollId: String,
    val batchLot: String,
    val category: DefectCategory,
    val severity: DefectSeverity,
    val footagePosition: Int,
    val totalFootage: Int,
    val scrapMeters: Int,
    val inspectorId: String,
    val timestamp: String,
    val status: String, // "QUARANTINED", "UNDER_INVESTIGATION", "SCRAPPED", "RESOLVED"
    val rootCause: String,
    val containmentAction: String
)

data class LabTestLog(
    val sampleId: String,
    val jobOrder: String,
    val rollId: String,
    val lineId: String,
    val testName: String,
    val standard: String,
    val measuredValue: String,
    val targetSpec: String,
    val delta: String,
    val result: MetricState,
    val technician: String,
    val timestamp: String
)
