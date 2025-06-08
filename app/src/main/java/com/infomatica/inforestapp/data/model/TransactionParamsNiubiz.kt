package com.infomatica.inforestapp.data.model

data class TransactionParamsNiubiz(
    val operationType: String?,       // Tipo de operación (EXTOP)
    val transactionId: String?,       // ID único de la transacción
    val merchantId: String?,          // ID del comercio
    val terminalSerial: String?,      // Serie del terminal
    val maskedCardNumber: String?,    // Tarjeta enmascarada
    val cardBrand: String?,           // Marca de la tarjeta
    val batchNumber: String?,         // Número de lote
    val referenceNumber: String?,     // Número de referencia de la transacción
    val authorizationCode: String?,   // Código de autorización
    val transactionAmount: String?,   // Monto de la transacción
    val tipAmount: String?,           // Monto de propina
    val isQrPayment: String?,         // Indica si la venta fue con QR (1 = QR, 0 = Tarjeta)
    val transactionDate: String?,     // Fecha de la transacción (DDMMAA)
    val transactionTime: String?,     // Hora de la transacción (HHMMSS)
    val currencyCode: String?         // Código de moneda
)