package io.bundler.processor.model

import com.squareup.kotlinpoet.TypeName

class FieldModel(
        val name: String,
        val bundleKey: String,
        val typeName: TypeName,
        val isRequired: Boolean = false
)
