package io.bundler.processor.writer

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.bundler.Injector
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.util.NameProvider

internal class InjectorWriter(
        environmentProvider: EnvironmentProvider
) : Writer<List<ClassModel>>(environmentProvider) {

    override fun write(file: FileSpec.Builder?, input: List<ClassModel>) {
        val fileName = NameProvider.injectorClassName()
        val injectorFile = file(NameProvider.packageName(input.firstOrNull()), fileName)

        val injectorClass = TypeSpec.classBuilder(fileName)
                .addSuperinterface(Injector::class.parameterizedBy(Any::class))

        injectorClass.addFunction(generateInjectMethod(input))

        injectorFile.addType(injectorClass.build())
                .build()
                .writeTo(environmentProvider.filer())
    }

    private fun generateInjectMethod(classModels: List<ClassModel>): FunSpec {
        val function = FunSpec.builder(NameProvider.injectMethodName())
                .addParameter(generateParam("target", Any::class.java.asTypeName()))
                .addModifiers(KModifier.OVERRIDE)

        function.addStatement("val targetClass = target.javaClass")

        function.beginControlFlow("when (targetClass)")
        classModels.forEach { classModel ->
            function.addCode(generateInjectStatement(classModel))
        }
        function.endControlFlow()

        return function.build()
    }

    private fun generateInjectStatement(classModel: ClassModel): CodeBlock {
        return CodeBlock.Builder()
                .addStatement("%L::class.java -> {", classModel.typeName.toString())
                .indent()
                .addStatement("%L(target as %L)", NameProvider.injectMethodName(classModel), classModel.typeName.toString())
                .unindent()
                .addStatement("}")
                .build()
    }
}
