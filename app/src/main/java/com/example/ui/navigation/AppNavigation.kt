package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DefectLogNcrScreen
import com.example.ui.screens.FleetMonitorScreen
import com.example.ui.screens.LabTestLogsScreen
import com.example.ui.screens.RollAuditScreen
import com.example.ui.theme.ChivoFamily
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.IndustrialTypography
import com.example.ui.theme.OnSafetyAmber
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.OutlineSlate
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberLight
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.FleetViewModel

enum class AppScreen(val title: String, val icon: ImageVector) {
    FLEET_MONITOR("FLEET", Icons.Default.PrecisionManufacturing),
    ROLL_AUDIT("AUDIT", Icons.Default.AssignmentTurnedIn),
    DEFECT_NCR("NCR LOG", Icons.Default.ReportProblem),
    LAB_RECORDS("LAB DATA", Icons.Default.Science)
}

@Composable
fun AppNavigation(
    viewModel: FleetViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(AppScreen.FLEET_MONITOR) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfaceDark,
        bottomBar = {
            IndustrialBottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    currentScreen = screen
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.FLEET_MONITOR -> {
                    FleetMonitorScreen(
                        uiState = uiState,
                        onSelectLine = { viewModel.selectLine(it) },
                        onNavigateToAudit = { lineId ->
                            viewModel.selectLine(lineId)
                            currentScreen = AppScreen.ROLL_AUDIT
                        },
                        onNavigateToDefects = { lineId ->
                            viewModel.selectLine(lineId)
                            currentScreen = AppScreen.DEFECT_NCR
                        },
                        onNavigateToLab = { lineId ->
                            viewModel.selectLine(lineId)
                            currentScreen = AppScreen.LAB_RECORDS
                        },
                        onApproveRoll = { lineId ->
                            viewModel.approveRoll(lineId)
                        },
                        onQuarantineLot = { lineId, reason ->
                            viewModel.quarantineLot(lineId, reason)
                        },
                        onFilterStatus = { viewModel.setFilterStatus(it) },
                        onToggleTelemetry = { viewModel.toggleLiveTelemetry() },
                        onClearStatusMessage = { viewModel.clearStatusMessage() }
                    )
                }

                AppScreen.ROLL_AUDIT -> {
                    val activeLine = viewModel.selectedLine ?: uiState.lines.first()
                    RollAuditScreen(
                        line = activeLine,
                        uiState = uiState,
                        onBack = { currentScreen = AppScreen.FLEET_MONITOR },
                        onUpdateMetric = { lineId, metricId, newVal ->
                            viewModel.updateMetricValue(lineId, metricId, newVal)
                        },
                        onToggleChecklist = { lineId, itemId, passed ->
                            viewModel.toggleChecklistItem(lineId, itemId, passed)
                        },
                        onApproveRoll = { lineId ->
                            viewModel.approveRoll(lineId)
                        },
                        onQuarantineLot = { lineId, reason ->
                            viewModel.quarantineLot(lineId, reason)
                        },
                        onNavigateToDefectLog = { lineId ->
                            viewModel.selectLine(lineId)
                            currentScreen = AppScreen.DEFECT_NCR
                        }
                    )
                }

                AppScreen.DEFECT_NCR -> {
                    DefectLogNcrScreen(
                        uiState = uiState,
                        initialLineId = uiState.selectedLineId,
                        onBack = { currentScreen = AppScreen.FLEET_MONITOR },
                        onLogDefect = { lineId, cat, sev, foot, scrap, cause, cont ->
                            viewModel.logDefect(lineId, cat, sev, foot, scrap, cause, cont)
                        }
                    )
                }

                AppScreen.LAB_RECORDS -> {
                    LabTestLogsScreen(
                        uiState = uiState,
                        initialLineId = uiState.selectedLineId,
                        onBack = { currentScreen = AppScreen.FLEET_MONITOR }
                    )
                }
            }
        }
    }
}

@Composable
fun IndustrialBottomNav(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineDark)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppScreen.entries.forEach { screen ->
                val isSelected = screen == currentScreen
                val itemColor = if (isSelected) SafetyAmberLight else OutlineSlate

                Box(
                    modifier = Modifier
                        .testTag("nav_item_${screen.name.lowercase()}")
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) SurfaceContainerHigh else SurfaceContainerLowest)
                        .border(
                            1.dp,
                            if (isSelected) SafetyAmber.copy(alpha = 0.5f) else OutlineDark.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onScreenSelected(screen) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = itemColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = screen.title,
                            style = IndustrialTypography.LabelCode,
                            color = itemColor,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
