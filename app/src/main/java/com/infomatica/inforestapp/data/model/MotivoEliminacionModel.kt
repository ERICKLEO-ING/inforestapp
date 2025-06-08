package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class MotivoEliminacionModel(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("detalle") val detalle:String,
    @SerializedName("resumido") val resumido:String)
