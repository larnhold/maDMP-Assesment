package org.arnhold.dmpeval.casestudy.evaluation.completenessCategoryEvaluators

import org.apache.jena.ontology.OntModel
import org.apache.jena.rdf.model.Model
import org.arnhold.dmpeval.casestudy.evaluation.CategoryDimmensionModels
import org.arnhold.sdk.model.EvaluationDimensionConstants
import org.arnhold.sdk.vocab.dqv.Measurement
import org.arnhold.sdk.evaluator.DimensionEvaluatorPlugin
import org.arnhold.sdk.evaluator.EvaluatorInformation
import org.arnhold.sdk.model.EvaluationTaskParameters
import org.arnhold.sdk.vocab.constants.Extension
import org.arnhold.sdk.vocab.context.DMPContext
import org.springframework.stereotype.Component

@Component
class ScienceEuropeExtensionCompletenessDimensionEvaluator : DimensionEvaluatorPlugin {

    override fun getPluginIdentifier(): String {
        return EvaluationDimensionConstants.SCIENCE_EUROPE_EXTENSION_COMPLETENESS.toString()
    }

    override fun getPluginInformation(): EvaluatorInformation {
        return EvaluatorInformation(
            CategoryDimmensionModels.SCIENCE_EUROPE_EXTENSION_COMPLETENESS_DIMENSION,
            CategoryDimmensionModels.COMPLETENSS_CATEGORY,
            listOf()
        )
    }

    override fun getAllMeasurements(
        dmp: Model,
        context: List<DMPContext>,
        parameters: EvaluationTaskParameters,
        dmpOntology: OntModel,
        extensionOntologies: Map<Extension, OntModel>
    ): List<Measurement> {
        return if (extensionOntologies.containsKey(Extension.SCIENCE_EUROPE)) {
            produceMeasurements()
        } else {
            listOf()
        }
    }

    private fun produceMeasurements(): List<Measurement> {
        return listOf()
    }

    override fun supports(p0: String): Boolean {
        return EvaluationDimensionConstants.SCIENCE_EUROPE_EXTENSION_COMPLETENESS.toString() == p0
    }
}