package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.QuoteModel
import retrofit2.Response
import retrofit2.http.GET

interface QuoteApiClient {
    @GET("/.json")
    suspend fun  getAllQuotes():Response<List<QuoteModel>>
}