package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class MesasModel(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre:String,
    @SerializedName("mesa") val mesa:String = "",
    @SerializedName("pax") val pax:Int= 0,
    @SerializedName("isfumador") val isfumador:Boolean = false,
    @SerializedName("codigoestado") val codigoestado:String = "",
    @SerializedName("estado") val estado:String = "",
    @SerializedName("color") val color:String = "",
    @SerializedName("colortexto") val colortexto:String = "",
    @SerializedName("tiempo") val tiempo:String = "",
    @SerializedName("precuenta") val precuenta:Int =0,
    @SerializedName("codigopedido") val codigopedido: String = "")
