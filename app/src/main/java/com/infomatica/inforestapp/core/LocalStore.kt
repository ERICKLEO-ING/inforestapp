import android.content.Context
import android.content.SharedPreferences

object LocalStore {
    private const val PREF_NAME = "AppPreferences"
    private const val BASE_URL_KEY = "BaseUrl"
    private const val PrinterBluetooth = "PrinterBluetooth"
    private const val PrinterBluetoothDocumento = "PrinterBluetoothDocumento"
    private const val PrinterBluetoothPrecuenta = "PrinterBluetoothPrecuenta"

    private const val PrinterBluetoothDocumentoWindow = "PrinterBluetoothDocumentoWindow"
    private const val PrinterBluetoothPrecuentaWindow = "PrinterBluetoothPrecuentaWindow"

    fun saveBaseUrl(context: Context, baseUrl: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(BASE_URL_KEY, baseUrl).apply()
    }
    fun getBaseUrl(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(BASE_URL_KEY, "http://161.132.106.116/api/") ?: "http://161.132.106.116/api/"
    }

    fun savePrinterBluetooth(context: Context, dato: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PrinterBluetooth, dato).apply()
    }
    fun getPrinterBluetooth(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PrinterBluetooth, "") ?: ""
    }

    fun savePrinterBluetoothDocumento(context: Context, dato: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PrinterBluetoothDocumento, dato).apply()
    }
    fun getPrinterBluetoothDocumento(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PrinterBluetoothDocumento, "0") ?: "0"
    }

    fun savePrinterBluetoothPrecuenta(context: Context, dato: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PrinterBluetoothPrecuenta, dato).apply()
    }
    fun getPrinterBluetoothPrecuenta(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PrinterBluetoothPrecuenta, "0") ?: "0"
    }

    fun savePrinterBluetoothDocumentoWindow(context: Context, dato: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PrinterBluetoothDocumentoWindow, dato).apply()
    }
    fun getPrinterBluetoothDocumentoWindow(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PrinterBluetoothDocumentoWindow, "0") ?: "0"
    }

    fun savePrinterBluetoothPrecuentaWindow(context: Context, dato: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PrinterBluetoothPrecuentaWindow, dato).apply()
    }
    fun getPrinterBluetoothPrecuentaWindow(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PrinterBluetoothPrecuentaWindow, "0") ?: "0"
    }
}