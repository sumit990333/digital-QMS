package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.MetricState
import com.example.model.ToleranceParam
import com.example.ui.theme.*

/**
 * Industrial Status Badge conforming strictly to Apex QMS specifications:
 * Monospaced uppercase, 20-24dp height, solid 1px border with subtle tint.
 */
@Composable
fun IndustrialStatusBadge(
    status: LineStatus,
    modifier: Modifier = Modifier,
    customText: String? = null
) {
    val (borderColor, surfaceColor, textColor) = when (status) {
        LineStatus.IN_SPEC -> Triple(PassGreenBorder, PassGreenSurface, PassGreenText)
        LineStatus.CALIBRATION_WARNING -> Triple(WarningBorder, WarningSurface, WarningText)
        LineStatus.OUT_OF_SPEC -> Triple(CriticalRedBorder, CriticalRedSurface, CriticalRedText)
        LineStatus.HOLD_OFFLINE -> Triple(HoldBorder, HoldSurface, HoldText)
    }

    Box(
        modifier = modifier
            .testTag("status_badge_${status.name.lowercase()}")
            .clip(RoundedCornerShape(4.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Binary status dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(textColor)
            )
            Text(
                text = (customText ?: status.title).uppercase(),
                style = IndustrialTypography.LabelCode,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Metric state badge for small indicator chips
 */
@Composable
fun MetricStateChip(
    state: MetricState,
    modifier: Modifier = Modifier
) {
    val (borderColor, surfaceColor, textColor, label) = when (state) {
        MetricState.PASS -> Tuple4(PassGreenBorder, PassGreenSurface, PassGreenText, "PASS")
        MetricState.WARNING -> Tuple4(WarningBorder, WarningSurface, WarningText, "WARN")
        MetricState.FAIL -> Tuple4(CriticalRedBorder, CriticalRedSurface, CriticalRedText, "FAIL")
    }

    Box(
        modifier = modifier
            .testTag("metric_chip_${state.name.lowercase()}")
            .clip(RoundedCornerShape(4.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = IndustrialTypography.LabelBadge,
            color = textColor
        )
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

/**
 * Metric Tile with Prominent Top Border Keyline:
 * - Top keyline colored by state (Green/Amber/Red)
 * - Monospaced actual-versus-target display format: Actual: 42.4 µm / Target: 40.0 µm (±5%)
 * - Inline variance indicator
 */
@Composable
fun MetricTileWithKeyline(
    param: ToleranceParam,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val keylineColor = when (param.state) {
        MetricState.PASS -> PassGreen
        MetricState.WARNING -> SafetyAmber
        MetricState.FAIL -> CriticalRed
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Column(
        modifier = modifier
            .testTag("metric_tile_${param.id}")
            .then(clickableModifier)
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineDark, RoundedCornerShape(4.dp))
    ) {
        // Prominent 3dp top border keyline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(keylineColor)
        )

        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = param.name.uppercase(),
                    style = IndustrialTypography.LabelCode,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                MetricStateChip(state = param.state)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Actual measurement in large monospaced font
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = param.displayActual,
                    style = IndustrialTypography.DataTabularLarge,
                    color = OnSurface
                )
                Text(
                    text = param.varianceText,
                    style = IndustrialTypography.LabelCode,
                    color = when (param.state) {
                        MetricState.PASS -> PassGreenText
                        MetricState.WARNING -> WarningText
                        MetricState.FAIL -> CriticalRedText
                    },
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Target and Standard Reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target: ${param.displayTarget}",
                    style = IndustrialTypography.DataTabular,
                    color = OnSurfaceVariant
                )
                Text(
                    text = param.standardRef,
                    style = IndustrialTypography.LabelCode,
                    color = OutlineSlate
                )
            }

            // Traverse profile mini-bar if available
            if (param.traversePoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                TraverseMiniBar(
                    points = param.traversePoints,
                    target = param.target,
                    unit = param.unit
                )
            }
        }
    }
}

/**
 * Mini 5-point traverse graph showing cross-web gauge distribution
 */
@Composable
fun TraverseMiniBar(
    points: List<Double>,
    target: Double,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineDark, RoundedCornerShape(2.dp))
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "CROSS-WEB PROFILE (L ➔ R)",
                style = IndustrialTypography.LabelCode,
                color = OutlineSlate,
                fontSize = 9.sp
            )
            Text(
                text = "${points.size} PTS",
                style = IndustrialTypography.LabelCode,
                color = OutlineSlate,
                fontSize = 9.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            points.forEachIndexed { index, value ->
                val delta = value - target
                val color = when {
                    kotlin.math.abs(delta) <= 1.0 -> PassGreen
                    kotlin.math.abs(delta) <= 2.0 -> SafetyAmber
                    else -> CriticalRed
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${"%.1f".format(value)}",
                        style = IndustrialTypography.LabelCode,
                        fontSize = 9.sp,
                        color = color
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}

/**
 * Permanent Batch and Roll Header Metadata:
 * Permanently displays Job Order, Extrusion Line Number, and QA Inspector ID.
 */
@Composable
fun PermanentBatchHeader(
    jobOrder: String,
    lineId: String,
    inspectorId: String,
    rollNumber: String,
    batchLot: String,
    modifier: Modifier = Modifier,
    clientName: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("permanent_batch_header")
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(SafetyAmber)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = lineId,
                        style = IndustrialTypography.LabelCode,
                        color = OnSafetyAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = jobOrder,
                    style = IndustrialTypography.DataTabularLarge,
                    color = OnSurface,
                    fontSize = 15.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, OutlineDark, RoundedCornerShape(2.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ROLL $rollNumber",
                    style = IndustrialTypography.LabelCode,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOT: $batchLot",
                style = IndustrialTypography.LabelCode,
                color = OnSurfaceVariant
            )
            Text(
                text = "QA: $inspectorId",
                style = IndustrialTypography.LabelCode,
                color = SafetyAmberLight
            )
        }

        if (!clientName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "CUSTOMER: $clientName",
                style = IndustrialTypography.LabelCode,
                color = OutlineSlate,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Primary Industrial Commit Button:
 * Solid Amber #f59e0b text #0f172a font-weight 700 with uppercase micro-tracking.
 * Used strictly for commit states: "Approve Roll", "Authorize Release", "Sign-Off Batch".
 */
@Composable
fun PrimaryIndustrialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "primary_industrial_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (enabled) SafetyAmber else SafetyAmber.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontFamily = ChivoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = OnSafetyAmber,
            letterSpacing = 0.08.sp
        )
    }
}

/**
 * Secondary Slate Button:
 * Slate #1e293b with a 1px #334155 border, text #f8fafc.
 */
@Composable
fun SecondaryIndustrialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "secondary_industrial_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainerHigh)
            .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontFamily = ChivoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = OnSurface,
            letterSpacing = 0.05.sp
        )
    }
}

