package com.infomatica.inforestapp.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Length
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.databinding.ActivityLoginBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.ScreenCategory
import com.infomatica.inforestapp.ui.fragment.MarcacionFragment
import com.infomatica.inforestapp.ui.view.VentaActivity.Companion.KEY_RESULT
import com.infomatica.inforestapp.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    // Código de solicitud de permisos
    private val REQUEST_PERMISSION_LOCATION = 1
    private var modificarApiUrl = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            // Save the base URL
            //LocalStore.saveBaseUrl(this, "http://161.132.106.116/api/")

            loginViewModel.onCreate()
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupUI()
            setupObservers()

            // Oculta o muestra según categoría de pantalla
            when (getScreenCategory()) {
                ScreenCategory.SMALL -> {
                    // <600dp → teléfono
                    binding.ivprincipallogin.isVisible = false
                }
                else -> {
                    // ≥600dp → tablet o más grande
                    binding.ivprincipallogin.isVisible = true
                }
            }
            val url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTAzqyXrS0olzZrQNlwcwZKhLwjVtEOGI6vwA&s"

            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ver_imagen_36x36)
                .error(R.drawable.ver_imagen_36x36)
                .into(binding.ivprincipallogin)

            binding.passwordEditText.setOnClickListener {
                mostrarNumPadDialog(this.supportFragmentManager, "login"){ cantidad ->
                    binding.passwordEditText.setText(cantidad.toInt().toString())
                    validarYEnviarLogin()
                }
            }


            // Verificar permisos en tiempo de ejecución
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos si no se tienen
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
            } else {
                // Si ya se tiene el permiso, ejecutar la función para obtener la información Wi-Fi
                getWifiInfo()
            }
            binding.llDatosgenerales.setOnClickListener{
                loginViewModel.onCreate()

                // Verificar permisos en tiempo de ejecución
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Solicitar permisos si no se tienen
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                } else {
                    // Si ya se tiene el permiso, ejecutar la función para obtener la información Wi-Fi
                    getWifiInfo()
                }
            }
