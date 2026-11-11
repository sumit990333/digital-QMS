package com.example.data

import com.example.model.DefectCategory
import com.example.model.DefectRecord
import com.example.model.DefectSeverity
import com.example.model.InspectionChecklistItem
import com.example.model.LabTestLog
import com.example.model.LineStatus
import com.example.model.LineType
import com.example.model.MetricState
import com.example.model.ProductionLine
import com.example.model.ToleranceParam

object SamplePlantData {

    fun getInitialLines(): List<ProductionLine> = listOf(
        ProductionLine(
            id = "EX-01",
            name = "Line 01 - Alpine 5-Layer Blown",
            type = LineType.BLOWN_FILM,
            status = LineStatus.CALIBRATION_WARNING,
            jobOrder = "JO-84920",
            clientName = "TetraTech Packaging",
            activeRoll = "R-12/18",
            batchLot = "LOT-2026-X89",
            inspectorId = "QC-7749 // M. Vance",
            recipe = "BOPP/EVOH/mPE 5-Layer Barrier 40.0µm",
            speedMpm = 245,
            targetMpm = 250,
            currentLengthMeters = 3480,
            targetLengthMeters = 4000,
            qualityYield = 98.4,
            oee = 91.2,
            activeNcrsCount = 1,
            lastAuditTimestamp = "11:42:15 UTC",
            metrics = listOf(
                ToleranceParam(
                    id = "p1",
                    name = "Thickness Gauge (Average)",
                    actual = 42.4,
                    target = 40.0,
                    tolerance = 5.0, // +-5% = +-2.0um -> 42.4 is slightly over (warning)
                    unit = "µm",
                    isPercentageTolerance = true,
                    standardRef = "ASTM D6988",
                    traversePoints = listOf(41.2, 42.1, 42.4, 41.8, 40.9)
                ),
                ToleranceParam(
                    id = "p2",
                    name = "Tensile Yield MD",
                    actual = 24.8,
                    target = 25.0,
                    tolerance = 2.0,
                    unit = "N/15mm",
                    isPercentageTolerance = false,
                    standardRef = "ASTM D882"
                ),
                ToleranceParam(
                    id = "p3",
                    name = "Heat Seal Strength",
                    actual = 18.2,
                    target = 15.0,
                    tolerance = 0.0,
                    unit = "N/15mm",
                    isLowerBoundOnly = true,
                    standardRef = "ASTM F88"
                ),
                ToleranceParam(
                    id = "p4",
                    name = "Optical Density / Haze",
                    actual = 1.42,
                    target = 1.40,
                    tolerance = 0.05,
                    unit = "OD",
                    standardRef = "ASTM D1003"
                ),
                ToleranceParam(
                    id = "p5",
                    name = "Corona Dyne Level",
                    actual = 42.0,
                    target = 40.0,
                    tolerance = 0.0,
                    unit = "dynes/cm",
                    isLowerBoundOnly = true,
                    standardRef = "ASTM D2578"
                )
            )
        ),
        ProductionLine(
            id = "EX-04",
            name = "Line 04 - SML Cast High-Barrier",
            type = LineType.CAST_FILM,
            status = LineStatus.IN_SPEC,
            jobOrder = "JO-85104",
            clientName = "PharmaSeal Solutions",
            activeRoll = "R-08/14",
            batchLot = "LOT-2026-C12",
            inspectorId = "QC-8102 // S. Chen",
            recipe = "CPP Retortable Cast Film 65.0µm",
            speedMpm = 310,
            targetMpm = 320,
            currentLengthMeters = 5200,
            targetLengthMeters = 6000,
            qualityYield = 99.6,
            oee = 94.8,
            activeNcrsCount = 0,
            lastAuditTimestamp = "11:35:00 UTC",
            metrics = listOf(
                ToleranceParam(
                    id = "p1",
                    name = "Thickness Gauge (Average)",
                    actual = 65.2,
                    target = 65.0,
                    tolerance = 3.0,
                    unit = "µm",
                    isPercentageTolerance = true,
                    standardRef = "ASTM D6988",
                    traversePoints = listOf(64.9, 65.1, 65.2, 65.3, 65.0)
                ),
                ToleranceParam(
                    id = "p2",
                    name = "Tensile Yield MD",
                    actual = 32.4,
                    target = 32.0,
                    tolerance = 2.5,
                    unit = "N/15mm",
                    standardRef = "ASTM D882"
                ),
                ToleranceParam(
                    id = "p3",
                    name = "Heat Seal Strength",
                    actual = 28.5,
                    target = 22.0,
                    tolerance = 0.0,
                    unit = "N/15mm",
                    isLowerBoundOnly = true,
                    standardRef = "ASTM F88"
                ),
                ToleranceParam(
                    id = "p4",
                    name = "Haze Transmittance",
                    actual = 1.15,
                    target = 1.20,
                    tolerance = 0.15,
                    unit = "% Haze",
                    standardRef = "ASTM D1003"
                ),
                ToleranceParam(
                    id = "p5",
                    name = "COF Kinetic (Film-to-Metal)",
                    actual = 0.22,
                    target = 0.24,
                    tolerance = 0.04,
                    unit = "µ",
                    standardRef = "ASTM D1894"
                )
            )
        ),
        ProductionLine(
            id = "RG-02",
            name = "Line 02 - Windmöller Heliostar 10C",
            type = LineType.ROTOGRAVURE,
            status = LineStatus.OUT_OF_SPEC,
            jobOrder = "JO-84992",
            clientName = "Nestor Foods Global",
            activeRoll = "R-04/10",
            batchLot = "LOT-2026-R44",
            inspectorId = "QC-7749 // M. Vance",
            recipe = "Print Web Reverse Gravure 20.0µm",
            speedMpm = 380,
            targetMpm = 400,
            currentLengthMeters = 2100,
            targetLengthMeters = 5000,
            qualityYield = 94.1,
            oee = 83.5,
            activeNcrsCount = 2,
            lastAuditTimestamp = "11:28:40 UTC",
            metrics = listOf(
                ToleranceParam(
                    id = "p1",
                    name = "Ink Density Cyan Unit 3",
                    actual = 1.68,
                    target = 1.45,
                    tolerance = 0.08,
                    unit = "OD",
                    standardRef = "ISO 12647-2"
                ),
                ToleranceParam(
                    id = "p2",
                    name = "Registration Color Offset",
                    actual = 0.14,
                    target = 0.05,
                    tolerance = 0.04,
                    unit = "mm",
                    standardRef = "ASTM F118"
                ),
                ToleranceParam(
                    id = "p3",
                    name = "Solvent Retention (GC)",
                    actual = 14.8,
                    target = 12.0,
                    tolerance = 2.0,
                    unit = "mg/m²",
                    standardRef = "EN 13676"
                ),
                ToleranceParam(
                    id = "p4",
                    name = "Corona Dyne Primed",
                    actual = 38.0,
                    target = 42.0,
                    tolerance = 0.0,
                    unit = "dynes/cm",
                    isLowerBoundOnly = true,
                    standardRef = "ASTM D2578"
                )
            )
        ),
        ProductionLine(
            id = "SL-01",
            name = "Line 01 - Nordmeccanica Simplex",
            type = LineType.SOLVENTLESS_LAMINATION,
            status = LineStatus.IN_SPEC,
            jobOrder = "JO-85210",
            clientName = "BioFresh Barrier",
            activeRoll = "R-15/20",
            batchLot = "LOT-2026-L07",
            inspectorId = "QC-8419 // T. Ross",
            recipe = "PET 12µm / AL 7µm / LLDPE 50µm",
            speedMpm = 420,
            targetMpm = 450,
            currentLengthMeters = 6100,
            targetLengthMeters = 8000,
            qualityYield = 99.2,
            oee = 93.0,
            activeNcrsCount = 0,
            lastAuditTimestamp = "11:15:10 UTC",
            metrics = listOf(
                ToleranceParam(
                    id = "p1",
                    name = "Adhesive Coat Weight",
                    actual = 1.85,
                    target = 1.80,
                    tolerance = 0.15,
                    unit = "GSM",
                    standardRef = "ISO 2859"
                ),
                ToleranceParam(
                    id = "p2",
                    name = "Bond Strength T-Peel",
                    actual = 4.2,
                    target = 3.5,
                    tolerance = 0.0,
                    unit = "N/15mm",
                    isLowerBoundOnly = true,
                    standardRef = "ASTM D1876"
                ),
                ToleranceParam(
                    id = "p3",
                    name = "Curing Tunnel Temp",
                    actual = 42.1,
                    target = 42.0,
                    tolerance = 1.5,
                    unit = "°C",
                    standardRef = "ISO 9001-Q"
                )
            )
        ),
        ProductionLine(
            id = "SLT-03",
            name = "Line 03 - Titan SR9 Dual-Turret",
            type = LineType.SLITTER_REWINDER,
            status = LineStatus.HOLD_OFFLINE,
            jobOrder = "JO-84880",
            clientName = "PouchCraft Systems",
            activeRoll = "R-01/06",
            batchLot = "LOT-2026-S19",
            inspectorId = "QC-7749 // M. Vance",
            recipe = "Slit Slugs Width 180mm ±0.5mm",
            speedMpm = 0,
            targetMpm = 600,
            currentLengthMeters = 0,
            targetLengthMeters = 12000,
            qualityYield = 97.5,
            oee = 72.4,
            activeNcrsCount = 1,
            lastAuditTimestamp = "09:50:00 UTC",
            metrics = listOf(
                ToleranceParam(
                    id = "p1",
                    name = "Slit Width Accuracy",
                    actual = 180.2,
                    target = 180.0,
                    tolerance = 0.5,
                    unit = "mm",
                    standardRef = "ASTM D6988"
                ),
                ToleranceParam(
                    id = "p2",
                    name = "Winding Tension Taper",
                    actual = 110.0,
                    target = 115.0,
                    tolerance = 10.0,
                    unit = "N",
                    standardRef = "TAPPI T560"
                )
            )
        )
    )

