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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.DefectCategory
import com.example.model.DefectRecord
import com.example.model.DefectSeverity
import com.example.ui.components.DestructiveIndustrialButton
import com.example.ui.components.PrimaryIndustrialButton
import com.example.ui.components.SecondaryIndustrialButton
import com.example.ui.theme.*
import com.example.viewmodel.FleetUiState

@Composable
fun DefectLogNcrScreen(
    uiState: FleetUiState,
    initialLineId: String,
    onBack: () -> Unit,
    onLogDefect: (String, DefectCategory, DefectSeverity, Int, Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedLineId by remember { mutableStateOf(initialLineId) }
    var selectedCategory by remember { mutableStateOf(DefectCategory.DIE_LINES) }
    var selectedSeverity by remember { mutableStateOf(DefectSeverity.MAJOR) }
    var footagePosition by remember { mutableIntStateOf(1420) }
    var scrapMeters by remember { mutableIntStateOf(150) }
    var rootCauseText by remember { mutableStateOf("Extruder die lip drag line detected across web edge.") }
    var containmentText by remember { mutableStateOf("Immediate bubble burp, scrape die lips with brass knife, tag scrap zone.") }

    var isAddingNewNcr by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("defect_log_screen"),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
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
                            .testTag("btn_back_to_fleet_from_ncr")
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
                            text = "DEFECT LOG & NON-CONFORMANCE (NCR)",
                            fontFamily = ChivoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = OnSurface
                        )
                        Text(
                            text = "Audit-Ready Incident Containment & Quarantine Trail",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isAddingNewNcr) SafetyAmber else SurfaceContainerHigh)
                            .border(1.dp, if (isAddingNewNcr) SafetyAmber else OutlineVariant, RoundedCornerShape(4.dp))
                            .clickable { isAddingNewNcr = !isAddingNewNcr }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isAddingNewNcr) "VIEW LIST" else "+ LOG DEFECT",
                            style = IndustrialTypography.LabelCode,
                            color = if (isAddingNewNcr) OnSafetyAmber else SafetyAmberLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (isAddingNewNcr) {
                // New Defect / NCR Form
                item {
                    NewNcrEntryForm(
                        lineId = selectedLineId,
                        availableLines = uiState.lines.map { it.id },
                        onLineSelect = { selectedLineId = it },
                        category = selectedCategory,
                        onCategorySelect = { selectedCategory = it },
                        severity = selectedSeverity,
                        onSeveritySelect = { selectedSeverity = it },
                        footage = footagePosition,
                        onFootageChange = { footagePosition = it },
                        scrapMeters = scrapMeters,
                        onScrapChange = { scrapMeters = it },
                        rootCause = rootCauseText,
                        onRootCauseChange = { rootCauseText = it },
                        containment = containmentText,
                        onContainmentChange = { containmentText = it },
                        onSubmit = {
                            onLogDefect(
                                selectedLineId,
                                selectedCategory,
                                selectedSeverity,
                                footagePosition,
                                scrapMeters,
                                rootCauseText,
                                containmentText
                            )
                            isAddingNewNcr = false
                        }
                    )
                }
            } else {
                // Summary count of NCRs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE NON-CONFORMANCE INCIDENTS (${uiState.defects.size})",
                            style = IndustrialTypography.LabelCode,
                            color = SafetyAmberLight
                        )
                        Text(
                            text = "ISO 9001 / BRCGS AUDIT RECORD",
                            style = IndustrialTypography.LabelCode,
                            color = OutlineSlate
                        )
                    }
                }

                // Defects List
                items(uiState.defects, key = { it.id }) { defect ->
                    DefectRecordCard(defect = defect)
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun DefectRecordCard(defect: DefectRecord) {
    val (statusBorder, statusBg, statusText) = when (defect.status) {
        "QUARANTINED" -> Triple(CriticalRedBorder, CriticalRedSurface, CriticalRedText)
        "UNDER_INVESTIGATION" -> Triple(WarningBorder, WarningSurface, WarningText)
        "RESOLVED" -> Triple(PassGreenBorder, PassGreenSurface, PassGreenText)
        else -> Triple(HoldBorder, HoldSurface, HoldText)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("defect_card_${defect.id}")
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: ID, Line, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = defect.id,
                        style = IndustrialTypography.DataTabularLarge,
                        color = OnSurface,
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${defect.lineId} // ${defect.rollId}",
                            style = IndustrialTypography.LabelCode,
                            color = SafetyAmberLight
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(statusBg)
                        .border(1.dp, statusBorder, RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = defect.status,
                        style = IndustrialTypography.LabelBadge,
                        color = statusText
                    )
                }
            }

            // Defect details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = defect.category.displayName.uppercase(),
                        style = IndustrialTypography.LabelCode,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Severity: ${defect.severity.label}",
                        style = IndustrialTypography.LabelCode,
                        color = when (defect.severity) {
                            DefectSeverity.CRITICAL -> CriticalRedText
                            DefectSeverity.MAJOR -> WarningText
                            DefectSeverity.MINOR -> PassGreenText
                        }
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "POS: ${defect.footagePosition}m of ${defect.totalFootage}m",
                        style = IndustrialTypography.DataTabular,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "SCRAP: ${defect.scrapMeters} meters",
                        style = IndustrialTypography.LabelCode,
                        color = CriticalRedText
                    )
                }
            }

            // Root Cause & Containment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineDark, RoundedCornerShape(2.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ROOT CAUSE: ${defect.rootCause}",
                        style = IndustrialTypography.DataTabular,
                        color = OnSurface,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "CONTAINMENT: ${defect.containmentAction}",
                        style = IndustrialTypography.DataTabular,
                        color = OutlineSlate,
                        fontSize = 11.sp
                    )
                }
            }

            // Footer metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "LOGGED BY: ${defect.inspectorId}",
                    style = IndustrialTypography.LabelCode,
                    color = OutlineSlate,
                    fontSize = 10.sp
                )
                Text(
                    text = defect.timestamp,
                    style = IndustrialTypography.LabelCode,
                    color = OutlineSlate,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun NewNcrEntryForm(
    lineId: String,
    availableLines: List<String>,
    onLineSelect: (String) -> Unit,
    category: DefectCategory,
    onCategorySelect: (DefectCategory) -> Unit,
    severity: DefectSeverity,
    onSeveritySelect: (DefectSeverity) -> Unit,
    footage: Int,
    onFootageChange: (Int) -> Unit,
    scrapMeters: Int,
    onScrapChange: (Int) -> Unit,
    rootCause: String,
    onRootCauseChange: (String) -> Unit,
    containment: String,
    onContainmentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "FAST NON-CONFORMANCE INCIDENT ENTRY",
            fontFamily = ChivoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = SafetyAmberLight
        )

        // Select Production Line
        Text(
            text = "TARGET PRODUCTION LINE",
            style = IndustrialTypography.LabelCode,
            color = OutlineSlate
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(availableLines) { id ->
                val isSelected = id == lineId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) SafetyAmber else SurfaceContainerLowest)
                        .border(1.dp, if (isSelected) SafetyAmber else OutlineDark, RoundedCornerShape(2.dp))
                        .clickable { onLineSelect(id) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = id,
                        style = IndustrialTypography.LabelCode,
                        color = if (isSelected) OnSafetyAmber else OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Select Category
        Text(
            text = "DEFECT CATEGORY",
            style = IndustrialTypography.LabelCode,
            color = OutlineSlate
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(DefectCategory.entries) { cat ->
                val isSelected = cat == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) SurfaceContainerHigh else SurfaceContainerLowest)
                        .border(1.dp, if (isSelected) FocusCyan else OutlineDark, RoundedCornerShape(2.dp))
                        .clickable { onCategorySelect(cat) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.displayName,
                        style = IndustrialTypography.LabelCode,
                        color = if (isSelected) OnSurface else OutlineSlate,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Select Severity
        Text(
            text = "SEVERITY LEVEL",
            style = IndustrialTypography.LabelCode,
            color = OutlineSlate
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DefectSeverity.entries.forEach { sev ->
                val isSelected = sev == severity
                val sevColor = when (sev) {
                    DefectSeverity.CRITICAL -> CriticalRed
                    DefectSeverity.MAJOR -> SafetyAmber
                    DefectSeverity.MINOR -> PassGreen
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) sevColor.copy(alpha = 0.2f) else SurfaceContainerLowest)
                        .border(1.dp, if (isSelected) sevColor else OutlineDark, RoundedCornerShape(2.dp))
                        .clickable { onSeveritySelect(sev) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sev.label,
                        style = IndustrialTypography.LabelCode,
                        color = if (isSelected) sevColor else OutlineSlate,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Footage & Scrap Inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FOOTAGE POSITION (m)",
                    style = IndustrialTypography.LabelCode,
                    color = OutlineSlate
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineDark, RoundedCornerShape(2.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "$footage m",
                        style = IndustrialTypography.DataTabularLarge,
                        color = OnSurface
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ESTIMATED SCRAP (m)",
                    style = IndustrialTypography.LabelCode,
                    color = OutlineSlate
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineDark, RoundedCornerShape(2.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "$scrapMeters m",
                        style = IndustrialTypography.DataTabularLarge,
                        color = CriticalRedText
                    )
                }
            }
        }

        // Root Cause Text Input
        Text(
            text = "ROOT CAUSE ANALYSIS",
            style = IndustrialTypography.LabelCode,
            color = OutlineSlate
        )
        OutlinedTextField(
            value = rootCause,
            onValueChange = onRootCauseChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest,
                focusedBorderColor = FocusCyan,
                unfocusedBorderColor = OutlineDark,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface
            ),
            textStyle = IndustrialTypography.DataTabular
        )

        // Containment Action Input
        Text(
            text = "CORRECTIVE CONTAINMENT ACTION",
            style = IndustrialTypography.LabelCode,
            color = OutlineSlate
        )
        OutlinedTextField(
            value = containment,
            onValueChange = onContainmentChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest,
                focusedBorderColor = FocusCyan,
                unfocusedBorderColor = OutlineDark,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface
            ),
            textStyle = IndustrialTypography.DataTabular
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Commit buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DestructiveIndustrialButton(
                text = "File NCR & Quarantine",
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                testTag = "submit_ncr_btn"
            )
        }
    }
}
