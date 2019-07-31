package io.bundler.processor.mapper

import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ProcessingException

internal abstract class Mapper<in In, out Out>(protected val environmentProvider: EnvironmentProvider) {

    @Throws(ProcessingException::class)
    abstract fun map(element: In): Out?
}
