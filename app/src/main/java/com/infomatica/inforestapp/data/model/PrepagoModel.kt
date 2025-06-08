package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class PrepagoModel(
    @SerializedName("codigo") var codigo: String,
    @SerializedName("documento") var documento: String="",
    @SerializedName("tipopago") val tipoPago: String = "02",
    @SerializedName("monto") val monto: Double= 0.00,
    @SerializedName("propina") val propina: Double = 0.00,
    @SerializedName("codigotarjeta") val codigoTarjeta: String = "",
    @SerializedName("numerotarjeta") val numeroTarjeta: String="",
    @SerializedName("referencia") val referencia: String="",
    @SerializedName("numeroatarjeta") val numeroATarjeta: String="",
    @SerializedName("banco") val banco: String=""
)