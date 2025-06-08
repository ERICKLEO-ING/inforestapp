package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class CanalVentaModel(
    @SerializedName("codigo") val Codigo: String = "01",
    @SerializedName("descripcion") val Descripcion: String = "-",
    @SerializedName("lactivamozo") val lActivaMozo: Boolean = false,
    @SerializedName("lactivamotorizado") val lActivaMotorizado: Boolean = false,
    @SerializedName("lobligamesa") val lObligaMesa: Boolean = false,
    @SerializedName("lobligapax") val lObligaPax: Boolean = false,
    @SerializedName("lobligamozo") val lObligaMozo: Boolean = false,
    @SerializedName("lobligamotorizado") val lObligaMotorizado: Boolean = false,
    @SerializedName("lcanalcentralpedidos") val lCanalCentralPedidos: Boolean = false,
    @SerializedName("lcanaldelivery") val lCanalDelivery: Boolean = false,
    @SerializedName("lobligaingresofechaentrega") val lObligaIngresoFechaEntrega: Boolean = false,
    @SerializedName("lobligaclientefrecuente") val lObligaClienteFrecuente: Boolean = false,
    @SerializedName("lactivo") val lActivo: Boolean = false,
//    @SerializedName("lobligaentregara") val lObligaEntregarA: Boolean = false,
//    @SerializedName("tenlacecontable1") val tEnlaceContable1: String? = null,
//    @SerializedName("tenlacecontable2") val tEnlaceContable2: String? = null,
//    @SerializedName("tcodigosunat") val tCodigoSunat: String? = null,
//    @SerializedName("lobligaorigenventa") val lObligaOrigenVenta: Boolean = false
)
