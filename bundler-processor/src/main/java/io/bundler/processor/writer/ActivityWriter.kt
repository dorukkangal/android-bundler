package io.bundler.processor.writer

import android.content.Context
import android.content.Intent
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.util.NameProvider

internal class ActivityWriter(
        environmentProvider: EnvironmentProvider
) : ClassWriter(environmentProvider) {

    override fun generateBundleGetStatement(classModel: ClassModel): CodeBlock {
        return CodeBlock.of("val args = target.intent?.extras\n")
    }

    override fun generateNewInstanceMethod(classModel: ClassModel): FunSpec {
        val function = FunSpec.builder(NameProvider.newIntentMethodName(classModel))
                .returns(Intent::class)

        function.addParameter(generateParam("context", Context::class.asTypeName()))

        classModel.fieldModels.forEach { fieldModel ->
            function.addParameter(generateParam(fieldModel))
        }

        function.addStatement("val intent = Intent(context, %L::class.java)", classModel.typeName)
                .indent()
                .addStatement(".apply {")
                .indent()
                .addStatement("putExtras(")
                .indent()
                .addStatement("%L(", NameProvider.bundleMethodName(classModel))
                .indent()

        classModel.fieldModels.forEachIndexed { index, fieldModel ->
            val lastIndex = index == classModel.fieldModels.lastIndex

            function.addStatement("%L = %L${if (lastIndex) "" else ","}", fieldModel.name, fieldModel.name)
        }

        function.unindent()
                .addStatement(")")
                .unindent()
                .addStatement(")")
                .unindent()
                .addStatement("}")
                .unindent()
                .addStatement("return intent")

        return function.build()
    }
}
