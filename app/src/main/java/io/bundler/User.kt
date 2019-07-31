package io.bundler

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        val name: String,
        val surname: String
) : Parcelable
