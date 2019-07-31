package io.bundler.processor.writer

import android.os.Bundle
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.util.NameProvider
import io.bundler.processor.util.javaToKotlinType

internal abstract class ClassWriter(
        environmentProvider: EnvironmentProvider
) : Writer<ClassModel>(environmentProvider) {

    override fun write(file: FileSpec.Builder?, input: ClassModel) {
        if (file == null) return

        generateBundleKeyConstants(input).forEach {
            file.addProperty(it)
        }

        file.addFunction(generateInjectMethod(input))
        file.addFunction(generateNewInstanceMethod(input))

        file.addFunction(generateBundleMethod(input))
    }

    private fun generateInjectMethod(classModel: ClassModel): FunSpec {
        val function = FunSpec.builder(NameProvider.injectMethodName(classModel))

        function.addParameter(generateParam("target", classModel.typeName))

        if (classModel.fieldModels.isNotEmpty()) {
            function.addCode(generateBundleGetStatement(classModel))
        }

        classModel.fieldModels.forEach { fieldModel ->
            function.addStatement("target.%L = args.%L<%L>(%L)",
                    fieldModel.name,
                    if (fieldModel.isRequired) "extraNotNull" else "extra",
                    fieldModel.typeName.javaToKotlinType().toString(),
                    NameProvider.bundleKeyName(classModel, fieldModel)
            )
        }

        return function.build()
    }

    private fun generateBundleMethod(classModel: ClassModel): FunSpec {
        val function = FunSpec.builder(NameProvider.bundleMethodName(classModel))
                .returns(Bundle::class)

        classModel.fieldModels.forEach { fieldModel ->
            function.addParameter(generateParam(fieldModel))
        }

        function.addStatement("val bundle = io.bundler.bundleOf(")
                .indent(2)

        classModel.fieldModels.forEachIndexed { index, fieldModel ->
            val lastIndex = index == classModel.fieldModels.lastIndex

            function.addStatement("%L to %L${if (lastIndex) "" else ","}", NameProvider.bundleKeyName(classModel, fieldModel), fieldModel.name)
        }

        function.unindent(2)
                .addStatement(")")
                .addStatement("return bundle")

        return function.build()
    }

    private fun generateBundleKeyConstants(classModel: ClassModel): List<PropertySpec> {
        val properties = arrayListOf<PropertySpec>()

        classModel.fieldModels.forEach { fieldModel ->
            val property = PropertySpec.builder(NameProvider.bundleKeyName(classModel, fieldModel), String::class)
                    .addModifiers(KModifier.CONST)
                    .initializer("\"${NameProvider.bundleKeyValue(fieldModel)}\"")
                    .build()

            properties.add(property)
        }

        return properties
    }

    abstract fun generateNewInstanceMethod(classModel: ClassModel): FunSpec

    abstract fun generateBundleGetStatement(classModel: ClassModel): CodeBlock
}
