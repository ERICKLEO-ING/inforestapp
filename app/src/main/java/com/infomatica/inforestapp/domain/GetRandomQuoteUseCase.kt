package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.QuoteModel
import com.infomatica.inforestapp.data.model.QuoteProvier
import javax.inject.Inject

class GetRandomQuoteUseCase @Inject constructor(private val quoteProvider: QuoteProvier) {
    operator fun invoke(): QuoteModel? {
        val quotes = quoteProvider.quotes
        quotes.let {
            val randomNumber = (quotes.indices).random()
            return quotes[randomNumber]
        }
    }
}