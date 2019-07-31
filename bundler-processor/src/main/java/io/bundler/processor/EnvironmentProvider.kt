package io.bundler.processor

import io.bundler.processor.model.ProcessingException
import javax.annotation.processing.Filer
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

interface EnvironmentProvider {
    fun typeUtils(): Types
    fun elementUtils(): Elements
    fun filer(): Filer
    fun error(exception: ProcessingException)
}
