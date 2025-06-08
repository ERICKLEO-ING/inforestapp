package com.infomatica.inforestapp.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.data.model.TipoIdentidadModel
import com.infomatica.inforestapp.databinding.ActivityClienteBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.TipoIdentidadAdapter
import com.infomatica.inforestapp.ui.view.VentaActivity.Companion.KEY_RESULT
import com.infomatica.inforestapp.ui.viewmodel.ClienteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClienteActivity : BaseActivity() {
    private lateinit var binding: ActivityClienteBinding
    private val clienteViewModel: ClienteViewModel by viewModels()
    private var documento:String = "BOLETA"
    var tiposIdentidad: List<TipoIdentidadModel> = emptyList()
    var tipoProceso: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent // Obtiene el Intent que inició la Activity
        tipoProceso =  intent.getStringExtra("tipo").toString() // Recupera el valor usando la misma clave


        setupClickOnListener()
        changeslistener()
        setupObservers()
        clienteViewModel.onCreate()


        binding.spinnerTipoDocumento.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTipo = tiposIdentidad[position]
                // Aquí puedes usar el objeto completo 'selectedTipo'
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Manejar caso cuando no hay nada seleccionado si es necesario
            }
        })

    }
    private fun setupObservers() {
        clienteViewModel.isLoading.observe(this, Observer { x ->
            binding.pgCargando.isVisible = x
        })
        clienteViewModel.resultUsuario.observe(this, Observer{
            if (it != null) {
                if (it.pais=="005"){
                    binding.tabBoleta.setText("FACTURA SIMPLE")
                    binding.tabFactura.setText("FACTURA FISCAL")

                    documento = "BOLETA SIMPLE"
                }

            }
        })
        clienteViewModel.resultTipoIdentidad.observe(this, Observer{
            if (it != null) {
                tiposIdentidad = it
                val adapter = TipoIdentidadAdapter(this, tiposIdentidad)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTipoDocumento.adapter = adapter
            }
        })
        clienteViewModel.clienteModel.observe(this, Observer{
            if (it != null) {
                if (it.tidentidad.isNotEmpty()) {
                    binding.etNombreApellido.setText(it.descripcion)
                    binding.etEnviarCorreo.setText(it.tcorreo)

                    binding.etNombreApellido.error = null
                    binding.etEnviarCorreo.error = null

                } else {
                    binding.etNombreApellido.setText("")
                    binding.etEnviarCorreo.setText("")

                    binding.etNombreApellido.error = "No Encontrado"
                    //binding.etEnviarCorreo.error = "No Encontrado"

                }
            }
        })
    }
    private fun setupClickOnListener() {
        try {
            binding.tvHeaderTitle.setOnClickListener {
                finishActivity("0")
            }
            binding.ivAtrasCobro.setOnClickListener {
                finishActivity("0")
            }
            binding.tvHeaderTitle.setOnClickListener {
                finishActivity("0")
            }

            binding.tabBoleta.setOnClickListener{
                // Set the text color
                binding.tabBoleta.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                // Set background drawable (not background color)
                binding.tabBoleta.setBackgroundResource(R.drawable.tab_selected)  // 'tab_selected' is a drawable resource

                // Make the "tabFactura" TextView visible
                binding.tabFactura.setTextColor(ContextCompat.getColor(this, R.color.lightgray))
                binding.tabFactura.setBackgroundResource(R.drawable.tab_unselected)

                binding.tvTituloEleccionDoc.text = "Ingresa el documento o elige SIN DATOS."

                binding.tvSinDatos.isVisible = true

                binding.spinnerTipoDocumento.isVisible = true

                if (clienteViewModel.resultUsuario.value!!.pais=="005"){
                    documento = "FACTURA SIMPLE"
                } else {
                    documento = "BOLETA"
                }

            }

            binding.tabFactura.setOnClickListener{
                // Set the text color
                binding.tabFactura.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                // Set background drawable (not background color)
                binding.tabFactura.setBackgroundResource(R.drawable.tab_selected)  // 'tab_selected' is a drawable resource

                // Make the "tabFactura" TextView visible
                binding.tabBoleta.setTextColor(ContextCompat.getColor(this, R.color.lightgray))
                binding.tabBoleta.setBackgroundResource(R.drawable.tab_unselected)

                binding.tvTituloEleccionDoc.text = "Ingresa el número de identificación del cliente."

                binding.tvSinDatos.isVisible = false

                binding.spinnerTipoDocumento.isVisible = false

                val posicionRuc = tiposIdentidad.indexOfFirst { it.codigo == "02" }
                
                if (posicionRuc != -1) {
                    binding.spinnerTipoDocumento.setSelection(posicionRuc)
                }
                if (clienteViewModel.resultUsuario.value!!.pais=="005"){
                    documento = "FACTURA FISCAL"
                } else {
                    documento = "FACTURA"
                }
                //documento = "FACTURA"
            }

            binding.btnConfirmar.setOnClickListener {
                ValidarCamposInformativos()
            }

            binding.tvSinDatos.setOnClickListener {

                var clientegenerico= clienteViewModel.resultParametrosModel.value!!.clientegenerico

                var clienteSinDatos = ClienteFacturadoModel(codigo = "0", tidentidad = clientegenerico, ttipoidentidad = "" ,tipoidentidad = "Documento" ,descripcion = "Cliente General", documento = documento)
                clienteViewModel.guardarCliente(clienteSinDatos)

                if (tipoProceso=="division"){
                    finishActivity("ok")
                } else {
                    val intent = Intent(this, CobroActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
                    pedidoLauncher.launch(intent, options)
                }


                //startActivity(CobroActivity::class.java)
            }

        } catch (_: Exception) {

        }
    }
    private fun ValidarCamposInformativos() {
        val numerDocumento = binding.etNumeroDocumento.text.toString().trim()
        val nombreApellido = binding.etNombreApellido.text.toString().trim()
        val enviarCorreo = binding.etEnviarCorreo.text.toString().trim()
        try {
            when {
                numerDocumento.isEmpty() -> binding.etNumeroDocumento.error = "Campo requerido"
                nombreApellido.isEmpty() -> binding.etNombreApellido.error = "Campo requerido"
                (enviarCorreo.isNotEmpty()  && !android.util.Patterns.EMAIL_ADDRESS.matcher(enviarCorreo).matches()) -> binding.etEnviarCorreo.error = "Ingresa un correo válido"
                else -> {
                    binding.etNumeroDocumento.error = null
                    binding.etNombreApellido.error = null
                    binding.etEnviarCorreo.error = null

                    var cliente =clienteViewModel.clienteModel.value!!
                    cliente.documento = documento

                    if (cliente == null || cliente.tidentidad.isEmpty()) {
                        cliente.descripcion = binding.etNombreApellido.text.toString().trim()
                        cliente.tidentidad = binding.etNumeroDocumento.text.toString().trim()
                        cliente.tcorreo = binding.etEnviarCorreo.text.toString().trim()

                        val tipoSeleccionado = binding.spinnerTipoDocumento.selectedItem as TipoIdentidadModel
                        cliente.ttipoidentidad = tipoSeleccionado.codigo
                        cliente.tipoidentidad =  tipoSeleccionado.descripcion

                        clienteViewModel.guardarCliente(cliente)
                    }

                    if (tipoProceso =="division"){
                        finishActivity("ok")
                    } else {
                        val intent = Intent(this, CobroActivity::class.java)
                        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
                        pedidoLauncher.launch(intent, options)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error en validarYEnviarLogin: ${e.message}", e)
            showToast("Ocurrió un error al intentar iniciar sesión.")
        }
    }

    private fun changeslistener(){
        binding.etNumeroDocumento.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Se ejecuta antes de que el texto cambie
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Se ejecuta mientras el usuario escribe o borra
            }

            override fun afterTextChanged(s: Editable?) {
                // Se ejecuta después de que el texto ha cambiado
                //binding.miTextView.text = "Texto ingresado: ${s.toString()}"

                if (s != null) {
                    if (s.toString().trim().length>=8){
                        clienteViewModel.consultarCliente(s.toString().trim())
                    }
                }
                showToast("Texto ingresado: ${s.toString()}")
            }
        })
    }

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultValue = data?.getStringExtra(KEY_RESULT)
                // Verificar si el valor resultante es nulo o vacío, si es necesario
                if (resultValue =="facturado") {
                    finishActivity("facturado")
                }
            }
        }
}