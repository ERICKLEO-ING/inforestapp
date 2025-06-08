package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class MensajeImpresionModel(
    @SerializedName("status")
    val status: Int = 100,

    @SerializedName("codigopedido")
    var codigoPedido: String = "",

    @SerializedName("mensaje")
    val mensaje: String = "",

    @SerializedName("codigoimpresora")
    val codigoImpresora: String = "",

    @SerializedName("usuario")
    var usuario: String = "",

    @SerializedName("impresion")
    val impresion: String = ""
)
