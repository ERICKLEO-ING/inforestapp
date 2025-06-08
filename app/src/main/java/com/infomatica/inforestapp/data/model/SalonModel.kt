package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class SalonModel(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("mesas") val mesas: List<MesasModel> = emptyList(),
)
