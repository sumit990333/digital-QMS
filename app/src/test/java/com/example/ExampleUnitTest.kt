package com.example

import com.example.model.DefectCategory
import com.example.model.DefectSeverity
import com.example.model.LineStatus
import com.example.model.MetricState
import com.example.model.ToleranceParam
import com.example.viewmodel.FleetViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `tolerance parameter calculates correct metric states`() {
    val inSpecParam = ToleranceParam(
      id = "p1",
      name = "Mil Thickness",
      actual = 40.2,
      target = 40.0,
      tolerance = 5.0,
      unit = "µm",
      isPercentageTolerance = true,
      standardRef = "ASTM D6988"
    )
    assertEquals(MetricState.PASS, inSpecParam.state)

    val warningParam = ToleranceParam(
      id = "p1",
      name = "Mil Thickness",
      actual = 42.4,
      target = 40.0,
      tolerance = 5.0,
      unit = "µm",
      isPercentageTolerance = true,
      standardRef = "ASTM D6988"
    )
    assertEquals(MetricState.WARNING, warningParam.state)

    val failParam = ToleranceParam(
      id = "p1",
      name = "Mil Thickness",
      actual = 45.0,
      target = 40.0,
      tolerance = 5.0,
      unit = "µm",
      isPercentageTolerance = true,
      standardRef = "ASTM D6988"
    )
    assertEquals(MetricState.FAIL, failParam.state)
  }

  @Test
  fun `viewModel approveRoll updates line status to IN_SPEC`() {
    val viewModel = FleetViewModel()
    val targetLine = "EX-01"
    viewModel.approveRoll(targetLine)

    val state = viewModel.uiState.value
    val line = state.lines.find { it.id == targetLine }
    assertNotNull(line)
    assertEquals(LineStatus.IN_SPEC, line?.status)
    assertTrue(state.statusMessage?.contains("Approved") == true)
  }

  @Test
  fun `viewModel quarantineLot halts line and creates NCR`() {
    val viewModel = FleetViewModel()
    val targetLine = "EX-01"
    val initialNcrs = viewModel.uiState.value.defects.size

    viewModel.quarantineLot(targetLine, "Severe bubble burst detected")

    val state = viewModel.uiState.value
    val line = state.lines.find { it.id == targetLine }
    assertNotNull(line)
    assertEquals(LineStatus.OUT_OF_SPEC, line?.status)
    assertEquals(0, line?.speedMpm)
    assertEquals(initialNcrs + 1, state.defects.size)
    assertEquals("QUARANTINED", state.defects.first().status)
  }

  @Test
  fun `viewModel logDefect registers new NCR incident correctly`() {
    val viewModel = FleetViewModel()
    val targetLine = "EX-04"
    val initialNcrs = viewModel.uiState.value.defects.size

    viewModel.logDefect(
      lineId = targetLine,
      category = DefectCategory.DIE_LINES,
      severity = DefectSeverity.MAJOR,
      footage = 2400,
      scrapMeters = 120,
      rootCause = "Extrusion die lip drag line",
      containment = "Die lip cleaned with brass scraper"
    )

    val state = viewModel.uiState.value
    assertEquals(initialNcrs + 1, state.defects.size)
    val latestNcr = state.defects.first()
    assertEquals(targetLine, latestNcr.lineId)
    assertEquals(DefectCategory.DIE_LINES, latestNcr.category)
    assertEquals(120, latestNcr.scrapMeters)
  }
}
