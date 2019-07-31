package io.bundler.processor

import io.bundler.annotation.Arg
import io.bundler.annotation.Bundler
import io.bundler.processor.mapper.BundlerMapper
import io.bundler.processor.model.ProcessingException
import io.bundler.processor.writer.BundlerWriter
import io.bundler.processor.writer.InjectorWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

class BundlerProcessor : AbstractProcessor(), EnvironmentProvider {

    private lateinit var processingEnvironment: ProcessingEnvironment

    private lateinit var bundlerMapper: BundlerMapper
    private lateinit var bundlerWriter: BundlerWriter
    private lateinit var injectorWriter: InjectorWriter

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)

        this.processingEnvironment = processingEnvironment

        bundlerMapper = BundlerMapper(this)
        bundlerWriter = BundlerWriter(this)
        injectorWriter = InjectorWriter(this)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(Bundler::class.java.canonicalName, Arg::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (roundEnvironment?.errorRaised() == false && !roundEnvironment.processingOver()) {
            processRound(roundEnvironment)
        }
        return false
    }

    private fun processRound(roundEnvironment: RoundEnvironment) {
        try {
            val models = roundEnvironment.getElementsAnnotatedWith(Bundler::class.java)
                    ?.mapNotNull {
                        bundlerMapper.map(it)
                    } ?: emptyList()

            bundlerWriter.write(input = models)
            injectorWriter.write(input = models)

        } catch (e: ProcessingException) {
            error(e)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun typeUtils(): Types = processingEnvironment.typeUtils

    override fun elementUtils(): Elements = processingEnvironment.elementUtils

    override fun filer(): Filer = processingEnvironment.filer

    override fun error(exception: ProcessingException) {
        processingEnvironment.messager.printMessage(
                Diagnostic.Kind.ERROR,
                exception.message,
                exception.element
        )
    }
}
