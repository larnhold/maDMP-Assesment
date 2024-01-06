package org.arnhold.sdk.common.dqv

import org.arnhold.sdk.common.constants.DataLifecycle

data class Measurement<T>(
    val lifeCycleStage: DataLifecycle,
    val metric: Metric,
    val guidance: Guidance,
    val computedOn: String,
    val value: T
)
