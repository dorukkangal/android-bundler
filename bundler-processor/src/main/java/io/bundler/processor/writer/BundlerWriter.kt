package io.bundler.processor.writer

import com.squareup.kotlinpoet.FileSpec
import io.bundler.processor.EnvironmentProvider
import io.bundler.processor.model.ClassModel
import io.bundler.processor.model.ClassType
import io.bundler.processor.util.NameProvider

internal class BundlerWriter(
        environmentProvider: EnvironmentProvider
) : Writer<List<ClassModel>>(environmentProvider) {

    private val activityWriter = ActivityWriter(environmentProvider)
    private val fragmentWriter = FragmentWriter(environmentProvider)

    override fun write(file: FileSpec.Builder?, input: List<ClassModel>) {
        input.forEach { classModel ->
            val fileName = NameProvider.bundlerClassName(classModel)
            val bundlerFile = file(NameProvider.packageName(classModel), fileName)

            when (classModel.classType) {
                ClassType.ACTIVITY -> {
                    activityWriter.write(bundlerFile, classModel)
                }
                ClassType.FRAGMENT -> {
                    fragmentWriter.write(bundlerFile, classModel)
                }
            }

            bundlerFile.build().writeTo(environmentProvider.filer())
        }
    }
}
