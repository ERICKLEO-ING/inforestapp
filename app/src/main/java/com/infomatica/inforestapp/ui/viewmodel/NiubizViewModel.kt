package com.infomatica.inforestapp.ui.viewmodel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.infomatica.inforestapp.data.model.CheckActivityResultDataNiubiz
import com.infomatica.inforestapp.data.model.TransactionParamsNiubiz
import com.infomatica.inforestapp.extension.ConstansNiubiz
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTCALLER
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.CLOSING
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.COPY_VOUCHER
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.DUPE
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.INIT
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.PRINT
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.PRINT_IMAGE
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.SALE
import com.infomatica.inforestapp.extension.ConstansNiubiz.EXTOP.VOID
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_CODE.CANCEL_OPERATION
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_CODE.FAIL_IN
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_CODE.NOT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_CODE.SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_CODE.SUCCESS_IN
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.CANCEL_BY_USER
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.CLOSING_NOT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.CLOSING_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.COPY_VOUCHER_NOT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.COPY_VOUCHER_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.DUPE_NOT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.DUPE_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.EMPTY_LOTE
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.INIT_FAIL
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.INIT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.SALE_NOT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.SALE_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.UNKNOWN
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.VOID_NOT_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.RESPONSE_MESSAGE.VOID_SUCCESS
import com.infomatica.inforestapp.extension.ConstansNiubiz.VNP_REQUEST_CODE
import com.infomatica.inforestapp.extension.getStringToPrintTest
import com.infomatica.inforestapp.extension.toAmountString
import com.infomatica.inforestapp.extension.toExtCom
import java.lang.StringBuilder

const val TAG = "NiubizViewModel"

class NiubizViewModel : ViewModel() {
    /**
     * NO SE HA IMPLEMENTADO EL LLAMADO A IMPRESORA
     */

    var message = MutableLiveData<String?>(null)
    var response = MutableLiveData<String?>(null)
    var currentCode = 0

    var merchantSelected = ""

    init {
        message.postValue(null)
        response.postValue(null)
    }

    private fun checkAmount(amount: Double): Boolean {
        return if (amount <= 0.0) {
            message.postValue("El monto debe ser mayor a 0")
            false
        } else true
    }

//    fun sendTestPrint(activity: Activity) {
//        currentCode = PRINT
//        val route =
//            "posweb://transact/?EXTCALLER=${EXTCALLER}&EXTOP=${PRINT}&EXTMONTO=0&EXTHEAD=&EXTTAIL=&EXTBODY=${getStringToPrintTest()}"
//        Log.e(TAG, route)
//        callAppNiubiz(activity, route)
//    }
//    fun sendPrintPrecuenta(activity: Activity, textoPrecuenta: String) {
//        currentCode = PRINT
//        val route =
//            "posweb://transact/?EXTCALLER=${EXTCALLER}&EXTOP=${PRINT}&EXTMONTO=0&EXTBODY=${textoPrecuenta}"
//        Log.e(TAG, route)
//        callAppNiubiz(activity, route)
//    }

//    fun sendTestPrintImage(activity: Activity) {
//        currentCode = PRINT_IMAGE
//        val route =
//            "posweb://transact/?EXTCALLER=${EXTCALLER}&EXTOP=${PRINT_IMAGE}&EXTPRINTIMAGE=${ConstansNiubiz.PRINT_IMAGE.TEST_BASE64}"
//        Log.e(TAG, route)
//        callAppNiubiz(activity, route)
//    }

    fun sendEcho(activity: Activity, code: Int, merchantCode: String? = null) {
        currentCode = code
//        val route = "aadr://echo"
        val route = "posweb://transact/?EXTCALLER=${EXTCALLER}&EXTOP=${code}&EXTMONTO=0${merchantCode.toExtCom()}"
        callAppNiubiz(activity, route)
    }

    fun sendAADR(activity: Activity, code: Int) {
        currentCode = code
        val route =
            "aadr://transact/?EXLIS=COM:750000438^SER:33700030^LOT:4^REF:25;COM:750006477^SER:47147418^LOT:2^REF:15"
        callAppNiubiz(activity, route)
    }

    fun sendNotSale(activity: Activity, code: Int, merchantCode: String? = null) {
        currentCode = code
        val route =
            "posweb://transact/?EXTCALLER=${EXTCALLER}&EXTOP=${code}&EXTMONTO=0${merchantCode.toExtCom()}"
        callAppNiubiz(activity, route)
    }