    fun getInitialChecklist(lineId: String): List<InspectionChecklistItem> = listOf(
        InspectionChecklistItem(
            id = "chk-1",
            label = "Corona Discharge Uniformity",
            standard = "ASTM D2578 (Dyne level test across web width)",
            passed = true,
            notes = "Checked at 42 dynes; no dielectric streak"
        ),
        InspectionChecklistItem(
            id = "chk-2",
            label = "Cross-Web Gauge Traverse Calibration",
            standard = "ASTM D6988 (Capacitive sensor zero calibrated)",
            passed = true,
            notes = "Zero calibrated with certified 40.0µm standard"
        ),
        InspectionChecklistItem(
            id = "chk-3",
            label = "Bubble Frost-Line Height Stability",
            standard = "ISO 9001-SOP-EX01 (Height within ±25mm)",
            passed = true,
            notes = "Frost line stable at 1,150mm"
        ),
        InspectionChecklistItem(
            id = "chk-4",
            label = "Optical Clarity & Micro-Gel Scan",
            standard = "ASTM D1003 (< 3 gels per 100m²)",
            passed = false,
            notes = "Camera sensor logged 4 minor pinhead gels near edge"
        ),
        InspectionChecklistItem(
            id = "chk-5",
            label = "Splice Detection & Core Alignment Zero",
            standard = "TAPPI T811 (Core runout < 0.3mm)",
            passed = true,
            notes = "3-inch fiber core seated and locked"
        )
    )

