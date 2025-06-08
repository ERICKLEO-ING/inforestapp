package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName


data class OfertaModel(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("dias") val dias: String="",
    @SerializedName("hora") val hora: String="",
    @SerializedName("descuento") val descuento: String="",
    @SerializedName("urlimagen") val urlimagen: String="")