    fun sendSale(activity: Activity, amount: Double, isQr: Boolean, merchantCode: String? = null) {
        if (checkAmount(amount)) {
            currentCode = SALE
            var route =
                "posweb://transact/?EXTCALLER=${EXTCALLER}&EXTOP=${SALE}&EXTMONTO=${amount.toAmountString()}${merchantCode.toExtCom()}"
            if (isQr) route += "&EXTFORQR=1"
            callAppNiubiz(activity, route)
        }
    }

    private fun callAppNiubiz(activity: Activity, route: String) {
        Log.e("route", route)
        activity.startActivityForResult(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(route)
            }, VNP_REQUEST_CODE
        )
    }

//    fun checkActivityResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
//        var finalResponse: String? = null // Variable para almacenar la respuesta final
//
//        if (requestCode == VNP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Log.e("RESPONSE", "=${data}=")
//            var mPWRIPARAMS = ""
//            var mCODE = ""
//            val props = data?.extras
//            Log.e("props", "=$props")
//
//            if (data?.action != null && (data.action.equals(Intent.ACTION_VIEW)
//                        || data.action.equals(Intent.ACTION_MAIN))
//            ) {
//                data.data?.let {
//                    mPWRIPARAMS = it.encodedQuery ?: ""
//                }
//            } else if (props != null) {
//                mPWRIPARAMS = props.getString("PWRIPARAMS") ?: ""
//                mCODE = props.getString("CODE") ?: ""
//            }
//
//            if (mPWRIPARAMS.isNotEmpty()) {
//                Log.e(TAG, "mPWRIPARAMS = $mPWRIPARAMS")
//                //EXTCALLER=niubiz&EXTOP=01
//                val res = mPWRIPARAMS.substringAfter("EXTOP=").take(2)
//                if (res.isNotEmpty()) {
//                    val resMessage = validateResponse(res)
//                    finalResponse = StringBuilder()
//                        .append(resMessage)
//                        .append(System.getProperty("line.separator"))
//                        .append(System.getProperty("line.separator"))
//                        .append(mPWRIPARAMS)
//                        .toString()
//
//                    response.postValue(finalResponse) // Publicar el valor
//                }
//            }
//            if (mCODE.isNotEmpty()) {
//                Log.e(TAG, "mCODE = $mCODE")
//                finalResponse = StringBuilder()
//                    .append("mCODE=$mCODE")
//                    .toString()
//
//                response.postValue(finalResponse) // Publicar el valor
//            }
//            Log.e(TAG, "checkActivityResult = $finalResponse")
//        }
//
//        return finalResponse // Retornar la respuesta final
//    }

