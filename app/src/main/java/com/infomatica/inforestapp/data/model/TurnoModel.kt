package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class TurnoModel(
    @SerializedName("codigo") val codigo: String = "",
    @SerializedName("caja") val caja: String = "",
    @SerializedName("usuario") val usuario: String = "",
    @SerializedName("apertura") val apertura: String = "",
    @SerializedName("ventas") val ventas: Double = 0.0,
    @SerializedName("propina") val propina: Double = 0.0,
    @SerializedName("mesas") val mesas: Double = 0.0,
    @SerializedName("pedidos") val pedidos: Double = 0.0,
    @SerializedName("ticketpromedio") val ticketPromedio: Double = 0.0,
    @SerializedName("consumopromediocomensal") val consumoPromedioComensal: Double = 0.0,
    @SerializedName("consumopromediopormesa") val consumoPromedioPorMesa: Double = 0.0,
    @SerializedName("moneda") val moneda: String = ""
)