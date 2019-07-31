package io.bundler.processor.mapper

import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassType

internal class ActivityMapper(
        environmentProvider: EnvironmentProvider
) : ClassMapper(environmentProvider) {

    override val classType: ClassType = ClassType.ACTIVITY
}
