package com.infomatica.inforestapp.data.model
import com.google.gson.annotations.SerializedName

data class ClienteFacturadoModel(
    @SerializedName("codigo") var codigo: String,
    @SerializedName("descripcion") var descripcion: String = "",
    @SerializedName("tidentidad") var tidentidad: String = "",
    @SerializedName("tdireccion") val tdireccion: String = "",
    @SerializedName("tcorreo") var tcorreo: String = "",
    @SerializedName("ttipoidentidad") var ttipoidentidad: String = "",
    @SerializedName("tipoidentidad") var tipoidentidad: String = "",
    @SerializedName("documento") var documento: String = "BOLETA",//
)
