package com.infomatica.inforestapp.data.model

data class CheckActivityResultDataNiubiz(
    val requestCode: Int,
    val resultCode: Int,
    val responseMessage: String?,
    val transactionParams: TransactionParamsNiubiz?,
    val errorCode: String?
)

