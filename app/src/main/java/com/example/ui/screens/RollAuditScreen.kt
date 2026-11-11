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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProductionLine
import com.example.ui.components.BinaryVerificationToggle
import com.example.ui.components.DestructiveIndustrialButton
import com.example.ui.components.IndustrialStatusBadge
import com.example.ui.components.MetricTileWithKeyline
import com.example.ui.components.PermanentBatchHeader
import com.example.ui.components.PrimaryIndustrialButton
import com.example.ui.components.SecondaryIndustrialButton
import com.example.ui.components.SpecStepperInput
import com.example.ui.theme.*
import com.example.viewmodel.FleetUiState

@Composable
fun RollAuditScreen(
    line: ProductionLine,
    uiState: FleetUiState,
    onBack: () -> Unit,
    onUpdateMetric: (String, String, Double) -> Unit,
    onToggleChecklist: (String, String, Boolean) -> Unit,
    onApproveRoll: (String) -> Unit,
    onQuarantineLot: (String, String) -> Unit,
    onNavigateToDefectLog: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showQuarantineDialog by remember { mutableStateOf(false) }
    var quarantineReason by remember { mutableStateOf("Out-of-spec gauge variation exceeding ±5% ASTM D6988 tolerance.") }
    var showApproveDialog by remember { mutableStateOf(false) }

    val checklistItems = uiState.checklist[line.id] ?: emptyList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("roll_audit_screen"),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top App Bar with back action
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
                            .testTag("btn_back_to_fleet")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Fleet Monitor",
                            tint = OnSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ROLL AUDIT // ${line.id}",
                            fontFamily = ChivoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = OnSurface
                        )
                        Text(
                            text = line.name,
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate
                        )
                    }

                    IndustrialStatusBadge(status = line.status)
                }
            }

            // Permanent Batch and Roll Header Metadata
            item {
                PermanentBatchHeader(
                    jobOrder = line.jobOrder,
                    lineId = line.id,
                    inspectorId = line.inspectorId,
                    rollNumber = line.activeRoll,
                    batchLot = line.batchLot,
                    clientName = line.clientName
                )
            }

            // Section: Current Live Tolerance Summary
            item {
                Text(
                    text = "CRITICAL QUALITY THRESHOLDS",
                    style = IndustrialTypography.LabelCode,
                    color = SafetyAmberLight
                )
            }

            items(line.metrics, key = { it.id }) { param ->
                MetricTileWithKeyline(param = param)
            }

            // Section: Interactive Spec Steppers & Caliper Adjustments
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "MANUAL SPEC STEPPERS & CALIBRATION",
                        style = IndustrialTypography.LabelCode,
                        color = SafetyAmberLight
                    )
                    Text(
                        text = "Fine-tune or log micrometer measurement adjustments for active batch inspection",
                        style = IndustrialTypography.DataTabular,
                        color = OutlineSlate,
                        fontSize = 11.sp
                    )
                }
            }

            item {
                if (line.metrics.isNotEmpty()) {
                    val firstMetric = line.metrics[0]
                    SpecStepperInput(
                        label = firstMetric.name,
                        value = firstMetric.actual,
                        unit = firstMetric.unit,
                        step = 0.2,
                        target = firstMetric.target,
                        standardRef = firstMetric.standardRef,
                        onValueChange = { newVal ->
                            onUpdateMetric(line.id, firstMetric.id, newVal)
                        }
                    )
                }
            }

            if (line.metrics.size > 1) {
                item {
                    val secondMetric = line.metrics[1]
                    SpecStepperInput(
                        label = secondMetric.name,
                        value = secondMetric.actual,
                        unit = secondMetric.unit,
                        step = 0.5,
                        target = secondMetric.target,
                        standardRef = secondMetric.standardRef,
                        onValueChange = { newVal ->
                            onUpdateMetric(line.id, secondMetric.id, newVal)
                        }
                    )
                }
            }

            // Section: Binary Verification Checklist
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "OPERATIONAL VERIFICATION CHECKLIST",
                        style = IndustrialTypography.LabelCode,
                        color = SafetyAmberLight
                    )
                    Text(
                        text = "Definitive pass/fail verification points required prior to CoC release",
                        style = IndustrialTypography.DataTabular,
                        color = OutlineSlate,
                        fontSize = 11.sp
                    )
                }
            }

            items(checklistItems, key = { it.id }) { item ->
                BinaryVerificationToggle(
                    label = item.label,
                    standard = item.standard,
                    passed = item.passed,
                    notes = item.notes,
                    onToggle = { newPassed ->
                        onToggleChecklist(line.id, item.id, newPassed)
                    }
                )
            }

            // Commit Actions: Industrial Primary Amber, Secondary Slate, Destructive Red
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineDark, RoundedCornerShape(4.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "OPERATIONAL SIGN-OFF & COMMIT",
                        style = IndustrialTypography.LabelCode,
                        color = OutlineSlate
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PrimaryIndustrialButton(
                            text = "Approve Roll",
                            onClick = { showApproveDialog = true },
                            modifier = Modifier.weight(1f),
                            testTag = "audit_approve_roll_btn"
                        )

                        DestructiveIndustrialButton(
                            text = "Quarantine Lot",
                            onClick = { showQuarantineDialog = true },
                            modifier = Modifier.weight(1f),
                            testTag = "audit_quarantine_btn"
                        )
                    }

                    SecondaryIndustrialButton(
                        text = "Log Non-Conformance (NCR)",
                        onClick = { onNavigateToDefectLog(line.id) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "audit_flag_ncr_btn"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Quarantine Confirmation Dialog
        if (showQuarantineDialog) {
            AlertDialog(
                onDismissRequest = { showQuarantineDialog = false },
                title = {
                    Text(
                        text = "QUARANTINE ORDER CONFIRMATION",
                        fontFamily = ChivoFamily,
                        fontWeight = FontWeight.Bold,
                        color = CriticalRedText,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Line: ${line.id} // Roll: ${line.activeRoll} // Lot: ${line.batchLot}",
                            style = IndustrialTypography.LabelCode,
                            color = OnSurface
                        )
                        Text(
                            text = "This action immediately halts line production, sets status to CRITICAL FAILURE / OUT-OF-SPEC, and tags scrap material in the quarantine database.",
                            style = IndustrialTypography.DataTabular,
                            color = OutlineSlate
                        )
                    }
                },
                confirmButton = {
                    DestructiveIndustrialButton(
                        text = "Confirm Quarantine",
                        onClick = {
                            onQuarantineLot(line.id, quarantineReason)
                            showQuarantineDialog = false
                            onBack()
                        }
                    )
                },
                dismissButton = {
                    SecondaryIndustrialButton(
                        text = "Cancel",
                        onClick = { showQuarantineDialog = false }
                    )
                },
                containerColor = SurfaceContainerHigh,
                shape = RoundedCornerShape(4.dp)
            )
        }

        // Approve Roll Confirmation Dialog
        if (showApproveDialog) {
            AlertDialog(
                onDismissRequest = { showApproveDialog = false },
                title = {
                    Text(
                        text = "AUTHORIZE ROLL RELEASE",
                        fontFamily = ChivoFamily,
                        fontWeight = FontWeight.Bold,
                        color = SafetyAmberLight,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Line: ${line.id} // Roll: ${line.activeRoll}",
                            style = IndustrialTypography.LabelCode,
                            color = OnSurface
                        )
                        Text(
                            text = "Digital inspector signature will be cryptographically attached. The roll will be certified for slitting and final packaging dispatch.",
                            style = IndustrialTypography.DataTabular,
                            color = OutlineSlate
                        )
                    }
                },
                confirmButton = {
                    PrimaryIndustrialButton(
                        text = "Sign-Off & Approve",
                        onClick = {
                            onApproveRoll(line.id)
                            showApproveDialog = false
                        }
                    )
                },
                dismissButton = {
                    SecondaryIndustrialButton(
                        text = "Cancel",
                        onClick = { showApproveDialog = false }
                    )
                },
                containerColor = SurfaceContainerHigh,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}
