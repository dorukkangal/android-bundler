package io.bundler.processor.mapper

import com.squareup.kotlinpoet.asTypeName
import io.bundler.annotation.Arg
import io.bundler.annotation.Bundler
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.model.ClassType
import io.bundler.processor.model.ProcessingException
import io.bundler.processor.util.hasAnnotation
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

internal abstract class ClassMapper(
        environmentProvider: EnvironmentProvider
) : Mapper<Element, ClassModel>(environmentProvider) {

    abstract val classType: ClassType

    private val fieldMapper: FieldMapper by lazy {
        FieldMapper(environmentProvider)
    }

    override fun map(element: Element): ClassModel? {
        checkModifiers(element)

        val annotation = element.getAnnotation(Bundler::class.java)

        return ClassModel(
                packageName = environmentProvider.elementUtils().getPackageOf(element).toString(),
                className = element.simpleName.toString(),
                typeName = element.asType().asTypeName(),
                classType = classType,
                fieldModels = getFields(element, annotation.inherited).mapNotNull { fieldMapper.map(it) }
        )
    }

    private fun getFields(element: Element, scanSuperClasses: Boolean): MutableList<Element> {
        val annotatedFields = element.enclosedElements
                ?.filter { it.hasAnnotation(Arg::class.java) }?.toMutableList() ?: mutableListOf()
        if (scanSuperClasses) {
            getSuperElement(element)?.let { superElement ->
                annotatedFields.apply {
                    addAll(getFields(superElement, scanSuperClasses))
                }
            }
        }
        return annotatedFields
    }

    private fun getSuperElement(element: Element?): Element? {
        if (element !is TypeElement) return null

        val superClass = element.superclass
        if (superClass.kind == TypeKind.NONE) return null

        val superElement = environmentProvider.typeUtils().asElement(superClass) as TypeElement
        if (superElement.qualifiedName.toString().startsWith("android.")) return null

        return superElement
    }

    private fun checkModifiers(element: Element) {
        if (element.kind != ElementKind.CLASS) {
            throw ProcessingException(
                    element,
                    Bundler::class.java,
                    "Can only be applied on Activity and Fragment classes"
            )
        }

        if (element.modifiers.contains(Modifier.ABSTRACT)) {
            throw ProcessingException(
                    element,
                    Bundler::class.java,
                    "Cannot be applied on abstract classes"
            )
        }
    }
}
