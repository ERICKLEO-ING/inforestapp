package com.infomatica.inforestapp.ui.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import com.infomatica.inforestapp.databinding.ActivityMensajeMeseroBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.MensajeMeseroAdapter
import com.infomatica.inforestapp.ui.fragment.ImpresoraCajaFragment
import com.infomatica.inforestapp.ui.fragment.TarjetaBancariaFragment
import com.infomatica.inforestapp.ui.viewmodel.MensajeMeseroViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MensajeMeseroActivity : BaseActivity(), MensajeMeseroAdapter.ItemClickListener {

    private lateinit var binding: ActivityMensajeMeseroBinding
    private val mensajeMeseroViewModel: MensajeMeseroViewModel by viewModels()
    private lateinit var mensajeMeseroAdapter: MensajeMeseroAdapter

    private var selectMensaje: String = ""
    private var selectNombreImpresora: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMensajeMeseroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureRecyclerViews()
        setupObservers()
        setupClickOnListener()

        mensajeMeseroViewModel.onCreate()

        binding.etObservacion.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etObservacion.windowToken, 0)
                binding.etObservacion.clearFocus()

                if (binding.etObservacion.text.toString() != ""){
                    binding.btnenviarmensajemesero.isEnabled = true
                }

                true
            } else {
                false
            }
        }


    }

    private fun configureRecyclerViews() {
        binding.recyclermensajemesero.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        mensajeMeseroAdapter = MensajeMeseroAdapter(this)
        binding.recyclermensajemesero.adapter = mensajeMeseroAdapter

    }

    private fun setupClickOnListener() {
        binding.tvcancelarmensajemesero.setOnClickListener {
            finishActivity("")
        }


        binding.btnenviarmensajemesero.setOnClickListener {
            binding.btnenviarmensajemesero.isEnabled =false

            if ( binding.etObservacion.text.toString() !="")
            {
                val fragment = ImpresoraCajaFragment.newInstance(mensajeMeseroViewModel.resultImpresoraCaja.value!!) { result ->
                    // Este callback se ejecuta cuando el fragmento devuelve el resultado
                    //showToast("Resultado recibido: $result")
                    if (!result.codigo.isNullOrBlank()) {
                        // Resaltar la tarjeta seleccionada
                        //it.setBackgroundResource(R.color.colorBackground2) // Color de selección
                        if (binding.etObservacion.text.toString() !=""){
                            selectMensaje = binding.etObservacion.text.toString()
                        }

                        if (selectMensaje.isNullOrBlank()){
                            showToast("Seleccione un mensaje")
                            binding.btnenviarmensajemesero.isEnabled =true
                            return@newInstance
                        }
                        selectNombreImpresora = result.descripcion
                        mensajeMeseroViewModel.fetchImprimeMensaje(MensajeImpresionModel(mensaje = selectMensaje, codigoImpresora = result.codigo))

                    } else {
                        // Resaltar la tarjeta seleccionada
                        //it.setBackgroundResource(R.color.white) // Color de selección
                        binding.btnenviarmensajemesero.isEnabled =true
                        showToast("Seleccione una impresora")
                    }


                }

                // Mostrar el BottomSheetDialogFragment
                fragment.show(supportFragmentManager, "ImpresoraCajaFragment")
            } else {
                selectNombreImpresora = ""
                mensajeMeseroViewModel.fetchImprimeMensaje(MensajeImpresionModel(mensaje = selectMensaje, codigoImpresora = "001"))

            }


        }

    }
    private fun setupObservers() {
        mensajeMeseroViewModel.resultPedidoModel.observe(this, Observer {

        })
        mensajeMeseroViewModel.isLoading.observe(this, Observer {
            binding.pgCargando.isVisible = it
        })

        mensajeMeseroViewModel.resultMensajeMesero.observe(this, Observer {
            mensajeMeseroAdapter.submitList(it)
        })
        mensajeMeseroViewModel.resultMensajeImpresion.observe(this, Observer {
            if (it.status==0){
//                showToast(it.mensaje)
                mensajeDialog(this.supportFragmentManager,"¡Mensaje enviado!","Tu mensaje fue impreso. ${selectNombreImpresora} ", "impresionmensaje", ""){ respuesta,respuesta2 ->
                    finishActivity("")
                }

            }else{
                showToast(it.mensaje)
                binding.btnenviarmensajemesero.isEnabled =true
            }
        })
    }
    override fun onItemClick(details: MensajeMeseroModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
            binding.btnenviarmensajemesero.isEnabled = true
            binding.etObservacion.setText("")
            showToast(it.descripcion)
            selectMensaje = it.descripcion
        }
    }
}