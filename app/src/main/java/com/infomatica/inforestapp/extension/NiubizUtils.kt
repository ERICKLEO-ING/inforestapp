package com.infomatica.inforestapp.extension

import android.util.Log
import android.widget.EditText
import java.lang.NumberFormatException
import java.text.DecimalFormat
import org.apache.commons.lang3.StringUtils

fun EditText.toDouble(): Double {
    return try {
        this.text.toString().toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

fun StringBuilder.addLineBreak() {
    this.append("\n")
}

fun StringBuilder.addDoubleLineBreak() {
    this.append("\n${" ".repeat(34)}\n${" ".repeat(34)}\n")
}

fun StringBuilder.appendRowLeft(str: String) {
    this.append(String.format("%1$34s", str))
}

fun StringBuilder.appendRowRight(str: String) {
    this.append(String.format("%1$-34s", str))
}

fun StringBuilder.appendRowCenter(str: String) {
    this.append(StringUtils.center(str, 34, ' '))
}

fun StringBuilder.appendTwoWordsInRow(strStart: String, strEnd: String) {
    val length = strStart.length + strEnd.length
    val diff = 34 - length
    if (diff >= 0) {
        this.append("$strStart${" ".repeat(diff)}$strEnd")
    }
}

fun Double.toAmountString(): String {
    val format = DecimalFormat("#.00")
    Log.e("ASD", format.format(this))
    return format.format(this).replace(".", "").replace(",", "")
}

fun String?.toExtCom() = if (isNullOrEmpty()) "" else "&EXTCOM=$this"

fun getStringToPrintTest(): String {
    val builder = java.lang.StringBuilder()
    builder.appendRowCenter("NIUBIZ")
    builder.addLineBreak()
    builder.appendRowCenter("TIENDAS DEPARTAMENTO NIUBIZ C.A.")
    builder.addLineBreak()
    builder.appendRowCenter("Av. José Pardo 831 MIRAFLORES LIMA")
    builder.addLineBreak()
    builder.appendRowCenter("RUC 12345678901")
    builder.addLineBreak()
    builder.appendRowCenter("00577/064 11/09/20 14:18 1750")
    builder.addLineBreak()
    builder.appendRowCenter("3001288411 Quispe F.Pedro")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("TICKET NRO:", "091080")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("Doc Cliente:", "DNI 77777777")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("Cliente loyalty:", "DNI 77777777")
    builder.addDoubleLineBreak()
    builder.appendTwoWordsInRow("6789012345678 I20 POLO ESTAM", "49.95")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("1234567890123 I20KGJEME06COL", "54.98")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("2345678901234 V20GPIJDREAMSH", "19.95")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("n3456789012345 I20KGCVPY02COL", "59.98")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("4567890123456 INF C/T A/S HI", "139.90")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("5678901234567 BOLSA REGULAR.", "0.25")
    builder.addDoubleLineBreak()
    builder.appendTwoWordsInRow("SUBTOTAL", "S/ 325.01")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("OP. GRAVADAS", "275.43")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("I.G.V.(18)", "49.58")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("TOTAL A PAGAR", "S/ 325.01")
    builder.addLineBreak()
    builder.appendRowLeft("SON: TRESCIENTOS VEINTICINCO 1/100")
    builder.addLineBreak()
    builder.appendRowLeft("SOLES")
    builder.addDoubleLineBreak()
    builder.appendTwoWordsInRow("TARJ NIUBIZ 525435******1263", "325.01")
    builder.addDoubleLineBreak()
    builder.appendTwoWordsInRow("MONTO", "325.01")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("PRIMER VENCIMIENTO", "05/11/2020")
    builder.addLineBreak()
    builder.appendTwoWordsInRow("VALOR CUOTA (S/)", "183.21")
    builder.addLineBreak()
    builder.appendRowLeft("CUOTAS: 2")
    builder.addDoubleLineBreak()
    builder.appendRowLeft("Sr/a APELLIDO PREINSCRIPTO")
    builder.addLineBreak()
    builder.appendRowLeft("UD. GANO 260 PTOS. A ABONAR EN 48H")
    builder.addLineBreak()
    builder.appendRowLeft("LIMITE ACUMULACION WWW.NIUBIZ.PE")
    builder.addLineBreak()
    builder.appendRowLeft("Ud tiene Disp Efec Exp S/ 1,200.00")
    builder.addDoubleLineBreak()
    builder.appendRowCenter("GRACIAS POR SU COMPRA")
    builder.addDoubleLineBreak()
    builder.appendRowCenter("==================================")
    return builder.toString()
}