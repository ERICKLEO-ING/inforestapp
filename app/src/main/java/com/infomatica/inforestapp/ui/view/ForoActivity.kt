package com.infomatica.inforestapp.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.ActivityForoBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.AlertaAdapter
import com.infomatica.inforestapp.ui.adapter.DescuentoAdapter
import com.infomatica.inforestapp.ui.adapter.GrupoAdapter
import com.infomatica.inforestapp.ui.adapter.OfertaAdapter
import com.infomatica.inforestapp.ui.adapter.SubGrupoAdapter
import com.infomatica.inforestapp.ui.viewmodel.ForoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForoActivity : BaseActivity() {
    private lateinit var binding: ActivityForoBinding
    private val foroViewModel: ForoViewModel by viewModels()
    private lateinit var alertaAdapter: AlertaAdapter
    private lateinit var ofertaAdapter: OfertaAdapter
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureRecyclerViews()
        setupObservers()
        setupClickOnListener()
        foroViewModel.onCreate()
        startRepeatingRequest()
    }
    private fun setupObservers() {
        foroViewModel.isLoading.observe(this, Observer { x ->
            binding.pgCargando.isVisible = x
        })
        foroViewModel.resultAlertas.observe(this, Observer {
            alertaAdapter.submitList(it)
        })
        foroViewModel.resultOfertas.observe(this, Observer {
            ofertaAdapter.submitList(it)
        })

    }
    private fun setupClickOnListener() {
        binding.btnCollapseOferta.setOnClickListener {
            binding.recyclerOfertas.isVisible = !binding.recyclerOfertas.isVisible
            if (!binding.recyclerOfertas.isVisible){
                binding.btnCollapseOferta.setImageResource(R.drawable.pedido_mas_36x40)
                binding.tvOfertas.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.lightgray))
                }
                binding.ivoferta.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.lightgray))
                }
            } else {
                binding.btnCollapseOferta.setImageResource(R.drawable.pedido_menos_36x40)
                binding.tvOfertas.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.TextAzul900))
                }
                binding.ivoferta.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.TextAzul900))
                }
            }

            binding.btnCollapseMensajesInternos.setImageResource(R.drawable.pedido_mas_36x40)
            binding.recyclerMensajes.isVisible = false
            binding.tvMensajesInternos.apply {
                setTextColor(ContextCompat.getColor(context, R.color.lightgray))
            }
            binding.ivmensajeinterno.apply {
                setColorFilter(ContextCompat.getColor(context, R.color.lightgray))
            }
        }
//        binding.btnCollapseRecomendaciones.setOnClickListener {
//            binding.recyclerRecomendaciones.isVisible = !binding.recyclerRecomendaciones.isVisible
//
//            binding.btnCollapseMensajesInternos.setImageResource(R.drawable.pedido_mas_36x40)
//            binding.recyclerMensajes.isVisible = false
//        }
        binding.btnCollapseMensajesInternos.setOnClickListener {
            binding.recyclerMensajes.isVisible = !binding.recyclerMensajes.isVisible
            if (!binding.recyclerMensajes.isVisible){
                binding.btnCollapseMensajesInternos.setImageResource(R.drawable.pedido_mas_36x40)
                binding.tvMensajesInternos.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.lightgray))
                }
                binding.ivmensajeinterno.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.lightgray))
                }
            } else {
                binding.tvMensajesInternos.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.TextAzul900))
                }
                binding.ivmensajeinterno.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.TextAzul900))
                }
                binding.btnCollapseMensajesInternos.setImageResource(R.drawable.pedido_menos_36x40)
            }

            binding.btnCollapseOferta.setImageResource(R.drawable.pedido_mas_36x40)
            binding.recyclerOfertas.isVisible = false
            binding.tvOfertas.apply {
                setTextColor(ContextCompat.getColor(context, R.color.lightgray))
            }
            binding.ivoferta.apply {
                setColorFilter(ContextCompat.getColor(context, R.color.lightgray))
            }
        }
        binding.tvVolverForo.setOnClickListener {
            finishActivity("")
        }
    }
    private fun configureRecyclerViews() {
        binding.recyclerMensajes.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        alertaAdapter = AlertaAdapter()
        binding.recyclerMensajes.adapter = alertaAdapter

        binding.recyclerOfertas.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        ofertaAdapter = OfertaAdapter()
        binding.recyclerOfertas.adapter = ofertaAdapter
    }
    private fun startRepeatingRequest() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) { // Se ejecuta mientras el Job esté activo
                foroViewModel.fetchAlertas()
                foroViewModel.fetchOfertas()
                delay(10000) // Espera 5 segundos antes de la siguiente petición
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Cancelar la tarea al destruir el Activity
    }
}