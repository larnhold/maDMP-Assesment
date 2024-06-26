package org.arnhold.evaluator.harvester.contextProvider

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.apache.jena.rdf.model.Model
import org.arnhold.evaluator.plugin.PluginLoader
import org.arnhold.sdk.context.ContextProviderInformation
import org.arnhold.sdk.vocab.context.DMPContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ContextProviderServiceImpl @Autowired constructor(
    val pluginLoader: PluginLoader
): ContextProviderService {
    override suspend fun getAvailableContext(model: Model): List<DMPContext> {
        return coroutineScope {
            return@coroutineScope pluginLoader.getContextLoaders().map { async { it.getContext(model) } }.awaitAll().flatten()
        }
    }

    override fun getContextFromLoader(identifier: String, model: Model): List<DMPContext> {
        return this.pluginLoader.getContextLoaders().find { it.supports(identifier) }?.getContext(model) ?: ArrayList()
    }

    override fun getContextProviderIdentifiers(): List<String> {
        return pluginLoader.getContextLoaders().map { it.getPluginIdentifier() }
    }

    override fun getContextProviderInformation(identifier: String): ContextProviderInformation {
        return pluginLoader.getContextLoaders().find { it.supports(identifier) }?.getPluginInformation() ?: ContextProviderInformation()
    }

    override fun getAllContextProviderInformation(identifier: String): Map<String, ContextProviderInformation> {
        return pluginLoader.getContextLoaders().associateBy({it.getPluginIdentifier()}, {it.getPluginInformation()})
    }
}