package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    @SerializedName("usuario") val usuario: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("empresa") val empresa: String,
    @SerializedName("estado") val estado: Boolean,
    @SerializedName("token") val token: String,
    @SerializedName("vercuentas") val vercuentas: Double = 0.00,
    @SerializedName("lpasswordtransferencia") val lpasswordtransferencia: Boolean = false,
    @SerializedName("caja") val caja: String="",
    @SerializedName("activaefectivo") val activaefectivo: Boolean,
    @SerializedName("activaniubiz") val activaniubiz: Boolean,
    @SerializedName("dispositivo") val dispositivo: String="",
    @SerializedName("activacajero") val activacajero: Boolean,
    @SerializedName("pais") val pais: String="",
)