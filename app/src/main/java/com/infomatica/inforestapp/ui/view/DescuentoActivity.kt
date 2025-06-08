package com.infomatica.inforestapp.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.MesasModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import com.infomatica.inforestapp.data.model.PrepagoModel
import com.infomatica.inforestapp.databinding.ActivityCobroBinding
import com.infomatica.inforestapp.databinding.ActivityDescuentoBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.DescuentoAdapter
import com.infomatica.inforestapp.ui.adapter.MesaAdapter
import com.infomatica.inforestapp.ui.adapter.SalonAdapter
import com.infomatica.inforestapp.ui.fragment.PropinaDialogFragment
import com.infomatica.inforestapp.ui.viewmodel.CobroViewModel
import com.infomatica.inforestapp.ui.viewmodel.DescuentoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class DescuentoActivity : BaseActivity(),DescuentoAdapter.ItemClickListener {
    private lateinit var binding: ActivityDescuentoBinding
    private val descuentoViewModel: DescuentoViewModel by viewModels()
    private lateinit var descuentoAdapter: DescuentoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescuentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarAdapters()

        descuentoViewModel.onCreate()
        setupClickOnListener()
        setupObservers()
    }

    private fun setupClickOnListener() {
        try {
            binding.tvCancelarDescuento.setOnClickListener {
                finishActivity("0", R.anim.slide_in_left)
            }
            binding.btnGuardarDescuento.setOnClickListener{
                val titulo = "¿Desea aplicar el descuento?"
                val descripcion="Ingrese su contraseña para validarlo."
                mensajeDialog(this.supportFragmentManager,titulo,descripcion,tipo="descuento"){ respuesta,respuesta2 ->
                    if (respuesta2.isEmpty()) {
                        showToast("Ingrese password!")
                        return@mensajeDialog
                    }

                    val descuentoSeleccionado = descuentoAdapter.currentList.firstOrNull { it.seleccionado }

                    if (descuentoSeleccionado == null) {
                        showToast("Seleccione un descuento!")
                        return@mensajeDialog
                    }

                    if (respuesta == "ok") {
                        descuentoViewModel.modificaPedido(descuentoSeleccionado.codigo, respuesta2)
                    }

                }
            }

        } catch (_: Exception) {

        }
    }
    private fun configurarAdapters() {
        binding.rvDescuentos.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        descuentoAdapter = DescuentoAdapter(this,this)
        binding.rvDescuentos.adapter = descuentoAdapter

    }
    private fun setupObservers() {
        descuentoViewModel.isLoading.observe(this, Observer { x ->
            binding.pgCargando.isVisible = x
        })
        descuentoViewModel.resultMotivoDescuentoModel.observe(this,Observer{
            descuentoAdapter.submitList(it)
        })
        descuentoViewModel.resultPedidoModel.observe(this,Observer{
            if (it!=null && it.codigopedido.isNotEmpty() && it.status==0){
                finishActivity("okdescuento", R.anim.slide_in_left)
            }
        })


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(details: MotivoDescuentoModel?) {
        details?.let {
            descuentoAdapter.currentList.forEach { item ->
                item.seleccionado = false
            }
            it.seleccionado = true
            descuentoAdapter.notifyDataSetChanged()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemEliminaClick(details: MotivoDescuentoModel?) {
        details?.let {
            descuentoAdapter.currentList.forEach { item ->
                item.seleccionado = false
            }
            it.seleccionado = true
            descuentoAdapter.notifyDataSetChanged()

            val titulo = "¿Desea eliminar el descuento?"
            val descripcion=it.descripcion
            mensajeDialog(this.supportFragmentManager,titulo,descripcion, tipo="descuento"){ respuesta,respuesta2 ->
                if (respuesta2.isEmpty()) {
                    showToast("Ingrese password!")
                    return@mensajeDialog
                }

                if (respuesta == "ok") {
                    descuentoViewModel.modificaPedido("", respuesta2)
                }

            }


        }
    }
}