package io.bundler.processor.mapper

import com.squareup.kotlinpoet.asTypeName
import io.bundler.annotation.Arg
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.FieldModel
import io.bundler.processor.model.ProcessingException
import io.bundler.processor.util.hasAnnotation
import org.jetbrains.annotations.NotNull
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

internal class FieldMapper(
        environmentProvider: EnvironmentProvider
) : Mapper<Element, FieldModel>(environmentProvider) {

    override fun map(element: Element): FieldModel? {
        checkModifiers(element)

        val annotation = element.getAnnotation(Arg::class.java)

        return FieldModel(
                name = element.simpleName.toString(),
                bundleKey = annotation.key,
                typeName = element.asType().asTypeName(),
                isRequired = element.hasAnnotation(NotNull::class.java) || annotation.required
        )
    }

    private fun checkModifiers(element: Element) {
        if (element.kind != ElementKind.FIELD) {
            throw ProcessingException(
                    element,
                    Arg::class.java,
                    "Can only be applied on fieldModels"
            )
        }

        if (element.modifiers.contains(Modifier.FINAL)) {
            throw ProcessingException(
                    element,
                    Arg::class.java,
                    "Fields must not be final"
            )
        }

        if (element.modifiers.contains(Modifier.STATIC)) {
            throw ProcessingException(
                    element,
                    Arg::class.java,
                    "Fields must not be static"
            )
        }

        if (element.modifiers.contains(Modifier.PRIVATE)) {
            val parent = element.enclosingElement

            val hasSetterMethod = parent.enclosedElements
                    ?.filterIsInstance<ExecutableElement>()
                    ?.any { method ->
                        val methodName = method.simpleName.toString()
                        methodName.equals(other = "set${element.simpleName}", ignoreCase = true)
                                && method.parameters.size == 1
                                && method.parameters.first().asType().toString() == element.asType().toString()
                    }

            if (hasSetterMethod == false) {
                throw ProcessingException(
                        element,
                        Arg::class.java,
                        "Fields must not be private"
                )
            }
        }
    }
}
