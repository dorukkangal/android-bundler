package io.bundler.processor.mapper

import io.bundler.annotation.Bundler
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.model.ClassType
import io.bundler.processor.model.ProcessingException
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal class BundlerMapper(
        environmentProvider: EnvironmentProvider
) : Mapper<Element, ClassModel>(environmentProvider) {

    private val activityTypes: List<TypeElement> by lazy {
        ClassType.ACTIVITY.definedClasses.mapNotNull {
            environmentProvider.elementUtils().getTypeElement(it)
        }
    }

    private val fragmentTypes: List<TypeElement> by lazy {
        ClassType.FRAGMENT.definedClasses.mapNotNull {
            environmentProvider.elementUtils().getTypeElement(it)
        }
    }

    private val fragmentMapper: FragmentMapper by lazy {
        FragmentMapper(environmentProvider)
    }

    private val activityMapper: ActivityMapper by lazy {
        ActivityMapper(environmentProvider)
    }

    override fun map(element: Element): ClassModel? {
        return when {
            isInheritedFromActivity(element) -> activityMapper.map(element)
            isInheritedFromFragment(element) -> fragmentMapper.map(element)
            else -> throw ProcessingException(
                    element,
                    Bundler::class.java,
                    "Can only be applied on Activity and Fragment classes"
            )
        }
    }

    private fun isInheritedFromActivity(element: Element): Boolean {
        return activityTypes.any {
            environmentProvider.typeUtils().isSubtype(element.asType(), it.asType())
        }
    }

    private fun isInheritedFromFragment(element: Element): Boolean {
        return fragmentTypes.any {
            environmentProvider.typeUtils().isSubtype(element.asType(), it.asType())
        }
    }
}
