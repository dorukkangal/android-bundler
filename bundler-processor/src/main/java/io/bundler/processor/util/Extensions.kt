package io.bundler.processor.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.Element
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

internal fun <T : Annotation> Element.hasAnnotation(a: Class<T>): Boolean {
    return getAnnotation(a) != null
}

internal fun TypeName.javaToKotlinType(): TypeName {
    return if (this is ParameterizedTypeName) {
        (rawType.javaToKotlinType() as ClassName)
                .parameterizedBy(
                        *typeArguments
                                .map { it.javaToKotlinType() }
                                .toTypedArray()
                )
    } else {
        val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))
                ?.asSingleFqName()
                ?.asString()
        if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}

internal fun String.toSnakeCase(): String {
    var text = ""
    this.forEachIndexed { index, char ->
        if (char.isUpperCase() && this.getOrNull(index - 1)?.isLowerCase() == true) {
            text += "_"
        }
        text += char.toUpperCase()
    }
    return text
}
