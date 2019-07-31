package io.bundler.processor.model

import javax.lang.model.element.Element

/**
 * A simple exception that will be thrown if something went wrong during processsing.
 */
class ProcessingException(
        val element: Element,
        annotation: Class<*>,
        errorMessage: String
) : Exception() {

    override val message: String =
            "${annotation.simpleName}: $errorMessage -> ${element.simpleName}"
}