//    private fun validateResponse(res: String): String {
//        val messageResponse = when (currentCode) {
//            SALE -> {
//                when (res) {
//                    SUCCESS -> SALE_SUCCESS
//                    NOT_SUCCESS -> SALE_NOT_SUCCESS
//                    CANCEL_OPERATION -> CANCEL_BY_USER
//                    else -> UNKNOWN
//                }
//            }
//
//            VOID -> {
//                when (res) {
//                    SUCCESS -> VOID_SUCCESS
//                    NOT_SUCCESS -> VOID_NOT_SUCCESS
//                    CANCEL_OPERATION -> CANCEL_BY_USER
//                    else -> UNKNOWN
//                }
//            }
//
//            CLOSING -> {
//                when (res) {
//                    SUCCESS -> CLOSING_SUCCESS
//                    NOT_SUCCESS -> CLOSING_NOT_SUCCESS
//                    "08" -> EMPTY_LOTE
//                    else -> UNKNOWN
//                }
//            }
//
//            COPY_VOUCHER -> {
//                when (res) {
//                    SUCCESS -> COPY_VOUCHER_SUCCESS
//                    NOT_SUCCESS -> COPY_VOUCHER_NOT_SUCCESS
//                    else -> UNKNOWN
//                }
//            }
//
//            DUPE -> {
//                when (res) {
//                    SUCCESS -> DUPE_SUCCESS
//                    NOT_SUCCESS -> DUPE_NOT_SUCCESS
//                    else -> UNKNOWN
//                }
//            }
//
//            INIT -> {
//                when (res) {
//                    SUCCESS_IN -> INIT_SUCCESS
//                    FAIL_IN -> INIT_FAIL
//                    else -> UNKNOWN
//                }
//            }
//
//            else -> UNKNOWN
//        }
//        return messageResponse
//    }

    fun checkActivityResult(requestCode: Int, resultCode: Int, data: Intent?): CheckActivityResultDataNiubiz? {
        if (requestCode != VNP_REQUEST_CODE || resultCode != Activity.RESULT_OK) {
            return null
        }

        Log.e("RESPONSE", "=${data}=")

        var rawParams = ""
        var errorCode = ""
        val props = data?.extras
        Log.e("props", "=$props")

        // Obtener parámetros desde Intent
        if (data?.action != null && (data.action == Intent.ACTION_VIEW || data.action == Intent.ACTION_MAIN)) {
            data.data?.let {
                rawParams = it.encodedQuery ?: ""
            }
        } else if (props != null) {
            rawParams = props.getString("PWRIPARAMS") ?: ""
            errorCode = props.getString("CODE") ?: ""
        }

        val extractedParams = extractTransactionParams(rawParams)
        val responseMessage = extractedParams?.let { it.operationType?.let { it1 ->
            validateResponse(
                it1
            )
        } } ?: errorCode.takeIf { it.isNotEmpty() }?.let { "Error Code: $errorCode" }

        val result = CheckActivityResultDataNiubiz(
            requestCode = requestCode,
            resultCode = resultCode,
            responseMessage = responseMessage,
            transactionParams = extractedParams,
            errorCode = if (errorCode.isNotEmpty()) errorCode else null
        )

        response.postValue(result.responseMessage)
        Log.e(TAG, "checkActivityResult = $result")

        return result
    }

    private fun validateResponse(res: String): String {
        val messageResponse = when (currentCode) {
            SALE -> {
                when (res) {
                    SUCCESS -> SALE_SUCCESS
                    NOT_SUCCESS -> SALE_NOT_SUCCESS
                    CANCEL_OPERATION -> CANCEL_BY_USER
                    else -> UNKNOWN
                }
            }

            VOID -> {
                when (res) {
                    SUCCESS -> VOID_SUCCESS
                    NOT_SUCCESS -> VOID_NOT_SUCCESS
                    CANCEL_OPERATION -> CANCEL_BY_USER
                    else -> UNKNOWN
                }
            }

            CLOSING -> {
                when (res) {
                    SUCCESS -> CLOSING_SUCCESS
                    NOT_SUCCESS -> CLOSING_NOT_SUCCESS
                    "08" -> EMPTY_LOTE
                    else -> UNKNOWN
                }
            }

            COPY_VOUCHER -> {
                when (res) {
                    SUCCESS -> COPY_VOUCHER_SUCCESS
                    NOT_SUCCESS -> COPY_VOUCHER_NOT_SUCCESS
                    else -> UNKNOWN
                }
            }

            DUPE -> {
                when (res) {
                    SUCCESS -> DUPE_SUCCESS
                    NOT_SUCCESS -> DUPE_NOT_SUCCESS
                    else -> UNKNOWN
                }
            }

            INIT -> {
                when (res) {
                    SUCCESS_IN -> INIT_SUCCESS
                    FAIL_IN -> INIT_FAIL
                    else -> UNKNOWN
                }
            }

            else -> UNKNOWN
        }
        return messageResponse
    }

    private fun extractTransactionParams(query: String): TransactionParamsNiubiz? {
        if (query.isEmpty()) return null

        val paramsMap = query.split("&").associate {
            val parts = it.split("=")
            if (parts.size == 2) parts[0] to parts[1] else parts[0] to ""
        }

        return TransactionParamsNiubiz(
            operationType = paramsMap["EXTOP"],   // Extrae el código de la operación
            transactionId = paramsMap["IDU"],
            merchantId = paramsMap["COM"],
            terminalSerial = paramsMap["SER"],
            maskedCardNumber = paramsMap["TAR"],
            cardBrand = paramsMap["BAN"],
            batchNumber = paramsMap["LOT"],
            referenceNumber = paramsMap["REF"],
            authorizationCode = paramsMap["CAP"],
            transactionAmount = paramsMap["IMP"],
            tipAmount = paramsMap["PRO"],
            isQrPayment = paramsMap["IQR"],
            transactionDate = paramsMap["FEC"],
            transactionTime = paramsMap["HOR"],
            currencyCode = paramsMap["MON"]
        )
    }



}