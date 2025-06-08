package com.infomatica.inforestapp.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.databinding.ActivityClienteBinding
import com.infomatica.inforestapp.databinding.ActivityTurnoBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.viewmodel.CobroViewModel
import com.infomatica.inforestapp.ui.viewmodel.TurnoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TurnoActivity : BaseActivity() {
    private lateinit var binding: ActivityTurnoBinding
    private val turnoViewModel: TurnoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTurnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        turnoViewModel.onCreate("000")

        setupObservers()
        setupClickOnListener()
    }
    private fun setupObservers() {
        turnoViewModel.isLoading.observe(this, Observer { x ->
            binding.pgCargando.isVisible = x
        })
        turnoViewModel.resultLiquidacion.observe(this, Observer { x ->
            imprimirEnPOS(x,"",this)
            binding.btnCerrarTurno.isEnabled = true
        })

        turnoViewModel.resultTurnoCierre.observe(this, Observer { x ->
           if (x.codigo.isNotEmpty()){
               finishActivity("cierre_turno")
           }
        })

        turnoViewModel.resultTurno.observe(this, Observer{
            binding.tvCodigo.setText(" / ${it.codigo}")
            binding.tvApertura.setText("${it.apertura}")
            binding.tvVentadeldia.setText("${it.moneda}${String.format("%.2f", it.ventas)}")
            binding.tvPropina.setText("${it.moneda}${String.format("%.2f", it.propina)}")
            binding.tvMesasAtendidas.setText("${String.format("%.0f", it.mesas)} atendidas")
            binding.tvPedidosAtendidos.setText("${String.format("%.0f", it.pedidos)} atendidos")
            binding.tvTicketPromedio.setText("${it.moneda}${String.format("%.2f", it.ticketPromedio)}")
            binding.tvConsumoComensal.setText("${it.moneda}${String.format("%.2f",it.consumoPromedioComensal)}")
            binding.tvConsumoMesa.setText("${it.moneda}${String.format("%.2f",it.consumoPromedioPorMesa)}")

            val formato = SimpleDateFormat("hh:mm a", Locale.getDefault())
            binding.tvHora.setText("${formato.format(Date())}")

        })
    }
    private fun setupClickOnListener() {
        try {
            binding.ivatrasTurno.setOnClickListener {
                finishActivity("0")
            }

            binding.ivImprimirLiquidacion.setOnClickListener {
                val titulo = "¿Desea imprimir el resumen de tus documentos?"
                val descripcion="Obtendras la liquidacion generada durante tu turno y podras realizar el cierre de turno."
                mensajeDialog(this.supportFragmentManager,titulo,descripcion){ respuesta, respuesta2 ->
                   if (respuesta=="ok"){

                       turnoViewModel.imprimeLiquidacion("000")
                   }
                }
            }

            binding.btnCerrarTurno.setOnClickListener{
                val titulo = "¿Desea cerrar tu turno?"
                val descripcion="Se cerrara el sistema y tendra que volver a iniciar."
                mensajeDialog(this.supportFragmentManager,titulo,descripcion){ respuesta,respuesta2 ->
                    if (respuesta=="ok"){
                        binding.btnCerrarTurno.isEnabled =false
                        turnoViewModel.cerrarTurno("000")
                    }
                }
            }
        } catch (Ex: Exception) {
            showToast(message = Ex.message.toString())
        }
    }
}