package org.arnhold.evaluator.indicator.evaluationProvider

import org.apache.jena.ontology.OntModel
import org.apache.jena.rdf.model.Model
import org.arnhold.sdk.model.EvaluationTaskParameters
import org.arnhold.sdk.evaluator.DimensionEvaluatorPlugin
import org.arnhold.sdk.vocab.constants.Extension
import org.arnhold.sdk.vocab.context.DMPContext
import org.arnhold.sdk.vocab.dqv.Measurement

interface EvaluationProviderService {
    fun getAllEvaluators(): List<DimensionEvaluatorPlugin>
    suspend fun produceAllMeasurements(dmp: Model, context: List<DMPContext>, parameters: EvaluationTaskParameters, dmpOntology: OntModel, extensionOntologies: Map<Extension, OntModel>): List<Measurement>
    suspend fun produceMeasurementsForDimensions(dmp: Model, context: List<DMPContext>, parameters: EvaluationTaskParameters, dmpOntology: OntModel, extensionOntologies: Map<Extension, OntModel>): List<Measurement>
    suspend fun produceMeasurementsForCategories(dmp: Model, context: List<DMPContext>, parameters: EvaluationTaskParameters, dmpOntology: OntModel, extensionOntologies: Map<Extension, OntModel>): List<Measurement>
}