package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.SalonModel
import retrofit2.Response
import retrofit2.http.GET

interface SalonApiClient {
    @GET("mesa/listasalon")
    suspend fun  getAllSalones(): Response<List<SalonModel>?>
}