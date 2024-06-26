package org.arnhold.evaluator.harvester.dmpProvider

import org.apache.jena.ontology.OntModel
import org.apache.jena.rdf.model.Model

interface DmpProviderService {

    fun loadDMP(dmploader: String, dmpIdentifier: String, ontology: OntModel): Model

}