    fun getInitialDefects(): List<DefectRecord> = listOf(
        DefectRecord(
            id = "NCR-2026-0412",
            lineId = "RG-02",
            rollId = "R-04/10",
            batchLot = "LOT-2026-R44",
            category = DefectCategory.INK_SMEAR_PINHOLES,
            severity = DefectSeverity.CRITICAL,
            footagePosition = 1420,
            totalFootage = 5000,
            scrapMeters = 240,
            inspectorId = "QC-7749 // M. Vance",
            timestamp = "11:24 UTC",
            status = "QUARANTINED",
            rootCause = "Doctor blade nick on cyan cylinder station 3",
            containmentAction = "Line halted, cylinder buffed, 240m quarantined to scrap bay"
        ),
        DefectRecord(
            id = "NCR-2026-0409",
            lineId = "EX-01",
            rollId = "R-12/18",
            batchLot = "LOT-2026-X89",
            category = DefectCategory.GAUGE_BAND,
            severity = DefectSeverity.MAJOR,
            footagePosition = 2850,
            totalFootage = 4000,
            scrapMeters = 80,
            inspectorId = "QC-7749 // M. Vance",
            timestamp = "10:15 UTC",
            status = "UNDER_INVESTIGATION",
            rootCause = "Air ring segment 14 heater resistance variance (+1.2°C)",
            containmentAction = "Manual trim on auto-die heating profile adjusted"
        ),
        DefectRecord(
            id = "NCR-2026-0398",
            lineId = "SLT-03",
            rollId = "R-01/06",
            batchLot = "LOT-2026-S19",
            category = DefectCategory.WRINKLES_TENSION,
            severity = DefectSeverity.MINOR,
            footagePosition = 350,
            totalFootage = 12000,
            scrapMeters = 25,
            inspectorId = "QC-8102 // S. Chen",
            timestamp = "09:40 UTC",
            status = "RESOLVED",
            rootCause = "Bowed spreader roller angle slippage",
            containmentAction = "Pillow block tightened, slug re-threaded"
        )
    )

