package org.arnhold.sdk.vocab.dqv

import com.fasterxml.jackson.annotation.JsonInclude
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.arnhold.sdk.tools.rdfParsing.DataPropertyDefinition
import org.arnhold.sdk.tools.rdfParsing.ObjectPropertyDefinition
import org.arnhold.sdk.tools.rdfParsing.RdfResourceProvider
import org.arnhold.sdk.vocab.ontologyDefinitions.DMPDQV

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Dimension(
    val inCategory: Category?,
    val title: String,
    val description: String?,
    var hasParentDimension: Dimension? = null
): RdfResourceProvider() {

    override fun toResource(model: Model, name: String): Resource {
        return super.toResource(model, title + "_Dimension", listOf(
            DataPropertyDefinition(DMPDQV.DESCRIPTION, description),
            DataPropertyDefinition(DMPDQV.TITLE, title)
        ), listOf(
            ObjectPropertyDefinition(DMPDQV.IN_CATEGORY, inCategory),
            ObjectPropertyDefinition(DMPDQV.HAS_PARENT_DIMENSION, hasParentDimension)
        ))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dimension

        if (inCategory != other.inCategory) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inCategory?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        return result
    }

    override fun toString(): String {
        return "Dimension(inCategory=$inCategory, title=$title, description=$description)"
    }


}