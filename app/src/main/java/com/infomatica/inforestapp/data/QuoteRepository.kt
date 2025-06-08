package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.QuoteModel
import com.infomatica.inforestapp.data.model.QuoteProvier
import com.infomatica.inforestapp.data.network.QuoteService
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val service : QuoteService,
    private  val provider: QuoteProvier ) {

    suspend fun getAllQuotes():List<QuoteModel>{
        val response = service.getQuotes()
        provider.quotes = response
        return response
    }
}