// BaseActivity.kt
package com.infomatica.inforestapp.ui

import android.app.Activity
import android.view.ViewGroup
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.infomatica.inforestapp.R
import dagger.hilt.android.AndroidEntryPoint

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.UUID
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import com.infomatica.inforestapp.ui.fragment.MensajeDialogFragment
import com.infomatica.inforestapp.ui.fragment.ObservacionDialogFragment
import kotlin.math.min

/** Categorías de tamaño de pantalla */
enum class ScreenCategory {
    SMALL,   // < 600dp
    SW600,   // ≥ 600dp && < 720dp
    SW720,   // ≥ 720dp && < 960dp
    SW960    // ≥ 960dp
}

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private var currentToast: Toast? = null // Variable para almacenar el Toast actual

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Manejar el resultado de la actividad lanzada, si es necesario
            if (result.resultCode == RESULT_OK) {
                // Procesa el resultado
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ocultarBarras() // Llamada automática a ocultarBarras
        // Listener para ocultar las barras del sistema al recuperar el foco de la ventana
        // Listener para detectar cambios en el tamaño de la ventana (como la aparición o desaparición del teclado)
        val decorView = window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener { ocultarBarras() }

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )
    }

    // Sobrescribimos onWindowFocusChanged para volver a ocultar las barras del sistema cuando la actividad recupere el foco
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            ocultarBarras()
        }
    }

    override fun onResume() {
        super.onResume()
        ocultarBarras()
    }

    private fun ocultarBarras() {
        val decorView = window.decorView

        // Verifica la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Para Android 11 (API 30) y superior
            window.setDecorFitsSystemWindows(false) // Evita que el sistema ajuste el diseño
            window.insetsController?.let { insetsController ->
                // Oculta las barras de navegación y de estado
                insetsController.hide(WindowInsets.Type.systemBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Para Android 10 (API 29) y versiones inferiores
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }


    fun isTablet(): Boolean {
        return (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    //region Métodos para tamaños

    /** Devuelve el smallest width en dp (API11+) */
    private fun screenSmallestWidthDp(): Int {
        val cfg = resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val sw = cfg.smallestScreenWidthDp
            if (sw != Configuration.SMALLEST_SCREEN_WIDTH_DP_UNDEFINED) sw
            else fallbackDp()
        } else {
            fallbackDp()
        }
    }

    /** Cálculo manual si no hay smallestScreenWidthDp */
    private fun fallbackDp(): Int {
        val dm: DisplayMetrics = resources.displayMetrics
        return (min(dm.widthPixels, dm.heightPixels) / dm.density).toInt()
    }

    /** Expuesto a las subclases: categoría de pantalla */
    protected fun getScreenCategory(): ScreenCategory {
        val swDp = screenSmallestWidthDp()
        return when {
            swDp >= 960 -> ScreenCategory.SW960
            swDp >= 720 -> ScreenCategory.SW720
            swDp >= 600 -> ScreenCategory.SW600
            else        -> ScreenCategory.SMALL
        }
    }

    //endregion Métodos para tamaños

    // Método utilitario para mostrar un Toast
    fun showToast(message: String) {
        // Si hay un Toast actualmente mostrado, lo cancelamos
        currentToast?.cancel()

        // Creamos un nuevo Toast y lo asignamos a currentToast
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    fun startActivity(actividad: Class<*>) {
        val intent = Intent(this, actividad)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
        pedidoLauncher.launch(intent, options)
    }

    // Finaliza Actividad
    fun finishActivity(value: String, unit: Int = 0) {
        setResult(Activity.RESULT_OK, Intent().putExtra("key", value)) // Establece el resultado
        finish() // Cierra la actividad
        overridePendingTransition(unit, R.anim.slide_out_right)
    }
    fun expand(view: View) {

//        view.isVisible = true
//        return

        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //val targetHeight = view.measuredHeight

        // Configuración inicial
        // view.layoutParams.height = 0
        view.isVisible = true

        // Animación para bajar el layout
        view.animate()
            .setDuration(500) // Duración de la animación
            .translationY(0f) // Mantiene la posición Y
            .alpha(1f) // Hacer visible
            .withEndAction {
                //view.layoutParams.height = targetHeight // Ajustar altura final
                //view.requestLayout() // Solicitar diseño
            }
            .start()

        // Incrementar la altura del layout
//        view.layoutParams.height = targetHeight
//        view.requestLayout()
    }

    fun collapse(view: View) {
        val initialHeight = view.height

        // Animación para subir y ocultar el layout
        view.animate()
            .setDuration(500) // Duración de la animación
            .translationY(-initialHeight.toFloat()) // Sube el layout
            .alpha(0f) // Desvanece el layout
            .withEndAction {
                view.isVisible = false // Oculta el layout al finalizar
                //view.layoutParams.height = 0 // Restablecer altura a 0
                //view.requestLayout() // Solicitar diseño
            }
            .start()
    }
    fun mostrarNumPadDialog(fragmentManager: FragmentManager, tipo: String ="", onResult: (Double) -> Unit) {

        val fragmentTag = "NumPadDialogFragment"

        // Verifica si el fragmento ya está agregado
        if (fragmentManager.findFragmentByTag(fragmentTag) != null) {
            return // Si ya existe, no lo abre nuevamente
        }

        val initialValue = "0.000" // El valor inicial que quieres mostrar en el modal

        val modal = NumPadDialogFragment.newInstance(initialValue,tipo) { result ->
            if (result.isNullOrBlank()) {
                return@newInstance
            }
            val cantidad = result.toDoubleOrNull()
            if (cantidad == null || cantidad < 0) {
                return@newInstance
            }

            onResult(cantidad) // Llama al callback con el resultado válido
        }

        // Mostrar el modal
        modal.show(fragmentManager, fragmentTag)
    }
    fun observacionDialog(fragmentManager: FragmentManager, observacion: String, onResult: (String) -> Unit) {
        val fragmentTag = "ObservacionDialogFragment"

        // Verifica si el fragmento ya está agregado
        if (fragmentManager.findFragmentByTag(fragmentTag) != null) {
            return // Si ya existe, no lo abre nuevamente
        }

        val initialValue = observacion // El valor inicial que quieres mostrar en el modal

        val modal = ObservacionDialogFragment.newInstance(initialValue) { result ->
            if (result.isNullOrBlank()) {
                return@newInstance
            }

            onResult(result.toString()) // Llama al callback con el resultado válido
        }

        // Mostrar el modal
        modal.show(fragmentManager, fragmentTag)
    }
    fun mensajeDialog(fragmentManager: FragmentManager, titulo: String,  descripcion: String, tipo: String = "mensaje", extra: String = "", onResult: (String, String) -> Unit) {
        val fragmentTag = "MensajeDialogFragment"

        // Verifica si el fragmento ya está agregado
        if (fragmentManager.findFragmentByTag(fragmentTag) != null) {
            return // Si ya existe, no lo abre nuevamente
        }

        val modal = MensajeDialogFragment.newInstance(titulo,descripcion,tipo,extra) { result, result2 ->
            if (result.isNullOrBlank()) {
                return@newInstance
            }
            onResult(result,result2)
        }
        // Mostrar el modal
        modal.show(fragmentManager, fragmentTag)
    }

    fun imprimirEnPOS(texto: String, qrData: String, activity: Activity) {
        try {
            val lineas = texto.lines()
            var qr = ""
            var textoArriba = ""
            var textoAbajo = ""

            val indiceQR = lineas.indexOfFirst { it.startsWith("IMPQR") }

            if (indiceQR != -1) {
                qr = lineas[indiceQR].replace("IMPQR", "").trim()
                textoArriba = lineas.take(indiceQR).joinToString("\n") // Líneas antes de "IMPQR"
                textoAbajo = lineas.drop(indiceQR + 1).joinToString("\n") // Líneas después de "IMPQR"
            } else {
                textoArriba = texto // Si no hay "IMPQR", el texto se queda en textoArriba
            }
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                println("Bluetooth no disponible o desactivado")
                return
            }

            // Verificar permisos en Android 12+ (API 31+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
                    println("Permiso BLUETOOTH_CONNECT requerido")
                    return
                }
            }

            val dispositivo: BluetoothDevice? = bluetoothAdapter.bondedDevices.find {
                it.name.contains("POS") || it.name.contains("Printer") // Ajusta el nombre de tu impresora
            }

            if (dispositivo == null) {
                println("No se encontró la impresora POS")
                return
            }

            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID común para impresoras POS
            val socket: BluetoothSocket? = dispositivo.createRfcommSocketToServiceRecord(uuid)


            socket?.connect()
            val outputStream: OutputStream = socket?.outputStream ?: return

            outputStream.write(byteArrayOf(0x1B, 0x61, 0x00)) // ESC a 0 (Izquierda)
            // Detectar ancho de la impresora (80mm, 50mm, 40mm)
            val anchoImpresora = 32 //detectarAnchoImpresora(outputStream)
            println("Ancho detectado: $anchoImpresora mm")

            //val base64Image = "iVBORw0KGgoAAAANSUhEUgAAADIAAAABCAIAAADgU1vGAAAAWklEQVRIDbXBAQEAAAABIP6PjO8qwlLDYcXQYAMMBhCZgYAlHAAAAAElFTkSuQmCC"
            //val imageBytes = base64ToImage(ConstansNiubiz.PRINT_IMAGE.TEST_BASE64)

            // Decodificar la imagen base64
//            val imageBytes = decodeBase64ToImage(ConstansNiubiz.PRINT_IMAGE.TEST_BASE64)
//            // Convertir los bytes a Bitmap
//            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//            // Convertir la imagen a monocromo
//            val monochromeBitmap = convertImageToMonochrome(bitmap, 384) // 384 es el ancho máximo para impresoras de 58 mm
//            // Convertir la imagen a comandos ESC/POS
//            val escPosCommands = convertBitmapToEscPos(monochromeBitmap)
//            outputStream.write(escPosCommands)

//            val drawable: Drawable = resources.getDrawable(R.drawable.logo_192x166, null)
//            imprimirDrawable(outputStream, drawable, 384)

            // Imprimir texto normal
            val bytesTexto = textoArriba.toByteArray(Charset.forName("GB18030"))

            //val bytesTexto = texto.toByteArray(Charset.forName("US-ASCII"))
            outputStream.write(bytesTexto)
            outputStream.write("\r\n".toByteArray()) // Salto de línea

            // Validar si hay datos para el QR y centrarlo
            if (!qr.isNullOrEmpty()) {
                imprimirQR(outputStream, qr, anchoImpresora)
            } else {
                println("No hay datos para imprimir el QR")
            }
            if (!textoAbajo.isNullOrEmpty()) {
                val bytesTextoabajo = textoAbajo.toByteArray(Charset.forName("GB18030"))
                outputStream.write(bytesTextoabajo)
                outputStream.write("\r\n".toByteArray()) // Salto de línea
                outputStream.write("\r\n".toByteArray()) // Salto de línea
            } else {
                println("No hay datos para imprimir el QR")
            }

            // Cortar papel al finalizar
            //cortarPapel(outputStream)

            outputStream.flush()
            socket.close()
        } catch (e: Exception) {
            showToast("Error al imprimir: ${e.message}")
        }
    }

    /**
     * Función para imprimir un código QR centrado
     */
    fun imprimirQR(outputStream: OutputStream, qrData: String, anchoImpresora: Int) {
        val qrSize = when (anchoImpresora) {
            80 -> 10 // QR más grande en impresoras de 80mm
            //50 -> 7  // QR mediano para impresoras de 50mm
            else -> 7 // QR pequeño para 40mm o desconocido
        }
        val qrModel = 49 // "Model 2", el más usado

        // Centrar QR
        outputStream.write(byteArrayOf(0x1B, 0x61, 0x01)) // ESC a 1 (Centrar)

        // Configurar el módulo del QR
        outputStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, qrSize.toByte()))

        // Seleccionar el tipo de modelo QR
        outputStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, qrModel.toByte()))

        // Enviar los datos del QR
        val qrDataBytes = qrData.toByteArray(Charset.forName("GB18030"))
        val dataSize = qrDataBytes.size + 3
        val pL = (dataSize and 0xFF).toByte()
        val pH = ((dataSize shr 8) and 0xFF).toByte()

        outputStream.write(byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30))
        outputStream.write(qrDataBytes)

        // Imprimir el QR
        outputStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))

        outputStream.write("\r\n".toByteArray())
