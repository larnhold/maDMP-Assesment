package org.arnhold.evaluator.harvester.dataProvider

import org.apache.jena.ontology.OntModel
import org.apache.jena.rdf.model.Model
import org.arnhold.sdk.model.DMPLoaderParameters
import org.arnhold.sdk.vocab.constants.Extension
import org.arnhold.sdk.vocab.context.DMPContext
import java.util.UUID

interface DataProviderService {

    fun getDCSOntology(): OntModel

    fun getDMPDQVOntology(): OntModel

    fun getExtensions(): Map<Extension, OntModel>

    fun loadContext(model: Model): List<DMPContext>

    fun loadDMP(parameters: DMPLoaderParameters): UUID

    fun getModel(id: UUID): Model

    fun saveModel(model: Model): UUID

    fun <T> saveAsJson(data: Any, uuid: UUID?): UUID

}