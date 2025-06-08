package com.infomatica.inforestapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MensajeMeseroModel(
    @SerializedName("codigo") val codigo: String = "",
    @SerializedName("descripcion") val descripcion: String = ""
): Parcelable