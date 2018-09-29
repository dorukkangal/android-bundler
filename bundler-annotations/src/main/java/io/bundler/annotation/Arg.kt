package io.bundler.annotation

/**
 * Annotate every field that should be put into the bundle and read from it with this annotation.
 *
 * @param required Specifies if the argument is required (default) or not.
 * @param key Key in the arguments bundle, by default uses the field name.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class Arg(
        val key: String = "",
        val required: Boolean = false
)
