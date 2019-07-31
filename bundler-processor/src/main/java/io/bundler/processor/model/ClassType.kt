package io.bundler.processor.model

enum class ClassType(val definedClasses: Array<String>) {
    ACTIVITY(
            arrayOf("android.app.Activity")
    ),
    FRAGMENT(
            arrayOf(
                    "android.app.Fragment",
                    "android.support.v4.app.Fragment",
                    "androidx.fragment.app.Fragment"
            )
    );
}
