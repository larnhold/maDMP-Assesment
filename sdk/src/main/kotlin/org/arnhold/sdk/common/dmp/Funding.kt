package org.arnhold.sdk.common.dmp

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.arnhold.sdk.common.DCSO
import org.arnhold.sdk.common.dmp.helper.DataPropertyDefinition
import org.arnhold.sdk.common.dmp.helper.ObjectPropertyDefinition
import org.arnhold.sdk.common.dmp.helper.RdfResourceProvider

data class Funding (
    @JsonProperty("funder_id")
    val funderId: FunderId?,
    @JsonProperty("funding_status")
    val fundingStatus: String?,
    @JsonProperty("grant_id")
    val grantId: GrantId?
): RdfResourceProvider() {
    override fun toResource(model: Model, name: String): Resource {
        return super.toResource(model, name, listOf(
            DataPropertyDefinition(DCSO.FUNDING_STATUS, fundingStatus)
        ), listOf(
            ObjectPropertyDefinition(DCSO.HAS_GRANT_ID, grantId, name, "grantId"),
            ObjectPropertyDefinition(DCSO.HAS_FUNDER_ID, funderId, name, "funderId")
        ))
    }
}