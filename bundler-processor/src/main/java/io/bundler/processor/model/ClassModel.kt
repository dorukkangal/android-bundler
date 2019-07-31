package io.bundler.processor.model

import com.squareup.kotlinpoet.TypeName

data class ClassModel(
        val packageName: String,
        val className: String,
        val typeName: TypeName,
        val classType: ClassType,
        val fieldModels: List<FieldModel>
)