//            binding.logoImageView.setOnClickListener{
//                modificarApiUrl++
//                if (modificarApiUrl>7){
//                    modal_CambiarUrlApi()
//                    modificarApiUrl = 0
//                } else {
//                    if (modificarApiUrl>3){
//                        showToast("Esta a ${8-modificarApiUrl} para modificar la api!")
//                    }
//                }
//            }

            binding.tvMarcacion.setOnClickListener {
                val fragmentTag = "MarcacionFragment"

                // Verifica si el fragmento ya está agregado
                val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                if (existingFragment != null) {
                    return@setOnClickListener // Si ya existe, no lo abre nuevamente
                }

                val modal = MarcacionFragment.newInstance { result ->
                    if (!result.isNullOrBlank()) {
                        loginViewModel.registraMarcacion(result)
                        //showToast("Resultado: $result")
                    }
                }

                // Mostrar el modal
                modal.show(supportFragmentManager, fragmentTag)
            }


        } catch (e: Exception) {
            Log.e("LoginActivity", "Error en onCreate: ${e.message}", e)
            showToast("Ocurrió un error al iniciar la aplicación. Intenta de nuevo. ${e.message.toString()}")
        }
    }
    private fun modal_CambiarUrlApi (){
        // Create a LinearLayout to hold both the EditText fields
        val linearLayout = LinearLayout(this@LoginActivity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(30)
        val baseUrl = LocalStore.getBaseUrl(this)
        //val cajasetting = LocalStore.getCajaSettings(this)
        // Create the password EditText
        val reasonEditText = EditText(this@LoginActivity).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
            hint = "http://161.132.106.116/api/"
            setText(baseUrl)
            textAlignment =  View.TEXT_ALIGNMENT_CENTER
        }

        // Add both EditTexts to the LinearLayout
        linearLayout.addView(reasonEditText)
        //linearLayout.addView(cajaEditText)
        //linearLayout.addView(reasonEditText)

        // Build the AlertDialog
        AlertDialog.Builder(this@LoginActivity)
            .setTitle("INFOREST APP")
            .setMessage("Configuraciones del sistema")
            .setView(linearLayout)  // Set the LinearLayout containing both EditTexts
            .setCancelable(false)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Dismiss dialog on cancel
            }
            .setPositiveButton("Confirmar") { dialog, _ ->

                val UrlNueva = reasonEditText.text.toString().trim() // Get the entered password
                //val cajaconfiguracion = cajaEditText.text.toString().trim() // Get the entered password

                LocalStore.saveBaseUrl(this, UrlNueva)
                //LocalStore.saveCajaSettings(this, cajaconfiguracion)

                cerrarAplicacion()
            }
            .show()

    }
    fun cerrarAplicacion() {
        Thread.sleep(1000)
        finishAndRemoveTask() // Cierra la actividad y elimina la tarea de la lista
        System.exit(0) // Finaliza el proceso
    }

    private fun setupUI() {
        try {
            binding.loginButton.setOnClickListener { validarYEnviarLogin() }
            binding.tvconfiguracion.setOnClickListener {
                modificarApiUrl++
                if (modificarApiUrl>7){
                    val intent = Intent(this, ConfiguracionActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                    pedidoLauncher.launch(intent,options)
                    modificarApiUrl = 0
                } else {
                    if (modificarApiUrl>3){
                        showToast("Esta a ${8-modificarApiUrl} para modificar la api!")
                    }
                }

            }


        } catch (e: Exception) {
            Log.e("LoginActivity", "Error en setupUI: ${e.message}", e)
            showToast("Ocurrió un error en la configuración de la interfaz.")
        }
    }

    private fun setupObservers() {
        try {
            loginViewModel.isLoading.observe(this) { isLoading ->
                binding.pgCargando.isVisible = isLoading
            }

            loginViewModel.usuarioModel.observe(this) { usuario ->
                handleLoginResponse(usuario)
            }
            loginViewModel.resultParametrosModel.observe(this){
                try {
                    binding.tvBasedatos.text = "Base dato: "+ it?.basedato
                    binding.tvServidor.text = "Servidor: " + it?.servidor
                    binding.tvLocalnombre.text = "Local: " + it?.localnombre
                    binding.tvIdDispositivo.text = "ID Dispositivo: " + obtenerUUID(this)


                    var url = "http://imgfz.com/i/DRo6LgX.jpeg"
//                    if (it != null) {
//                        if (!it.urlapp.isNullOrEmpty()){
//                            url = it.urlapp
//                        }
//                    }
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.ver_imagen_36x36)
                        .error(R.drawable.ver_imagen_36x36)
                        .into(binding.ivprincipallogin)

                } catch (Ex: Exception){
                    showToast(Ex.message.toString())
                }

            }

            loginViewModel.respuestaMarcacion.observe(this){
                if (it != null) {

                    mensajeDialog(this.supportFragmentManager,it.result,it.mensaje, "marcacion", it.hora){ respuesta,respuesta2 ->

                    }
                }


            }

        } catch (e: Exception) {
            Log.e("LoginActivity", "Error en setupObservers: ${e.message}", e)
            showToast("Error al establecer las observaciones de datos.")
        }
    }

    private fun validarYEnviarLogin() {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        try {
            when {
                username.isEmpty() -> binding.usernameEditText.error = "Usuario requerido"
                password.isEmpty() -> binding.passwordEditText.error = "Contraseña requerida"
                else -> {
                    binding.usernameEditText.error = null
                    binding.passwordEditText.error = null
                    loginViewModel.fetchLogin(username, password, iddispositivo = obtenerUUID(this))
                }
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error en validarYEnviarLogin: ${e.message}", e)
            showToast("Ocurrió un error al intentar iniciar sesión.")
        }
    }

    private fun handleLoginResponse(usuario: UsuarioModel?) {
        try {
            usuario?.let {
                if (it.token.isNotEmpty()) {
                    binding.passwordEditText.setText("")
                    startActivity(Intent(this, VentaActivity::class.java))
                } else {
                    binding.passwordEditText.error = "Contraseña incorrecta"
                }
            } ?: run {
                binding.usernameEditText.error = "Usuario no existe"
                binding.passwordEditText.error = "Contraseña incorrecta"
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error en handleLoginResponse: ${e.message}", e)
            showToast("Ocurrió un error durante la validación del usuario.")
        }
    }

    // Función para obtener el nombre de la red Wi-Fi y la calidad de la señal
    private fun getWifiInfo() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val currentConnection: WifiInfo? = wifiManager.connectionInfo

        // Verificar si estamos conectados a una red Wi-Fi
        if (currentConnection != null && currentConnection.networkId != -1) {
            val ssid = currentConnection.ssid // Nombre de la red Wi-Fi (SSID)
            val signalLevel = currentConnection.rssi // Nivel de la señal en dBm

            // Convertir el nivel de señal a porcentaje
            val signalQuality = WifiManager.calculateSignalLevel(signalLevel, 100)

            binding.tvRedwiffi.text = "Red Wi-Fi: $ssid"
            binding.tvCalidadsenal.text = "Calidad Wi-Fi: $signalQuality%"
            // Mostrar la información en un Toast
            //Toast.makeText(this, "Red Wi-Fi: $ssid\nCalidad de señal: $signalQuality%", Toast.LENGTH_LONG).show()
        } else {
            binding.tvRedwiffi.text = "No estás conectado a ninguna red Wi-Fi"
            binding.tvCalidadsenal.text = "Calidad Wi-Fi: "
            // Si no estamos conectados a una red Wi-Fi, mostrar mensaje
            Toast.makeText(this, "No estás conectado a ninguna red Wi-Fi", Toast.LENGTH_LONG).show()
        }
    }

    // Manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso se concede, ejecutar la función
                getWifiInfo()
            } else {
                // Si el permiso se niega, mostrar un mensaje
                Toast.makeText(this, "Permiso denegado. No se puede obtener la información Wi-Fi.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val keystring = result.data?.getStringExtra(KEY_RESULT)
                if (keystring=="ok" ){
                    cerrarAplicacion()
                }
            }
        }
}
