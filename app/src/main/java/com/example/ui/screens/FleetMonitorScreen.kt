package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LineStatus
import com.example.model.ProductionLine
import com.example.ui.components.DestructiveIndustrialButton
import com.example.ui.components.IndustrialStatusBadge
import com.example.ui.components.MetricTileWithKeyline
import com.example.ui.components.PermanentBatchHeader
import com.example.ui.components.PrimaryIndustrialButton
import com.example.ui.components.SecondaryIndustrialButton
import com.example.ui.theme.*
import com.example.viewmodel.FleetUiState

@Composable
fun FleetMonitorScreen(
    uiState: FleetUiState,
    onSelectLine: (String) -> Unit,
    onNavigateToAudit: (String) -> Unit,
    onNavigateToDefects: (String) -> Unit,
    onNavigateToLab: (String) -> Unit,
    onApproveRoll: (String) -> Unit,
    onQuarantineLot: (String, String) -> Unit,
    onFilterStatus: (LineStatus?) -> Unit,
    onToggleTelemetry: () -> Unit,
    onClearStatusMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearStatusMessage()
        }
    }

    val filteredLines = remember(uiState.lines, uiState.filterStatus) {
        if (uiState.filterStatus == null) {
            uiState.lines
        } else {
            uiState.lines.filter { it.status == uiState.filterStatus }
        }
    }

    val runningLinesCount = uiState.lines.count { it.speedMpm > 0 }
    val avgYield = if (uiState.lines.isNotEmpty()) uiState.lines.map { it.qualityYield }.average() else 0.0
    val totalOpenNcrs = uiState.lines.sumOf { it.activeNcrsCount }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("fleet_monitor_list"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Plant Telemetry Command Banner
            item {
                PlantCommandBanner(
                    runningLines = runningLinesCount,
                    totalLines = uiState.lines.size,
                    avgYield = avgYield,
                    totalNcrs = totalOpenNcrs,
                    isLive = uiState.isLiveTelemetryActive,
                    onToggleLive = onToggleTelemetry
                )
            }

            // Quick Status Filter Tabs
            item {
                StatusFilterRow(
                    currentFilter = uiState.filterStatus,
                    onFilterSelect = onFilterStatus,
                    lines = uiState.lines
                )
            }

            // Lines List
            items(filteredLines, key = { it.id }) { line ->
                LineMonitorCard(
                    line = line,
                    onCardClick = {
                        onSelectLine(line.id)
                        onNavigateToAudit(line.id)
                    },
                    onAuditClick = {
                        onSelectLine(line.id)
                        onNavigateToAudit(line.id)
                    },
                    onDefectClick = {
                        onSelectLine(line.id)
                        onNavigateToDefects(line.id)
                    },
                    onLabClick = {
                        onSelectLine(line.id)
                        onNavigateToLab(line.id)
                    },
                    onApproveClick = { onApproveRoll(line.id) },
                    onQuarantineClick = {
                        onQuarantineLot(line.id, "Inspector manual quarantine trigger on ${line.activeRoll}")
                    }
                )
            }

            // Bottom breathing space
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
fun PlantCommandBanner(
    runningLines: Int,
    totalLines: Int,
    avgYield: Double,
    totalNcrs: Int,
    isLive: Boolean,
    onToggleLive: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plant_command_banner")
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "APEX QMS",
                        fontFamily = ChivoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OnSurface,
                        letterSpacing = 0.05.sp
                    )
                    Text(
                        text = "// FLEET MONITOR",
                        fontFamily = ChivoFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = SafetyAmberLight
                    )
                }
                Text(
                    text = "High-Output Flexible Packaging Manufacturing Lines",
                    style = IndustrialTypography.LabelCode,
                    color = OutlineSlate,
                    fontSize = 10.sp
                )
            }

            // Live Telemetry Toggle Chip
            Box(
                modifier = Modifier
                    .testTag("live_telemetry_toggle")
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isLive) SurfaceContainerHigh else SurfaceContainerLowest)
                    .border(1.dp, if (isLive) PassGreenBorder else OutlineSlate, RoundedCornerShape(4.dp))
                    .clickable { onToggleLive() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isLive) Icons.Default.FiberManualRecord else Icons.Default.Pause,
                        contentDescription = "Telemetry Status",
                        tint = if (isLive) PassGreen else OutlineSlate,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = if (isLive) "LIVE TELEMETRY" else "PAUSED",
                        style = IndustrialTypography.LabelCode,
                        color = if (isLive) PassGreenText else OutlineSlate,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // High-Density Telemetry Matrices
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryStatBox(
                label = "AVG YIELD",
                value = "${"%.1f".format(avgYield)}%",
                stateColor = if (avgYield >= 98.0) PassGreenText else WarningText,
                modifier = Modifier.weight(1f)
            )
            TelemetryStatBox(
                label = "ACTIVE LINES",
                value = "$runningLines / $totalLines",
                stateColor = if (runningLines == totalLines) PassGreenText else SafetyAmberLight,
                modifier = Modifier.weight(1f)
            )
            TelemetryStatBox(
                label = "OPEN NCRS",
                value = "$totalNcrs",
                stateColor = if (totalNcrs == 0) PassGreenText else CriticalRedText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TelemetryStatBox(
    label: String,
    value: String,
    stateColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineDark, RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = IndustrialTypography.LabelCode,
            color = OutlineSlate,
            fontSize = 9.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = IndustrialTypography.DataTabularLarge,
            color = stateColor,
            fontSize = 16.sp
        )
    }
}

