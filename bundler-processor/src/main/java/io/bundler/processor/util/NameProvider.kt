package io.bundler.processor.util

import io.bundler.processor.model.ClassModel
import io.bundler.processor.model.FieldModel

object NameProvider {

    fun packageName(classModel: ClassModel?): String {
        return classModel?.packageName ?: "io.bundler"
    }

    fun injectorClassName(): String {
        return "AutoBundlerInjector"
    }

    fun injectMethodName(classModel: ClassModel? = null): String {
        return "inject${classModel?.className?.capitalize() ?: ""}"
    }

    fun bundlerClassName(classModel: ClassModel): String {
        return "${classModel.className.capitalize()}Bundler"
    }

    fun newIntentMethodName(classModel: ClassModel): String {
        return "${classModel.className.decapitalize()}Intent"
    }

    fun newInstanceMethodName(classModel: ClassModel): String {
        return "${classModel.className.decapitalize()}Instance"
    }

    fun bundleMethodName(classModel: ClassModel): String {
        return "${classModel.className.decapitalize()}Bundle"
    }

    fun bundleKeyName(classModel: ClassModel, fieldModel: FieldModel): String {
        return "KEY_${classModel.className.toSnakeCase()}_${fieldModel.name.toSnakeCase()}"
    }

    fun bundleKeyValue(fieldModel: FieldModel): String {
        return if (fieldModel.bundleKey.isNotEmpty()) {
            fieldModel.bundleKey
        } else {
            fieldModel.name
        }
    }
}
