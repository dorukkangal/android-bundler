package io.bundler

/**
 * The root class to inject arguments to the target class
 */
object BundlerInjector : Injector<Any> {

    private const val AUTO_MAPPING_CLASS_NAME = "AutoBundlerInjector"
    private const val AUTO_MAPPING_PACKAGE = "io.bundler"
    private const val AUTO_MAPPING_QUALIFIED_CLASS = "$AUTO_MAPPING_PACKAGE.$AUTO_MAPPING_CLASS_NAME"

    private val autoMappingInjector: Injector<Any>? by getAutoMappingInjector()

    override fun inject(target: Any) {
        autoMappingInjector?.inject(target)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAutoMappingInjector(): Lazy<Injector<Any>?> = lazy {
        try {
            val clazz = Class.forName(AUTO_MAPPING_QUALIFIED_CLASS)
            clazz.newInstance() as Injector<Any>
        } catch (e: Exception) {
            val wrapped = Exception("Could not load the generated auto mapping class.", e)
            wrapped.printStackTrace()
            null
        }
    }
}
