package io.bundler

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Returns a new [Bundle] with the given key/value pairs as elements.
 *
 * @throws IllegalArgumentException When a value is not a supported type of [Bundle].
 */
fun bundleOf(vararg pairs: Pair<String, Any?>) = Bundle(pairs.size).apply {
    for ((key, value) in pairs) {
        when (value) {
            null -> putString(key, null) // Any nullable type will suffice.

            // Scalars
            is Boolean -> putBoolean(key, value)
            is Byte -> putByte(key, value)
            is Char -> putChar(key, value)
            is Double -> putDouble(key, value)
            is Float -> putFloat(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Short -> putShort(key, value)

            // References
            is Bundle -> putBundle(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Parcelable -> putParcelable(key, value)

            // Scalar arrays
            is BooleanArray -> putBooleanArray(key, value)
            is ByteArray -> putByteArray(key, value)
            is CharArray -> putCharArray(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is FloatArray -> putFloatArray(key, value)
            is IntArray -> putIntArray(key, value)
            is LongArray -> putLongArray(key, value)
            is ShortArray -> putShortArray(key, value)

            // Reference arrays
            is Array<*> -> {
                val componentType = value::class.java.componentType
                @Suppress("UNCHECKED_CAST") // Checked by reflection.
                when {
                    value.isArrayOf<Parcelable>() -> {
                        putParcelableArray(key, value as Array<Parcelable>)
                    }
                    value.isArrayOf<String>() -> {
                        putStringArray(key, value as Array<String>)
                    }
                    value.isArrayOf<CharSequence>() -> {
                        putCharSequenceArray(key, value as Array<CharSequence>)
                    }
                    value.isArrayOf<Serializable>() -> {
                        putSerializable(key, value)
                    }
                    else -> {
                        val valueType = componentType.canonicalName
                        throw IllegalArgumentException(
                                "Illegal value array type $valueType for key \"$key\"")
                    }
                }
            }

            // Reference arrays
            is ArrayList<*> -> {
                val componentType = value.firstOrNull()?.javaClass ?: Serializable::class.java
                @Suppress("UNCHECKED_CAST") // Checked by reflection.
                when {
                    Parcelable::class.java.isAssignableFrom(componentType) -> {
                        putParcelableArrayList(key, value as ArrayList<Parcelable>)
                    }
                    String::class.java.isAssignableFrom(componentType) -> {
                        putStringArrayList(key, value as ArrayList<String>)
                    }
                    Int::class.java.isAssignableFrom(componentType) -> {
                        putIntegerArrayList(key, value as ArrayList<Int>)
                    }
                    CharSequence::class.java.isAssignableFrom(componentType) -> {
                        putCharSequenceArrayList(key, value as ArrayList<CharSequence>)
                    }
                    Serializable::class.java.isAssignableFrom(componentType) -> {
                        putSerializable(key, value)
                    }
                    else -> {
                        val valueType = componentType.canonicalName
                        throw IllegalArgumentException(
                                "Illegal value array type $valueType for key \"$key\"")
                    }
                }
            }

            // Last resort. Also we must check this after Array<*> as all arrays are serializable.
            is Serializable -> putSerializable(key, value)
        }
    }
}

inline fun <reified T : Any> Bundle?.extra(key: String): T? {
    return this?.get(key)as? T
}

inline fun <reified T : Any> Bundle?.extraNotNull(key: String): T {
    return this.extra(key)as? T
            ?: throw RuntimeException("No value can be found in bundle with associated key: $key")
}
