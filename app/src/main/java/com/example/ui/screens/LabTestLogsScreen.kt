package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LabTestLog
import com.example.model.MetricState
import com.example.ui.components.IndustrialStatusBadge
import com.example.ui.components.MetricStateChip
import com.example.ui.components.PrimaryIndustrialButton
import com.example.ui.components.SecondaryIndustrialButton
import com.example.ui.theme.*
import com.example.viewmodel.FleetUiState

@Composable
fun LabTestLogsScreen(
    uiState: FleetUiState,
    initialLineId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedSample by remember { mutableStateOf<LabTestLog?>(null) }
    var showCoaModal by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("lab_test_logs_screen"),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top App Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("btn_back_to_fleet_from_lab")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CENTRAL LAB TEST RECORDS",
                            fontFamily = ChivoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = OnSurface
                        )
                        Text(
                            text = "ASTM / ISO Spectrometry & Mechanical Tensile Matrix",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerHigh)
                            .border(1.dp, FocusCyan, RoundedCornerShape(4.dp))
                            .clickable { showCoaModal = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "CoA",
                                tint = FocusCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "VIEW COA",
                                style = IndustrialTypography.LabelCode,
                                color = FocusCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Summary Info
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CERTIFIED LAB SAMPLES (${uiState.labLogs.size})",
                        style = IndustrialTypography.LabelCode,
                        color = SafetyAmberLight
                    )
                    Text(
                        text = "CALIBRATION NIST TRACEABLE",
                        style = IndustrialTypography.LabelCode,
                        color = OutlineSlate
                    )
                }
            }

            // High-Density Data Table
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, OutlineDark, RoundedCornerShape(4.dp))
                ) {
                    // Sticky Header with uppercase label-code typography
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TableHeaderBg)
                            .border(1.dp, OutlineVariant)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SAMPLE / LINE",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate,
                            modifier = Modifier.weight(1.3f)
                        )
                        Text(
                            text = "ASTM TEST",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = "MEASURED (DELTA)",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate,
                            modifier = Modifier.weight(1.6f)
                        )
                        Text(
                            text = "RESULT",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    // Alternating row banding using #0f172a and #131d31
                    uiState.labLogs.forEachIndexed { index, log ->
                        val rowBg = if (index % 2 == 0) TableRowBase else TableRowAlternate
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .background(rowBg)
                                .clickable { selectedSample = log }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    text = log.sampleId,
                                    style = IndustrialTypography.DataTabular,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${log.lineId} • ${log.rollId}",
                                    style = IndustrialTypography.LabelCode,
                                    color = SafetyAmberLight,
                                    fontSize = 10.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(
                                    text = log.testName,
                                    style = IndustrialTypography.LabelCode,
                                    color = OnSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = log.standard,
                                    style = IndustrialTypography.LabelCode,
                                    color = OutlineSlate,
                                    fontSize = 9.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1.6f)) {
                                Text(
                                    text = log.measuredValue,
                                    style = IndustrialTypography.DataTabular,
                                    color = OnSurface,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = log.delta,
                                    style = IndustrialTypography.LabelCode,
                                    color = when (log.result) {
                                        MetricState.PASS -> PassGreenText
                                        MetricState.WARNING -> WarningText
                                        MetricState.FAIL -> CriticalRedText
                                    },
                                    fontSize = 9.sp
                                )
                            }

                            Box(
                                modifier = Modifier.weight(0.8f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                MetricStateChip(state = log.result)
                            }
                        }
                        if (index < uiState.labLogs.size - 1) {
                            HorizontalDivider(color = OutlineDark, thickness = 0.5.dp)
                        }
                    }
                }
            }

            // Quick detail card if sample selected
            selectedSample?.let { sample ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, FocusCyan, RoundedCornerShape(4.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DETAIL: ${sample.sampleId}",
                                fontFamily = ChivoFamily,
                                fontWeight = FontWeight.Bold,
                                color = FocusCyan
                            )
                            MetricStateChip(state = sample.result)
                        }
                        Text(
                            text = "Test: ${sample.testName} // Standard: ${sample.standard}",
                            style = IndustrialTypography.LabelCode,
                            color = OnSurface
                        )
                        Text(
                            text = "Target Spec: ${sample.targetSpec}",
                            style = IndustrialTypography.DataTabular,
                            color = OutlineSlate
                        )
                        Text(
                            text = "Measured Value: ${sample.measuredValue} (${sample.delta})",
                            style = IndustrialTypography.DataTabularLarge,
                            color = OnSurface,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Operator: ${sample.technician} // Time: ${sample.timestamp}",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Certificate of Analysis (CoA) Modal
        if (showCoaModal) {
            AlertDialog(
                onDismissRequest = { showCoaModal = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "CoA",
                            tint = SafetyAmberLight
                        )
                        Text(
                            text = "CERTIFICATE OF ANALYSIS (CoA)",
                            fontFamily = ChivoFamily,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 15.sp
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "LOT: LOT-2026-X89 // Alpine 5-Layer Blown",
                            style = IndustrialTypography.LabelCode,
                            color = SafetyAmberLight
                        )
                        Text(
                            text = "Customer: TetraTech Packaging Ltd.",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate
                        )
                        HorizontalDivider(color = OutlineDark)
                        Text(
                            text = "• ASTM D6988 Thickness: 42.4 µm (Passed with tolerance)\n• ASTM D882 Tensile Yield: 24.8 N/15mm (Certified)\n• ASTM F88 Seal Strength: 18.2 N/15mm (Certified)\n• ASTM D2578 Dyne Level: 42 dynes (Passed)\n• Heavy Metals & Migration: Non-Detectable (< 0.01 mg/dm²)",
                            style = IndustrialTypography.DataTabular,
                            color = OnSurface,
                            lineHeight = 18.sp
                        )
                        HorizontalDivider(color = OutlineDark)
                        Text(
                            text = "NIST Traceable QA Lab ID: LAB-ISO-17025-QMS\nApproved By: Dr. Aris / Lead Materials Engineer",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate,
                            fontSize = 10.sp
                        )
                    }
                },
                confirmButton = {
                    PrimaryIndustrialButton(
                        text = "Export CoA PDF",
                        onClick = { showCoaModal = false }
                    )
                },
                dismissButton = {
                    SecondaryIndustrialButton(
                        text = "Close",
                        onClick = { showCoaModal = false }
                    )
                },
                containerColor = SurfaceContainerHigh,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}
