package io.bundler.processor.writer

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.util.NameProvider

internal class FragmentWriter(
        environmentProvider: EnvironmentProvider
) : ClassWriter(environmentProvider) {
    override fun generateBundleGetStatement(classModel: ClassModel): CodeBlock {
        return CodeBlock.of("val args = target.arguments\n")
    }

    override fun generateNewInstanceMethod(classModel: ClassModel): FunSpec {
        val function = FunSpec.builder(NameProvider.newInstanceMethodName(classModel))
                .returns(classModel.typeName)

        classModel.fieldModels.forEach { fieldModel ->
            function.addParameter(generateParam(fieldModel))
        }

        function.addStatement("val instance = %L()", classModel.typeName)
                .indent()
                .addStatement(".apply {")
                .indent()
                .addStatement("arguments = %L(", NameProvider.bundleMethodName(classModel))
                .indent()

        classModel.fieldModels.forEachIndexed { index, fieldModel ->
            val lastIndex = index == classModel.fieldModels.lastIndex

            function.addStatement("%L=%L${if (lastIndex) "" else ","}", fieldModel.name, fieldModel.name)
        }

        function.unindent()
                .addStatement(")")
                .unindent()
                .addStatement("}")
                .unindent()
                .addStatement("return instance")

        return function.build()
    }
}
