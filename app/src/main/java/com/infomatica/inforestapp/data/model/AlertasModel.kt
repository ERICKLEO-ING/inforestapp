package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class AlertasModel(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("isalerta") val isalerta: Boolean = false,
    @SerializedName("tiempo") val tiempo: String,
    @SerializedName("mensaje") val mensaje:String)