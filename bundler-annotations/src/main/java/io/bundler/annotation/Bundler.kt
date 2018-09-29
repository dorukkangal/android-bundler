package io.bundler.annotation

/**
 * Use this notation to mark the Fragment and Activity classes that contain the [Arg] annotation.
 * This annotation is required to run Annotation processing.
 *
 * @param inherited Indicates if [Arg] annotations of all super classes should be included.
 * The default value is true.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Bundler(
        val inherited: Boolean = true
)
