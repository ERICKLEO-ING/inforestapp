package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.QuoteRepository
import com.infomatica.inforestapp.data.model.QuoteModel
import javax.inject.Inject

class GetQuotesuseCase @Inject constructor( private val  repository:QuoteRepository) {

    suspend operator fun invoke(): List<QuoteModel>?= repository.getAllQuotes()

}