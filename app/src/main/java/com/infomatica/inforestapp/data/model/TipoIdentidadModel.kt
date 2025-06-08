package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class TipoIdentidadModel(
    @SerializedName("codigotopping") val codigo: String = "",
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("tResumido") val tResumido: String = "",
    @SerializedName("nLongitud") val nLongitud: Int = 0,
    @SerializedName("lTipoDato") val lTipoDato: Int = 0
)