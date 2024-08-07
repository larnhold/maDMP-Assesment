package org.arnhold.sdk.tools.shacl

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.shacl.ShaclValidator
import org.apache.jena.shacl.Shapes
import org.apache.jena.shacl.ValidationReport
import org.apache.jena.shacl.validation.ReportEntry
import org.apache.jena.vocabulary.XSD
import org.arnhold.sdk.model.SoftareAgents
import org.arnhold.sdk.vocab.dqv.*
import org.springframework.stereotype.Service
import java.io.FileInputStream
import java.nio.file.Path

@Service
class ShaclValidationService {

    fun validateShape(dmp: Model,
                      shapesPath: Path,
                      metric: Metric,
                      dmpRoot: DMPLocation,
                      lifecycle: DmpLifecycle
    ): List<Measurement> {
        val shapesModel = ModelFactory.createOntologyModel()
        val extensionInputStream = FileInputStream(shapesPath.toFile())
        RDFDataMgr.read(shapesModel, extensionInputStream, Lang.TURTLE)

        val shapes = Shapes.parse(shapesModel)
        val graph = dmp.graph

        val report: ValidationReport = ShaclValidator.get().validate(shapes, graph)

        return if (report.conforms()) {
            listOf(getConformsMetric(lifecycle, dmpRoot, metric))
        } else {
            report.entries.map { createNotConfirmMeasurement(dmp, lifecycle, metric, it)}
        }
    }

    private fun getConformsMetric(lifecycle: DmpLifecycle, dmpRoot: DMPLocation, metric: Metric): Measurement {
        return Measurement(
            lifecycle,
            metric,
            null,
            dmpRoot,
            true,
            SoftareAgents.SHACL_AGENT,
            ArrayList()
        )
    }

    private fun createNotConfirmMeasurement(dmp: Model, lifecycle: DmpLifecycle, metric: Metric, report: ReportEntry): Measurement {
        val missingProperty = report.resultPath().toString()
        val guidance = Guidance("SHACL Report", missingProperty + ": " + report.message())
        val location = DMPLocation(report.focusNode().toString(), null)

        val shaclTestDefinition = MetricTestDefinition(
            identifier = report.source().toString(),
            title = "SHACL Shape",
            description = "A reference to the SHACL shape used for evaluation",
            expectedDataType = XSD.xboolean.toString()
        )
        val testResult = TestResult(
            testDefinition = shaclTestDefinition,
            value = false
        )

        //metric.metricTests.add(shaclTestDefinition)

        return Measurement(
            lifecycle,
            metric,
            listOf(guidance),
            location,
            false,
            SoftareAgents.SHACL_AGENT,
            null
        )
    }
}