/**
 * Destructive/Halt Button:
 * Deep Crimson background with #ef4444 border and crisp white text:
 * "Quarantine Lot", "Flag NCR".
 */
@Composable
fun DestructiveIndustrialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "destructive_industrial_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(DestructiveBg)
            .border(1.dp, CriticalRedBorder, RoundedCornerShape(4.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontFamily = ChivoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            letterSpacing = 0.06.sp
        )
    }
}

/**
 * Tactile Box-Style Binary Verification Toggle:
 * 18px x 18px with 2px borders (#475569).
 * Checked state fills with #10b981 (Pass) or #ef4444 (Fail) with definitive iconography.
 */
@Composable
fun BinaryVerificationToggle(
    label: String,
    standard: String,
    passed: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    notes: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineDark, RoundedCornerShape(4.dp))
            .clickable { onToggle(!passed) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 20x20 box toggle
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (passed) PassGreen else DestructiveBg)
                .border(2.dp, if (passed) PassGreenBorder else CriticalRedBorder, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (passed) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (passed) "Verification Passed" else "Verification Failed",
                tint = if (passed) OnPassGreen else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = GeistFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = OnSurface
            )
            Text(
                text = standard,
                style = IndustrialTypography.LabelCode,
                color = OutlineSlate
            )
            if (notes.isNotBlank()) {
                Text(
                    text = notes,
                    style = IndustrialTypography.DataTabular,
                    fontSize = 11.sp,
                    color = if (passed) PassGreenText else CriticalRedText
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(if (passed) PassGreenSurface else CriticalRedSurface)
                .border(1.dp, if (passed) PassGreenBorder else CriticalRedBorder, RoundedCornerShape(2.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (passed) "PASS" else "FAIL",
                style = IndustrialTypography.LabelBadge,
                color = if (passed) PassGreenText else CriticalRedText
            )
        }
    }
}

/**
 * Spec Stepper & Inset Input Field:
 * Dark slate fill #0b1120 inset within a #334155 border.
 * Integrated unit-of-measure label permanently docked in the right slot (µm, GSM, N/15mm, etc.).
 * Tactile increment/decrement hit zones.
 */
@Composable
fun SpecStepperInput(
    label: String,
    value: Double,
    unit: String,
    step: Double,
    onValueChange: (Double) -> Unit,
    target: Double,
    standardRef: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineDark, RoundedCornerShape(4.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = IndustrialTypography.LabelCode,
                color = OnSurfaceVariant
            )
            Text(
                text = standardRef,
                style = IndustrialTypography.LabelCode,
                color = OutlineSlate
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrement button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
                    .clickable {
                        val next = (value - step).coerceAtLeast(0.0)
                        onValueChange(kotlin.math.round(next * 100) / 100.0)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrement",
                    tint = OnSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Inset Display Field with Docked Unit
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, FocusCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "%.2f".format(value),
                        style = IndustrialTypography.DataTabularLarge,
                        color = OnSurface
                    )
                    Text(
                        text = unit,
                        style = IndustrialTypography.LabelCode,
                        color = SafetyAmberLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Increment button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
                    .clickable {
                        val next = value + step
                        onValueChange(kotlin.math.round(next * 100) / 100.0)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increment",
                    tint = OnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val delta = value - target
            Text(
                text = "Target: ${"%.2f".format(target)} $unit",
                style = IndustrialTypography.DataTabular,
                color = OutlineSlate,
                fontSize = 11.sp
            )
            Text(
                text = "Variance: ${if (delta >= 0) "+" else ""}${"%.2f".format(delta)} $unit",
                style = IndustrialTypography.LabelCode,
                color = if (kotlin.math.abs(delta) <= step * 2) PassGreenText else WarningText,
                fontSize = 11.sp
            )
        }
    }
}