@Composable
fun StatusFilterRow(
    currentFilter: LineStatus?,
    onFilterSelect: (LineStatus?) -> Unit,
    lines: List<ProductionLine>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("status_filter_row"),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            FilterTabButton(
                label = "ALL (${lines.size})",
                isSelected = currentFilter == null,
                onClick = { onFilterSelect(null) }
            )
        }
        item {
            val count = lines.count { it.status == LineStatus.IN_SPEC }
            FilterTabButton(
                label = "IN-SPEC ($count)",
                isSelected = currentFilter == LineStatus.IN_SPEC,
                accentColor = PassGreen,
                onClick = { onFilterSelect(LineStatus.IN_SPEC) }
            )
        }
        item {
            val count = lines.count { it.status == LineStatus.CALIBRATION_WARNING }
            FilterTabButton(
                label = "WARNING ($count)",
                isSelected = currentFilter == LineStatus.CALIBRATION_WARNING,
                accentColor = SafetyAmber,
                onClick = { onFilterSelect(LineStatus.CALIBRATION_WARNING) }
            )
        }
        item {
            val count = lines.count { it.status == LineStatus.OUT_OF_SPEC }
            FilterTabButton(
                label = "OUT-OF-SPEC ($count)",
                isSelected = currentFilter == LineStatus.OUT_OF_SPEC,
                accentColor = CriticalRed,
                onClick = { onFilterSelect(LineStatus.OUT_OF_SPEC) }
            )
        }
        item {
            val count = lines.count { it.status == LineStatus.HOLD_OFFLINE }
            FilterTabButton(
                label = "OFFLINE ($count)",
                isSelected = currentFilter == LineStatus.HOLD_OFFLINE,
                accentColor = OutlineSlate,
                onClick = { onFilterSelect(LineStatus.HOLD_OFFLINE) }
            )
        }
    }
}

@Composable
fun FilterTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = FocusCyan
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) SurfaceContainerHigh else SurfaceContainerLowest)
            .border(
                1.dp,
                if (isSelected) accentColor else OutlineDark,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = IndustrialTypography.LabelCode,
            color = if (isSelected) OnSurface else OutlineSlate,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun LineMonitorCard(
    line: ProductionLine,
    onCardClick: () -> Unit,
    onAuditClick: () -> Unit,
    onDefectClick: () -> Unit,
    onLabClick: () -> Unit,
    onApproveClick: () -> Unit,
    onQuarantineClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("line_card_${line.id}")
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header Row: Line ID, Name, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = line.name,
                            fontFamily = ChivoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${line.type.displayName} // Recipe: ${line.recipe}",
                        style = IndustrialTypography.LabelCode,
                        color = OutlineSlate,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                IndustrialStatusBadge(status = line.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Permanent Batch and Roll Header Metadata
            PermanentBatchHeader(
                jobOrder = line.jobOrder,
                lineId = line.id,
                inspectorId = line.inspectorId,
                rollNumber = line.activeRoll,
                batchLot = line.batchLot,
                clientName = line.clientName
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Production Speed & Footage Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Line Speed",
                        tint = SafetyAmberLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SPEED: ${line.speedMpm} mpm",
                        style = IndustrialTypography.DataTabular,
                        color = OnSurface
                    )
                    Text(
                        text = "(Target: ${line.targetMpm})",
                        style = IndustrialTypography.LabelCode,
                        color = OutlineSlate
                    )
                }

                Text(
                    text = "${line.currentLengthMeters}m / ${line.targetLengthMeters}m",
                    style = IndustrialTypography.DataTabular,
                    color = SafetyAmberLight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Linear Progress Indicator for Roll Footage
            val progress = (line.currentLengthMeters.toFloat() / line.targetLengthMeters.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = when (line.status) {
                    LineStatus.IN_SPEC -> PassGreen
                    LineStatus.CALIBRATION_WARNING -> SafetyAmber
                    LineStatus.OUT_OF_SPEC -> CriticalRed
                    LineStatus.HOLD_OFFLINE -> OutlineSlate
                },
                trackColor = SurfaceContainerLowest
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Parameter Metric Tiles
            Text(
                text = "IN-LINE QC TELEMETRY",
                style = IndustrialTypography.LabelCode,
                color = OutlineSlate
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Show primary 2 metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (line.metrics.isNotEmpty()) {
                    MetricTileWithKeyline(
                        param = line.metrics[0],
                        modifier = Modifier.weight(1f),
                        onClick = onAuditClick
                    )
                }
                if (line.metrics.size > 1) {
                    MetricTileWithKeyline(
                        param = line.metrics[1],
                        modifier = Modifier.weight(1f),
                        onClick = onAuditClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Direct-Action Buttons Row conforming to design specs:
            // "Approve Roll", "Quarantine Lot", "Audit Roll", "Log Defect"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryIndustrialButton(
                    text = "Audit Roll",
                    onClick = onAuditClick,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_audit_roll_${line.id}"
                )

                if (line.status == LineStatus.OUT_OF_SPEC || line.activeNcrsCount > 0) {
                    DestructiveIndustrialButton(
                        text = "Quarantine",
                        onClick = onQuarantineClick,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_quarantine_${line.id}"
                    )
                } else {
                    PrimaryIndustrialButton(
                        text = "Approve Roll",
                        onClick = onApproveClick,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_approve_roll_${line.id}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Quick Actions (NCR / Lab Drilldown)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onDefectClick)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = "NCRs",
                        tint = if (line.activeNcrsCount > 0) CriticalRedText else OutlineSlate,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "NCRS: ${line.activeNcrsCount} LOGGED ➔",
                        style = IndustrialTypography.LabelCode,
                        color = if (line.activeNcrsCount > 0) CriticalRedText else OutlineSlate
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable(onClick = onLabClick)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = "Lab Log",
                        tint = FocusCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "LAB RECORDS ➔",
                        style = IndustrialTypography.LabelCode,
                        color = FocusCyan
                    )
                }
            }
        }
    }
}