    fun getInitialLabLogs(): List<LabTestLog> = listOf(
        LabTestLog(
            sampleId = "SMP-9021-A",
            jobOrder = "JO-84920",
            rollId = "R-12/18",
            lineId = "EX-01",
            testName = "Tensile Yield MD/TD",
            standard = "ASTM D882",
            measuredValue = "24.8 / 23.9 N/15mm",
            targetSpec = "25.0 ± 2.0 N/15mm",
            delta = "-0.2 N/15mm",
            result = MetricState.PASS,
            technician = "Lab Tech #4 // R. Davis",
            timestamp = "11:30 UTC"
        ),
        LabTestLog(
            sampleId = "SMP-9021-B",
            jobOrder = "JO-84920",
            rollId = "R-12/18",
            lineId = "EX-01",
            testName = "Mil Thickness Traverse (5-Pt)",
            standard = "ASTM D6988",
            measuredValue = "42.4 µm (Max 42.8)",
            targetSpec = "40.0 ± 2.0 µm",
            delta = "+2.4 µm (Warn)",
            result = MetricState.WARNING,
            technician = "Lab Tech #4 // R. Davis",
            timestamp = "11:32 UTC"
        ),
        LabTestLog(
            sampleId = "SMP-9018-C",
            jobOrder = "JO-84992",
            rollId = "R-04/10",
            lineId = "RG-02",
            testName = "Solvent Retention GC Headspace",
            standard = "EN 13676",
            measuredValue = "14.8 mg/m²",
            targetSpec = "≤ 12.0 mg/m²",
            delta = "+2.8 mg/m² (Fail)",
            result = MetricState.FAIL,
            technician = "QA Chemist #1 // Dr. Aris",
            timestamp = "11:10 UTC"
        ),
        LabTestLog(
            sampleId = "SMP-9015-D",
            jobOrder = "JO-85104",
            rollId = "R-08/14",
            lineId = "EX-04",
            testName = "Heat Seal Yield (140°C, 3 bar)",
            standard = "ASTM F88",
            measuredValue = "28.5 N/15mm",
            targetSpec = "≥ 22.0 N/15mm",
            delta = "+6.5 N/15mm",
            result = MetricState.PASS,
            technician = "Lab Tech #2 // K. Novak",
            timestamp = "10:55 UTC"
        ),
        LabTestLog(
            sampleId = "SMP-9010-E",
            jobOrder = "JO-85210",
            rollId = "R-15/20",
            lineId = "SL-01",
            testName = "Solventless Bond T-Peel (24h)",
            standard = "ASTM D1876",
            measuredValue = "4.2 N/15mm",
            targetSpec = "≥ 3.5 N/15mm",
            delta = "+0.7 N/15mm",
            result = MetricState.PASS,
            technician = "Lab Tech #2 // K. Novak",
            timestamp = "10:30 UTC"
        )
    )
}
