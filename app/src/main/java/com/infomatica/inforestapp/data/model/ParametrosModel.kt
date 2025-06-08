package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class ParametrosModel(
    @SerializedName("localnombre") val localnombre: String,
    @SerializedName("basedato") val basedato:String,
    @SerializedName("servidor") val servidor:String,
    @SerializedName("urlapp") val urlapp:String="",
    @SerializedName("clientegenerico") val clientegenerico:String="")