//        outputStream.write("\r\n".toByteArray())
//        outputStream.write("\r\n".toByteArray())
//        outputStream.write("\r\n".toByteArray())// Salto de línea después del QR
    }
    /**
     * Función para cortar el papel después de la impresión
     */
//    fun cortarPapel(outputStream: OutputStream) {
//        outputStream.write("\n\n\n\n\n".toByteArray()) // Espacio extra antes del corte
//
//        // Comando estándar ESC/POS para cortar papel (GS V 0)
//        outputStream.write(byteArrayOf(0x1D, 0x56, 0x00))
//
//        println("Corte de papel enviado")
//    }

    fun imprimirDrawable(outputStream: OutputStream, drawable: Drawable, anchoImpresora: Int) {
        // Convertir el Drawable a Bitmap
        val bitmap = (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            ?: convertDrawableToBitmap(drawable)

        // Redimensionar el Bitmap al ancho de la impresora
        val resizedBitmap = resizeBitmap(bitmap, anchoImpresora)

        // Convertir el Bitmap a un formato compatible con la impresora (monocromo)
        val monochromeBitmap = convertToMonochrome(resizedBitmap)

        // Enviar el Bitmap a la impresora
        imprimirBitmap(outputStream, monochromeBitmap)
    }

    // Función para convertir un Drawable a Bitmap
    fun convertDrawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    // Función para redimensionar el Bitmap al ancho de la impresora
    fun resizeBitmap(bitmap: Bitmap, anchoImpresora: Int): Bitmap {
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        val newHeight = (anchoImpresora * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, anchoImpresora, newHeight, false)
    }

    // Función para convertir el Bitmap a monocromo
    fun convertToMonochrome(bitmap: Bitmap): Bitmap {
        val monochromeBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xff
                val g = (pixel shr 8) and 0xff
                val b = pixel and 0xff
                val luminance = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                val newPixel = if (luminance > 128) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
                monochromeBitmap.setPixel(x, y, newPixel)
            }
        }
        return monochromeBitmap
    }

    // Función para imprimir un Bitmap en la impresora
    // Función para imprimir un Bitmap en la impresora
    fun imprimirBitmap(outputStream: OutputStream, bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height

        // Comandos ESC/POS para imprimir una imagen
        outputStream.write(byteArrayOf(0x1D, 0x76, 0x30, 0x00)) // GS v 0 (Comando para imprimir imagen)

        // Enviar los datos de la imagen
        for (y in 0 until height step 24) {
            // Configurar el modo de imagen y el tamaño
            outputStream.write(byteArrayOf(0x1B, 0x2A, 33, (width and 0xFF).toByte(), ((width shr 8) and 0xFF).toByte()))

            for (x in 0 until width) {
                for (k in 0 until 3) {
                    var slice = 0
                    for (b in 0 until 8) {
                        val yPos = y + (k * 8) + b
                        if (yPos < height) {
                            val pixel = bitmap.getPixel(x, yPos)
                            val luminance = (0.299 * ((pixel shr 16) and 0xFF) +
                                    0.587 * ((pixel shr 8) and 0xFF) +
                                    0.114 * (pixel and 0xFF))
                            if (luminance < 128) {
                                slice = slice or (1 shl (7 - b))
                            }
                        }
                    }
                    // Asegurar que el valor de slice esté en el rango de un byte (0-255)
                    val byteValue = slice and 0xFF
                    outputStream.write(byteValue)
                }
            }
        }

        // Finalizar la impresión de la imagen
        outputStream.write(byteArrayOf(0x1B, 0x64, 0x02)) // Avanzar papel 2 líneas
    }
    fun ocultarTeclado(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }
    fun obtenerUUID(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var uuid = prefs.getString("device_uuid", null)

        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().putString("device_uuid", uuid).apply()
        }
        return uuid
    }
    fun configurarOnBackPressed() {
        // Callback siempre habilitado que simplemente ignora el Back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // NO HACER NADA: cancela el retroceso y NO cierra la Activity
            }
        })
    }
}
