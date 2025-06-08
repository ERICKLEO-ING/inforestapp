package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("result") val result: String,
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("hora") val hora: String = ""
)
