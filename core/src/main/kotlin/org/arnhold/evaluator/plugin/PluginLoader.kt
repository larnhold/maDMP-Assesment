package org.arnhold.evaluator.plugin

import org.arnhold.sdk.common.dqv.Metric
import org.arnhold.sdk.contextLoader.ContextLoaderPlugin
import org.arnhold.sdk.dmpLoader.DmpLoaderPlugin
import org.arnhold.sdk.evaluator.EvaluationMethodPlugin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.plugin.core.PluginRegistry
import org.springframework.stereotype.Component

@Component
class PluginLoader @Autowired constructor(
        val evaluationMethodRegistry: PluginRegistry<EvaluationMethodPlugin, Metric>,
        val dmpLoaderRegistry: PluginRegistry<DmpLoaderPlugin, String>,
        val contextLoaderRegistry: PluginRegistry<ContextLoaderPlugin, String>
)  {

    fun getEvaluators(): List<EvaluationMethodPlugin> {
        return evaluationMethodRegistry.plugins
    }

    fun getDMPLoader(key: String):  DmpLoaderPlugin {
        return dmpLoaderRegistry.getRequiredPluginFor(key)
    }

    fun getContextLoaders(): List<ContextLoaderPlugin> {
        return contextLoaderRegistry.plugins
    }

}