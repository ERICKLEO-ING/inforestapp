package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteProvier @Inject constructor() {
    var quotes: List<QuoteModel> = emptyList()
}