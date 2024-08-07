package org.arnhold.dmpeval.casestudy.context.openAire

import eu.openaire.namespace.oaf.FieldType
import eu.openaire.namespace.oaf.QualifierType
import eu.openaire.namespace.oaf.ResultChildrenType
import eu.openaire.namespace.oaf.StructuredPropertyElementType
import generated.Response
import jakarta.xml.bind.JAXBElement
import org.arnhold.sdk.context.schema.Dataset
import org.arnhold.sdk.context.schema.Identifier
import org.arnhold.sdk.context.schema.enums.EDataAccessType
import org.arnhold.sdk.context.schema.enums.EDataSource
import org.arnhold.sdk.context.schema.enums.EDataType
import org.arnhold.sdk.context.schema.enums.EIdentifierType
import org.arnhold.sdk.context.schema.enums.ELicense
import org.springframework.stereotype.Component
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Component
class OpenAireMapperService {
    fun toNormalizedDataset(doi: String?, openaireResponse: Response, dataset: Dataset): Dataset? {
        var datasetCopy = dataset
        datasetCopy.source = EDataSource.REUSED
        try {
            val result = openaireResponse.results.result.metadata.entity.result
            val elements: List<JAXBElement<*>> = result.getCreatorOrResulttypeOrLanguage()
            for (element in elements) {
                if (element.name.localPart == "subject") {
                    val tmpValue = element.value as StructuredPropertyElementType
                    datasetCopy = addStringProperties(element.name.localPart, tmpValue.value, datasetCopy)
                } else if (element.declaredType === StructuredPropertyElementType::class.java) {
                    datasetCopy = addStructuresProperties(element.name.localPart, element.value as StructuredPropertyElementType, datasetCopy)
                } else if (element.declaredType === QualifierType::class.java) {
                    datasetCopy = addQualifierTypeProperties(element.name.localPart, element.value as QualifierType, datasetCopy)
                } else if (element.declaredType === String::class.java) {
                    datasetCopy = addStringProperties(element.name.localPart, element.value as String, datasetCopy)
                } else if (element.declaredType === ResultChildrenType::class.java) {
                    for (instance in (element.value as ResultChildrenType).instance) {
                        for (instanceElement in instance.licenseOrAccessrightOrInstancetype) {
                            if (instanceElement.declaredType === FieldType::class.java) {
                                datasetCopy = mapAtoB(
                                    instanceElement.name.localPart,
                                    instanceElement.value as FieldType,
                                    dataset
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: NullPointerException) {
            // Return null if no result present
            return null
        }
        val identifier = Identifier()
        identifier.type = (EIdentifierType.DOI)
        identifier.identifier = (doi)
        datasetCopy.datasetId = identifier
        return datasetCopy
    }

    private fun addStructuresProperties(propertyName: String, elementType: StructuredPropertyElementType, dataset: Dataset): Dataset {
        if (propertyName == "title") {
            if (dataset.title == null) {
                dataset.title = elementType.value
            } else {
                dataset.title = dataset.title + ' ' + elementType.value
            }
        }
        return dataset
    }

    private fun addQualifierTypeProperties(propertyName: String, qualifierType: QualifierType, dataset: Dataset): Dataset {
        if ("bestaccessright" == propertyName) {
            dataset.dataAccess = (getDataAccessType(qualifierType.classid.lowercase()))
        } else if ("resourcetype" == propertyName) {
            val type: String = qualifierType.classname.lowercase()
            when (type) {
                "image" -> dataset.type.add(EDataType.IMAGES)
                "film", "sound" -> dataset.type.add(EDataType.AUDIOVISUAL_DATA)
                "software" -> dataset.type.add(EDataType.SOFTWARE_APPLICATIONS)
                "text" -> dataset.type.add(EDataType.PLAIN_TEXT)
                else -> dataset.type.add(EDataType.OTHER)
            }
        }
        return dataset
    }

    fun addStringProperties(propertyName: String?, string: String, dataset: Dataset): Dataset {
        when (propertyName) {
            "description" -> if (dataset.description == null) dataset.description = string
            else dataset.description = ((dataset.description + ' ') + string)

            "size" -> try {
                val size = string.toLong()
                dataset.size = size
            } catch (ignored: NumberFormatException) {
                // Ignore
            }

            "dateofacceptance" -> {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                try {
                    dataset.startDate = formatter.parse(string)
                } catch (ignored: ParseException) {
                    // Ignore
                }
            }

            "subject" -> {
                dataset.subject.add(string)
            }

            "publisher" -> {
                dataset.publisher.add(string)
            }

            else -> {}
        }
        return dataset
    }

    fun mapAtoB(propertyName: String, elementType: FieldType, dataset: Dataset): Dataset {
        if ("license" == propertyName) {
            if (dataset.license == null) {
                dataset.license = ELicense.fromAcronym(elementType.value)
            }
        }
        return dataset
    }

    private fun getDataAccessType(bestAccessRight: String): EDataAccessType? {
        return when (bestAccessRight) {
            "open" -> EDataAccessType.OPEN
            "restricted" -> EDataAccessType.RESTRICTED
            "closed" -> EDataAccessType.CLOSED
            else -> null
        }
    }
}