package com.infomatica.inforestapp.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.CheckActivityResultDataNiubiz
import com.infomatica.inforestapp.data.model.PrepagoModel

import com.infomatica.inforestapp.databinding.ActivityCobroBinding
import com.infomatica.inforestapp.extension.ConstansNiubiz.VNP_REQUEST_CODE
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.PrepagoAdapter
import com.infomatica.inforestapp.ui.fragment.DivisionProductoFragment
import com.infomatica.inforestapp.ui.fragment.ImpresoraCajaFragment
import com.infomatica.inforestapp.ui.fragment.PropinaDialogFragment
import com.infomatica.inforestapp.ui.fragment.TarjetaBancariaFragment
import com.infomatica.inforestapp.ui.viewmodel.CobroViewModel
import com.infomatica.inforestapp.ui.viewmodel.NiubizViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@AndroidEntryPoint
class CobroActivity : BaseActivity(), PrepagoAdapter.ItemClickListener {
    private lateinit var binding:ActivityCobroBinding
    private val niubizViewModel: NiubizViewModel by viewModels()
    private var tipopago:String = "tarjeta"
    private var tipopagoMultiple:String = "tarjeta"
    private var tipotarjeta:String = "visa"
    private var monedaPedido:String = ""
    private val cobroViewModel: CobroViewModel by viewModels()

    private var montoCobro:Double = 0.0
    private var montoCobroFaltante:Double = 0.0
    private var montoPedido:Double = 0.0
    private var propina:Double = 0.0
    private var propinaMultiple:Double = 0.0
    private var montoEfectivo:Double = 0.0

    private lateinit var prepagoAdapter: PrepagoAdapter

    var tipoProceso: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCobroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarOnBackPressed()

        val intent = intent // Obtiene el Intent que inició la Activity
        tipoProceso =  intent.getStringExtra("tipo").toString() // Recupera el valor usando la misma clave

        setupObservers()
        setupClickOnListener()
        configureRecyclerViews()
        cobroViewModel.onCreate(tipoProceso)

    }

    private fun configureRecyclerViews() {
        binding.recyclerPrepagos.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        prepagoAdapter = PrepagoAdapter(this)
        binding.recyclerPrepagos.adapter = prepagoAdapter
    }

    private fun setupObservers() {
        cobroViewModel.isLoading.observe(this, Observer { x ->
            binding.pgCargando.isVisible = x
        })
        cobroViewModel.resultUsuario.observe(this, Observer { x ->
            if (x != null) {
                binding.llPagoEfectivo.isVisible = x.activaefectivo
                binding.llPagoEfectivoMultiple.isVisible = x.activaefectivo

                binding.llTarjetapanel.isVisible =!x.activaniubiz
                binding.llPagoQr.isVisible = x.activaniubiz
                binding.llPagoQrMultiple.isVisible = x.activaniubiz

                if (x.pais =="005"){
                    binding.textInputmontocobrado.isVisible = true
                    binding.tvPropinaPago.isVisible = false
                }
            }
        })
        cobroViewModel.clienteModel.observe(this, Observer{
            if (it != null) {

                binding.tvHeaderTitle.setText("Cobro con ${it.documento}")
                binding.tvdocumentocliente.setText("${it.tipoidentidad}: ${it.tidentidad}")
                binding.tvnombrecliente.setText(it.descripcion)

            }
        })
        cobroViewModel.resultPedidoModel.observe(this, Observer{currentCute ->
            if (currentCute != null) {
                if (tipoProceso=="division"){
                    montoPedido =  currentCute.montodivision
                    montoCobro =  currentCute.montodivision+propina
                } else {
                    montoPedido =  currentCute.detalle.sumOf { it.itemventa }
                    montoCobro =  currentCute.detalle.sumOf { it.itemventa }+propina
                }
                // =  currentCute.detalle.sumOf { it.itemventa }
                //montoCobro =  currentCute.detalle.sumOf { it.itemventa }+propina

                binding.tvTitulo1TotalPagar.text = "Total a cobrar ${currentCute.monedapedido} ${ String.format("%.2f",montoPedido)}"
                binding.btnfinalizarcobro.text = "FINALIZAR COBRO ${currentCute.monedapedido} ${ String.format("%.2f", montoCobro)}"

                binding.tvtitulo2.text = "${currentCute.monedapedido} ${ String.format("%.2f", montoPedido)}"
                monedaPedido = currentCute.monedapedido

                if (propina==0.00){
                    if (tipopago!="multiple"){
                        binding.tvPropinaPago.setText("¿Añadir Propina?")
                    }
                } else {
                    binding.tvPropinaPago.setText("Propina ${monedaPedido} ${String.format("%.2f", propina)}")
                }

                if (propinaMultiple==0.00){
                    binding.tvPropinaMultiple.setText("¿Añadir Propina?")
                } else {
                    binding.tvPropinaMultiple.setText("Propina ${monedaPedido} ${String.format("%.2f", propinaMultiple)}")
                }

                binding.tvefectivo5.setText("${monedaPedido}5")
                binding.tvefectivo10.setText("${monedaPedido}10")
                binding.tvefectivo20.setText("${monedaPedido}20")
                binding.tvefectivo50.setText("${monedaPedido}50")
                binding.tvefectivo100.setText("${monedaPedido}100")

                if (tipopago=="multiple") {
                    if (montoCobroFaltante <= 0) {
                        binding.llCobroMultiple.isVisible = false
                        binding.llMontoMultiple.isVisible = false
                        collapse(binding.llEfectivopanel)
                        collapse(binding.llTarjetapanel)
                    } else {
                        binding.llCobroMultiple.isVisible = true
                        binding.llMontoMultiple.isVisible = true
                        binding.btnfinalizarcobro.setText("COBRAR")
                    }
                    binding.tvPropinaPago.setText("Falta cobrar ${monedaPedido}${ String.format("%.2f",montoCobroFaltante)}")
                }

            }
        })

        cobroViewModel.resultFacturacionModel.observe(this, Observer{
            if (it != null) {
                if (it.status == 0 || it.status==100) {
                    if (it.impresiondocumento.isNotEmpty()){
                        var printerDoc = LocalStore.getPrinterBluetoothDocumento(this)
                        if (printerDoc=="1"){
                            imprimirEnPOS(it.impresiondocumento,"",this)
                        }
//                    imprimirEnPOS(it.impresiondocumento,"",this)
                    } else {
                        showToast(it.impresiondocumento)
                    }
                    finishActivity("facturado")
                } else {
                    showToast(it.mensaje)
                }

            }
        })
        cobroViewModel.resultPrepagos.observe(this, Observer{
            prepagoAdapter.submitList(it)
            binding.tvcontadormultiple.setText("Cobro ${it.size+1}")
            binding.tvMontoCobrar.setText("")
            propinaMultiple=0.00
            binding.etnumeroopracion.setText("")
            limpiarEfectivoPaga()

            montoCobroFaltante = BigDecimal(montoPedido-it.sumOf { it.monto })
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
            binding.btnfinalizarcobro.isEnabled =true

            binding.tvMontoCobrar.setText(String.format("%.2f", montoCobroFaltante))

            if (it.size>0){
                binding.llPagoTarjeta.isEnabled =false
                binding.llPagoQr.isEnabled =false
                binding.llPagoEfectivo.isEnabled =false
                binding.llPagoMultiple.callOnClick()
            } else {
                binding.llPagoTarjeta.isEnabled =true
                binding.llPagoQr.isEnabled =true
                binding.llPagoEfectivo.isEnabled =true
            }

            cobroViewModel.resultPedidoModel.value = cobroViewModel.resultPedidoModel.value
        })
    }

    private fun limpiarEfectivoPaga(){
        montoEfectivo = 0.00
        binding.tvefectivopagacon.setText("Paga con: -")
        binding.tvefectivovuelto.setText("Vuelto: -")
    }
    // Click  para eleccion  de Item pedido
    override fun onItemClickElimina(details: PrepagoModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
            val titulo = "¿Desea eliminar el prepago?"
            val descripcion=""
            mensajeDialog(this.supportFragmentManager,titulo,descripcion){ respuesta,respuesta2 ->
                if (respuesta == "ok") {
                    cobroViewModel.eliminaPrepago(it)
                }
            }

        }
    }
    private fun ProcesarPagoManual(prepago: PrepagoModel, prefijo: String){
        try {
            var printerDocW = LocalStore.getPrinterBluetoothDocumentoWindow(this)
            if (printerDocW=="0"){
                var tamano : Int = 32
                var printerT = LocalStore.getPrinterBluetooth(this)
                if (printerT=="50"){tamano=32}
                if (printerT=="55"){tamano=34}
                if (printerT=="80"){tamano=40}

                val numerocopia = binding.tvCantidadcopias.text.toString().toDouble().toInt()
                cobroViewModel.procesoPedidoFacturar(prepago,prefijo,"000",numerocopia,tamano,false, tipoProceso)
                return
            }


            val fragment = ImpresoraCajaFragment.newInstance(cobroViewModel.resultImpresoraCaja.value!!) { result ->

                // Este callback se ejecuta cuando el fragmento devuelve el resultado

                if (!result.codigo.isNullOrBlank()) {
                    val numerocopia = binding.tvCantidadcopias.text.toString().toDouble().toInt()
                    // Resaltar la tarjeta seleccionada

                    var tamano : Int = 32
                    var printerT = LocalStore.getPrinterBluetooth(this)
                    if (printerT=="50"){tamano=32}
                    if (printerT=="55"){tamano=34}
                    if (printerT=="80"){tamano=40}

                    var printerDocW = LocalStore.getPrinterBluetoothDocumentoWindow(this)
                    var activadoc: Boolean = false
                    if (printerDocW=="1")
                        activadoc = true

                    cobroViewModel.procesoPedidoFacturar(prepago,prefijo,result.codigo,numerocopia,tamano,activadoc, tipoProceso)
                } else {
                    binding.btnfinalizarcobro.isEnabled = true
                    // Resaltar la tarjeta seleccionada
                    showToast("Seleccione una impresora")
                }
            }
            fragment.show(supportFragmentManager, "ImpresoraCajaFragment")
        } catch (Ex: Exception){
            showToast("Error al imprimir ${Ex.message}")
        }

    }

    private fun setupClickOnListener() = try {
        binding.tvHeaderTitle.setOnClickListener {
            finishActivity("0", R.anim.slide_in_left)
        }
        binding.ivAtrasCobro.setOnClickListener {
            finishActivity("0", R.anim.slide_in_left)
        }

        binding.llmontocobrar.setOnClickListener {
            mostrarNumPadDialog(this.supportFragmentManager){ cantidad ->
                if (cantidad>0){
                    binding.tvMontoCobrar.setText(String.format("%.2f", cantidad))
                }
            }
        }

        binding.etmontocobrado.setOnClickListener {
            mostrarNumPadDialog(this.supportFragmentManager){ cantidad ->
                if ((cantidad * 100.0).roundToInt() / 100.0 < (montoPedido * 100.0).roundToInt() / 100.0) {
                    binding.etmontocobrado.setText("")
                    showToast("Monto ingresado debe ser mayor o igual al monto a cobrar")
                    return@mostrarNumPadDialog
                }

                binding.etmontocobrado.setText("${monedaPedido}${String.format("%.2f", cantidad)}")
                propina = cantidad - montoPedido
                if (propina>0.0) {
                    binding.tvPropinaPago2.setText("Propina: ${monedaPedido}${String.format("%.2f", propina)}")
                    binding.tvPropinaPago2.isVisible = true

                } else {
                    binding.tvPropinaPago2.isVisible = false
                    propina=0.0
                }

            }
        }

        binding.btnfinalizarcobro.setOnClickListener{
            if ( !binding.btnfinalizarcobro.isEnabled){
                return@setOnClickListener
            }
            binding.btnfinalizarcobro.isEnabled = false
            //val monto = 1.0 // Monto en soles (convertido a céntimos en el ViewModel)
            if (tipopago =="efectivo"){

                if (montoEfectivo<montoPedido){
                    binding.btnfinalizarcobro.isEnabled = true
                    showToast("Ingresa monto de efectivo.")
                    return@setOnClickListener
                }
                val prefijo = if (cobroViewModel.clienteModel.value!!.documento == "BOLETA") "B" else "F"
                val prepago: PrepagoModel = PrepagoModel(
                    codigo="",
                    tipoPago = "01",
                    monto = montoPedido,
                    propina = montoEfectivo,
                    codigoTarjeta = "",
                    numeroTarjeta = "",
                    referencia = "",
                    numeroATarjeta = "",
                    banco = ""
                )

//                cobroViewModel.procesoPedidoFacturar(prepago,prefijo)
                ProcesarPagoManual(prepago,prefijo)
            }
            if (tipopago =="tarjeta"   || tipopago=="qr"){

                if (cobroViewModel.resultUsuario.value!!.activaniubiz){
                    niubizViewModel.sendSale(
                        this,
                        montoCobro,
                        isQr = tipopago == "qr",
                        merchantCode = null
                    )
                } else {
                    val prefijo = if (cobroViewModel.clienteModel.value!!.documento == "BOLETA") "B" else "F"
                    val numerDocumento = binding.etnumeroopracion.text.toString().trim()
                    val numeroTarjeta = if (numerDocumento.length >= 8) numerDocumento.substring(0, 8) else numerDocumento

                    val prepago: PrepagoModel = PrepagoModel(
                        codigo = "",
                        tipoPago = "02",
                        monto = montoPedido,
                        propina = propina,
                        codigoTarjeta = if (tipopago == "qr") "APPQR" else (if (tipotarjeta.length>2) "APP"+tipotarjeta.substring(0, 4).uppercase() else tipotarjeta) ,
                        numeroTarjeta = numeroTarjeta,
                        referencia = tipopagoMultiple.substring(0,2).uppercase(),
                        numeroATarjeta = if (tipopago == "qr") "APPQR" else  (if (tipotarjeta.length>2) "APP"+tipotarjeta.substring(0, 4).uppercase() else "otro") ,
                        banco = ""
                    )
//                    cobroViewModel.procesoPedidoFacturar(prepago,prefijo)
                    ProcesarPagoManual(prepago,prefijo)
                }

            }
            if (tipopago == "multiple") {

                if (montoCobroFaltante<=0){
                    val prefijo = if (cobroViewModel.clienteModel.value!!.documento == "BOLETA") "B" else "F"
//                    cobroViewModel.procesoPedidoFacturar(PrepagoModel(codigo="",tipoPago = "",monto = montoPedido,propina = propinaMultiple,codigoTarjeta = "",numeroTarjeta = "",referencia = "",numeroATarjeta = "",banco = ""),prefijo)
                    var pre = PrepagoModel(codigo="",tipoPago = "",monto = montoPedido,propina = propinaMultiple,codigoTarjeta = "",numeroTarjeta = "",referencia = "",numeroATarjeta = "",banco = "")
                    ProcesarPagoManual(pre,prefijo)
                    return@setOnClickListener
                }

                binding.btnfinalizarcobro.isEnabled = true
                val montoText = binding.tvMontoCobrar.text.toString().trim()
                val montocobrarmultiple: Double? = montoText.toDoubleOrNull()

                if (!montoText.isNullOrEmpty() && montocobrarmultiple != null && montocobrarmultiple > 0) {
                    val montoFaltanteRedondeado = montoCobroFaltante.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
                    val montoCobrarRedondeado = montocobrarmultiple!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
                    if (montoFaltanteRedondeado < montoCobrarRedondeado) {
                        showToast("Monto a cobrar ingresado inválido, debe ser menor a ${String.format("%.2f", montoFaltanteRedondeado)}")
                        return@setOnClickListener
                    }
                } else {
                    showToast("Ingrese un monto válido mayor a 0")
                    return@setOnClickListener
                }
                binding.btnfinalizarcobro.isEnabled = false

                if (tipopagoMultiple =="tarjeta" || tipopagoMultiple=="qr"){
                    if (cobroViewModel.resultUsuario.value?.activaniubiz == true) {
                        niubizViewModel.sendSale(
                            this,
                            montocobrarmultiple,
                            isQr = tipopago == "qr",
                            merchantCode = null
                        )

                    } else {
                        val numerDocumento = binding.etnumeroopracion.text.toString().trim()
                        val numeroTarjeta = if (numerDocumento.length >= 8) numerDocumento.substring(0, 8) else numerDocumento

                        val prepago = PrepagoModel(
                            codigo = "",
                            tipoPago = "02",
                            monto = montocobrarmultiple,
                            propina = propinaMultiple,
                            codigoTarjeta = if (tipopago == "qr") "APPQR" else (if (tipotarjeta.length>2) "APP"+tipotarjeta.substring(0, 4).uppercase() else tipotarjeta) ,
                            numeroTarjeta = numeroTarjeta,
                            referencia = tipopagoMultiple.substring(0,2).uppercase(),
                            numeroATarjeta = if (tipopago == "qr") "APPQR" else (if (tipotarjeta.length>2) "APP"+tipotarjeta.substring(0, 4).uppercase() else "otro") ,
                            banco = ""
                        )
                        cobroViewModel.procesaPrepago(prepago, tipoProceso)
                    }
                }

                if (tipopagoMultiple =="efectivo"){

                    if (montoEfectivo<montocobrarmultiple){
                        binding.btnfinalizarcobro.isEnabled = true
                        showToast("Ingresa monto de efectivo.")
                        return@setOnClickListener
                    }
                    val prepago: PrepagoModel = PrepagoModel(
                        codigo="",
                        tipoPago = "01",
                        monto = montocobrarmultiple,
                        propina = montoEfectivo,
                        codigoTarjeta = "",
                        numeroTarjeta = "",
                        referencia = "",
                        numeroATarjeta = "",
                        banco = ""
                    )
                    cobroViewModel.procesaPrepago(prepago, tipoProceso)
                }
            }
        }

        binding.llVerificaInformacionpago.setOnClickListener{
            if (binding.tvAbrecierraInformacion.text=="+") {
                binding.tvAbrecierraInformacion.text = "—"
                expand(binding.llDetalleVerificainformacion)
                binding.llVerificaInformacionpago.setBackgroundResource(R.color.colorBackground)
                binding.tvTituloInformacionpago.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

                //// pago
                binding.tvAbrecierraPago.text = "+"
                collapse(binding.llOpcionesPago)
                binding.llEligeMetodopago.setBackgroundResource(R.color.ColorFondoPantalla)
                binding.tvTituloPago.setTextColor(ContextCompat.getColor(this, R.color.colorText))
                collapse(binding.llPieTotalPagarConfirmar)
            } else {
                binding.tvAbrecierraInformacion.text = "+"
                collapse(binding.llDetalleVerificainformacion)
                binding.llVerificaInformacionpago.setBackgroundResource(R.color.ColorFondoPantalla)
                binding.tvTituloInformacionpago.setTextColor(ContextCompat.getColor(this, R.color.colorText))
                expand(binding.llPieTotalPagarConfirmar)
            }
        }

        binding.llEligeMetodopago.setOnClickListener{
            if (binding.tvAbrecierraPago.text=="+") {
                binding.tvAbrecierraPago.text = "—"

//                    binding.llOpcionesPago.isVisible = true
                expand(binding.llOpcionesPago)
                binding.llEligeMetodopago.setBackgroundResource(R.color.colorBackground)
                binding.tvTituloPago.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

                /// informacion
                binding.tvAbrecierraInformacion.text = "+"
//                    binding.llDetalleVerificainformacion.isVisible = false
                collapse(binding.llDetalleVerificainformacion)
                binding.llVerificaInformacionpago.setBackgroundResource(R.color.ColorFondoPantalla)
                binding.tvTituloInformacionpago.setTextColor(ContextCompat.getColor(this, R.color.colorText))
                expand(binding.llPieTotalPagarConfirmar)

            } else {
                binding.tvAbrecierraPago.text = "+"
//                    binding.llOpcionesPago.isVisible = false
                collapse(binding.llOpcionesPago)
                binding.llEligeMetodopago.setBackgroundResource(R.color.ColorFondoPantalla)
                binding.tvTituloPago.setTextColor(ContextCompat.getColor(this, R.color.colorText))
//                    binding.llPieTotalPagarConfirmar.isVisible = false
                collapse(binding.llPieTotalPagarConfirmar)
            }
        }

        binding.tvPropinaPago.setOnClickListener{
            if (tipopago == "multiple"){
                return@setOnClickListener
            }
            val initialValue = propina.toString() // El valor inicial que quieres mostrar en el modal
            val modal = PropinaDialogFragment.newInstance(initialValue, monedaPedido, montoPedido) { result ->
                ocultarTeclado(this)
                if (result.isNullOrBlank()) {
                    // Validación: resultado no debe estar vacío o nulo
                    //binding.tvCantidadproducto.text = "0"
                    return@newInstance
                }

                val cantidad = result.toDoubleOrNull()
                if (cantidad == null || cantidad <= 0) {
                    // Validación: resultado debe ser un número válido y no negativo
                    //binding.tvCantidadproducto.text = "0"
                    return@newInstance
                }

                propina = cantidad
                cobroViewModel.resultPedidoModel.value = cobroViewModel.resultPedidoModel.value

            }
            // Mostrar el modal
            modal.show(this.supportFragmentManager, "PropinaDialogFragment")
        }

        binding.tvPropinaMultiple.setOnClickListener{
            val montoText = binding.tvMontoCobrar.text.toString().trim()
            val montocobrarmultiple: Double? = montoText.toDoubleOrNull()
            if (!montoText.isNullOrEmpty() && montocobrarmultiple != null && montocobrarmultiple > 0) {
                val initialValue = propinaMultiple.toString() // El valor inicial que quieres mostrar en el modal
                val modal = PropinaDialogFragment.newInstance(initialValue, monedaPedido, montocobrarmultiple) { result ->
                    ocultarTeclado(this)
                    if (result.isNullOrBlank()) {
                        // Validación: resultado no debe estar vacío o nulo
                        //binding.tvCantidadproducto.text = "0"
                        return@newInstance
                    }

                    val cantidad = result.toDoubleOrNull()
                    if (cantidad == null || cantidad <= 0) {
                        // Validación: resultado debe ser un número válido y no negativo
                        //binding.tvCantidadproducto.text = "0"
                        return@newInstance
                    }

                    propinaMultiple = cantidad
                    cobroViewModel.resultPedidoModel.value = cobroViewModel.resultPedidoModel.value

                }
                // Mostrar el modal
                modal.show(this.supportFragmentManager, "PropinaDialogFragment")
            } else {
                showToast("Ingrese un monto válido mayor a 0")
            }

        }

        val linearLayouts = listOf(
            binding.llPagoQr, binding.llPagoTarjeta,
            binding.llPagoEfectivo,binding.llPagoMultiple
        )
        linearLayouts.forEach { id ->
            id.setOnClickListener{

                binding.llPagoQr.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))
                binding.llPagoTarjeta.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))
                binding.llPagoEfectivo.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))
                binding.llPagoMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))

                binding.ivPagoQr.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.ivPagoTarjeta.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.ivPagoEfectivo.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.ivPagoMultiple.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))

                binding.tvPagoQr.setTextColor(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.tvPagoTarjeta.setTextColor(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.tvPagoEfectivo.setTextColor(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.tvPagoMultiple.setTextColor(ContextCompat.getColor(this, R.color.light_blue_600))

                collapse(binding.llEfectivopanel)
                if (cobroViewModel.resultUsuario.value!!.pais =="005"){
                    binding.tvPropinaPago.isVisible = false
                } else {
                    binding.tvPropinaPago.isVisible = true
                }

                binding.imageButtonLimpiarEfectivo.isVisible =false

                binding.tvPropinaPago.setBackgroundResource(R.drawable.boton_redondo_verde)
                binding.tvPropinaPago.setTextColor(ContextCompat.getColor(this, R.color.full900))
                binding.tvPropinaPago.setText("¿Añadir propina?")
                binding.btnfinalizarcobro.setText("FINALIZAR COBRO ${monedaPedido} ${String.format("%.2f",montoPedido)}")

                collapse(binding.llTarjetapanel)

                if (tipopagoMultiple == "efectivo" && id.contentDescription =="multiple"){
                    expand(binding.llEfectivopanel)
                    binding.imageButtonLimpiarEfectivoMultiple.isVisible =true
                    binding.tvPropinaMultiple.isVisible =false
                }  else {
                    collapse(binding.llEfectivopanel)
                    binding.imageButtonLimpiarEfectivoMultiple.isVisible =false
                    binding.tvPropinaMultiple.isVisible =true
                }

                if (!cobroViewModel.resultUsuario.value!!.activaniubiz && tipopagoMultiple == "tarjeta" && id.contentDescription =="multiple"){
                    expand(binding.llTarjetapanel)
                }  else {
                    collapse(binding.llTarjetapanel)
                }

                collapse(binding.llMultiplepanel)

                binding.textInputmontocobrado.isVisible = false
                binding.etmontocobrado.setText("")
                binding.tvPropinaPago2.isVisible =false
                binding.tvPropinaPago2.setText("")

               if (id.contentDescription =="efectivo" ){
                   binding.llPagoEfectivo.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                   binding.ivPagoEfectivo.setColorFilter(ContextCompat.getColor(this, R.color.white))
                   binding.tvPagoEfectivo.setTextColor(ContextCompat.getColor(this, R.color.white))
                   expand(binding.llPieTotalPagarConfirmar)
                   expand(binding.llEfectivopanel)
                   tipopago = "efectivo"
                   propina = 0.0
                   binding.tvPropinaPago.isVisible = false
                   binding.imageButtonLimpiarEfectivo.isVisible =true

               } else if (id.contentDescription =="tarjeta" ) {
                   binding.llPagoTarjeta.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                   binding.ivPagoTarjeta.setColorFilter(ContextCompat.getColor(this, R.color.white))
                   binding.tvPagoTarjeta.setTextColor(ContextCompat.getColor(this, R.color.white))
                   //binding.llPieTotalPagarConfirmar.isVisible = true
                   expand(binding.llPieTotalPagarConfirmar)
                   tipopago = "tarjeta"
                   if (!cobroViewModel.resultUsuario.value!!.activaniubiz){
                       expand(binding.llTarjetapanel)
                   }

                   if (cobroViewModel.resultUsuario.value!!.pais=="005"){
                       binding.textInputmontocobrado.isVisible = true
                       binding.tvPropinaPago2.isVisible =true
                   }

               } else if (id.contentDescription =="qr" ) {
                   binding.llPagoQr.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                   binding.ivPagoQr.setColorFilter(ContextCompat.getColor(this, R.color.white))
                   binding.tvPagoQr.setTextColor(ContextCompat.getColor(this, R.color.white))
//                       binding.llPieTotalPagarConfirmar.isVisible = true
                   expand(binding.llPieTotalPagarConfirmar)
                   tipopago = "qr"

               } else if (id.contentDescription =="multiple" ) {
                   binding.llPagoMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                   binding.ivPagoMultiple.setColorFilter(ContextCompat.getColor(this, R.color.white))
                   binding.tvPagoMultiple.setTextColor(ContextCompat.getColor(this, R.color.white))

                   binding.tvPropinaPago.setBackgroundResource(R.drawable.boton_redondo_celeste)
                   binding.tvPropinaPago.setTextColor(ContextCompat.getColor(this, R.color.celeste900))

                   montoCobroFaltante =BigDecimal(montoPedido - cobroViewModel.resultPrepagos.value!!.sumOf { it.monto })
                       .setScale(2, RoundingMode.HALF_UP)
                       .toDouble()
                   binding.tvPropinaPago.setText("Falta cobrar ${monedaPedido}${ String.format("%.2f",montoCobroFaltante)}")

                   if (montoCobroFaltante<=0){
                       binding.llCobroMultiple.isVisible =false
                       binding.llMontoMultiple.isVisible =false
                       collapse(binding.llEfectivopanel)
                   } else {
                       binding.llCobroMultiple.isVisible =true
                       binding.llMontoMultiple.isVisible =true
                       binding.btnfinalizarcobro.setText("COBRAR")
                   }

                   expand(binding.llPieTotalPagarConfirmar)
                   tipopago = "multiple"
                   expand(binding.llMultiplepanel)

               }
            }
        }

        val linearLayoutsMultiple = listOf(
            binding.llPagoQrMultiple, binding.llPagoTarjetaMultiple,
            binding.llPagoEfectivoMultiple
        )
        linearLayoutsMultiple.forEach { id ->
            id.setOnClickListener{
                binding.llPagoQrMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))
                binding.llPagoTarjetaMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))
                binding.llPagoEfectivoMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorFondoPantalla))

                binding.ivPagoQrMultiple.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.ivPagoTarjetaMultiple.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))
                binding.ivPagoEfectivoMultiple.setColorFilter(ContextCompat.getColor(this, R.color.light_blue_600))

                collapse(binding.llEfectivopanel)
                collapse(binding.llTarjetapanel)
                binding.imageButtonLimpiarEfectivoMultiple.isVisible =false
                binding.tvPropinaMultiple.isVisible = true

                if (id.contentDescription =="efectivo" ){
                    binding.llPagoEfectivoMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                    binding.ivPagoEfectivoMultiple.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    tipopagoMultiple = "efectivo"
                    binding.imageButtonLimpiarEfectivoMultiple.isVisible =true
                    binding.tvPropinaMultiple.isVisible = false
                    expand(binding.llEfectivopanel)

                } else if (id.contentDescription =="tarjeta" ) {
                    binding.llPagoTarjetaMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                    binding.ivPagoTarjetaMultiple.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    tipopagoMultiple = "tarjeta"
                    if (!cobroViewModel.resultUsuario.value!!.activaniubiz) {
                        expand(binding.llTarjetapanel)
                    }

                } else if (id.contentDescription =="qr" ) {
                    binding.llPagoQrMultiple.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600))
                    binding.ivPagoQrMultiple.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    tipopagoMultiple = "qr"

                }
            }
        }

        val botonesEfectivo = listOf(
            binding.tvefectivo5 to "5",
            binding.tvefectivo10 to "10",
            binding.tvefectivo20 to "20",
            binding.tvefectivo50 to "50",
            binding.tvefectivo100 to "100",
            binding.tvefectivoexacto to "Exacto",
        )
        botonesEfectivo.forEach { (button, amount) ->
            button.setOnClickListener {
                var montoacobrar = montoPedido
                if (tipopago == "multiple" && tipopagoMultiple == "efectivo"){
                    val montoText = binding.tvMontoCobrar.text.toString().trim()
                    val montocobrarmultiple: Double? = montoText.toDoubleOrNull()
                    if (!montoText.isNullOrEmpty() && montocobrarmultiple != null && montocobrarmultiple > 0) {
                        montoacobrar = montocobrarmultiple
                    } else {
                        showToast("Ingrese un monto válido mayor a 0")
                        return@setOnClickListener
                    }

                }
                if (montoacobrar>montoEfectivo){
                    if (amount =="Exacto"){
                        mostrarNumPadDialog(this.supportFragmentManager){ cantidad ->
//
                            montoEfectivo = cantidad

                            var vueltoEfectivo = montoEfectivo-montoacobrar
                            val vueltoRedondeado = BigDecimal(vueltoEfectivo)
                                .setScale(2, RoundingMode.HALF_UP)
                                .toPlainString()

                            binding.tvefectivopagacon.setText("Paga con: ${monedaPedido}${montoEfectivo}")
                            if (vueltoEfectivo>0){
                                binding.tvefectivovuelto.setText("Vuelto: ${monedaPedido}${vueltoRedondeado}")
                            } else {
                                binding.tvefectivovuelto.setText("Vuelto: -")
                            }

                        }
//                        montoEfectivo = montoacobrar
                    } else {
                        var selectedAmount = amount.toDouble()
                        montoEfectivo +=selectedAmount
                    }
                }
                if (montoEfectivo>0){
                    var vueltoEfectivo = montoEfectivo-montoacobrar
                    val vueltoRedondeado = BigDecimal(vueltoEfectivo)
                        .setScale(2, RoundingMode.HALF_UP)
                        .toPlainString()

                    binding.tvefectivopagacon.setText("Paga con: ${monedaPedido}${montoEfectivo}")
                    if (vueltoEfectivo>0){
                        binding.tvefectivovuelto.setText("Vuelto: ${monedaPedido}${vueltoRedondeado}")
                    } else {
                        binding.tvefectivovuelto.setText("Vuelto: -")
                    }

                } else {
                    binding.tvefectivopagacon.setText("Paga con: -")
                    binding.tvefectivovuelto.setText("Vuelto: -")
                }
            }
        }

        binding.imageButtonLimpiarEfectivo.setOnClickListener{
            limpiarEfectivoPaga()
        }
        binding.imageButtonLimpiarEfectivoMultiple.setOnClickListener{
            limpiarEfectivoPaga()
        }

        val viewTarjetas = listOf(
            binding.ivtarjetavisa to "visa",
            binding.ivtarjetamastercard to "mastercard",
            binding.ivtarjetadiners to "diners",
            binding.ivtarjetaamercian to "american",
            binding.ivtarjetaotro to "otro"
        )

        viewTarjetas.forEach { (view, tarjeta) ->
            view.setOnClickListener {
                // Poner todas las tarjetas en color base
                viewTarjetas.forEach { (tarjetaView, _) ->
                    tarjetaView.setBackgroundResource(R.color.white) // Color base
                }
                tipotarjeta = ""
                if (tarjeta=="otro"){
                    // Crear el fragmento y pasarle el objeto PedidoModel y el callback
                    val fragment = TarjetaBancariaFragment.newInstance(cobroViewModel.resultTarjetasBancarias.value!!) { result ->
                        // Este callback se ejecuta cuando el fragmento devuelve el resultado
                        // Aquí puedes manejar el objeto PedidoModel que has devuelto desde el fragmento
                        //showToast("Resultado recibido: $result")
                        if (!result.isNullOrBlank()) {
                            // Resaltar la tarjeta seleccionada
                            view.setBackgroundResource(R.color.colorBackground2) // Color de selección
                            // Guardar el valor de la tarjeta seleccionada
                            tipotarjeta = result
                        } else {
                            // Resaltar la tarjeta seleccionada
                            view.setBackgroundResource(R.color.white) // Color de selección
                            // Guardar el valor de la tarjeta seleccionada
                            tipotarjeta = ""
                            showToast("Seleccione una tarjeta")
                        }


                    }

                    // Mostrar el BottomSheetDialogFragment
                    fragment.show(supportFragmentManager, "TarjetaBancariaFragment")
                }else {
                    // Resaltar la tarjeta seleccionada
                    view.setBackgroundResource(R.color.colorBackground2) // Color de selección
                    // Guardar el valor de la tarjeta seleccionada
                    tipotarjeta = tarjeta
                }

            }
        }

        binding.btnIncrementa.setOnClickListener{
            var cantidad = binding.tvCantidadcopias.text.toString().toDouble()
            cantidad+=1
            binding.tvCantidadcopias.text = cantidad.toInt().toString()
//            productoViewModel.resultProductoModel.value?.let { productoActual ->
//                if (productoActual.cantidad+1>productoActual.stockcritico && productoActual.lcontrolcritico){
//                    showToast("Stock Crítico: ${String.format("%.2f", productoActual.stockcritico)}")
//                    return@setOnClickListener
//                }
//                productoActual.cantidad += 1
//                updateDialogProductInfo(productoActual)
//            }
        }

        binding.btnDisminuye.setOnClickListener{
//            productoViewModel.resultProductoModel.value?.let { productoActual ->
//                if (productoActual.cantidad > 1) {
//                    productoActual.cantidad--
//                } else
//                    return@setOnClickListener
//                updateDialogProductInfo(productoActual)
//            }

            var cantidad = binding.tvCantidadcopias.text.toString().toDouble()
            cantidad-=1
            if (cantidad<=0){
                cantidad= 0.0
            }
            binding.tvCantidadcopias.text = cantidad.toInt().toString()
        }


    } catch (_: Exception) {

    }

    /**
     * Manejo de la respuesta de Niubiz.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VNP_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var result  = niubizViewModel.checkActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    val message = buildResultMessage(result)

                    val montoText = binding.tvMontoCobrar.text.toString().trim()
                    val montocobrarmultiple: Double? = montoText.toDoubleOrNull()

                    //binding.tvprueba.text = message  // Mostrar en el TextView
                    if (result.transactionParams?.operationType=="00") {
                        val prefijo = if (cobroViewModel.clienteModel.value!!.documento == "BOLETA") "B" else "F"
                        val prepago = PrepagoModel(
                            codigo          = "",
                            tipoPago        = "02",
                            monto           = if (tipopago == "multiple") montocobrarmultiple!! else montoPedido,
                            propina         = if (tipopago == "multiple") propinaMultiple else propina,
                            codigoTarjeta   = if (result.transactionParams?.isQrPayment == "1") "NIUBIZ-QR" else "NIUBIZ-TA",
                            numeroTarjeta   = result.transactionParams?.maskedCardNumber ?: "",
                            referencia      = "",
                            numeroATarjeta  = result.transactionParams?.cardBrand       ?: "",
                            banco           = ""
                        )

                        val numerocopia = binding.tvCantidadcopias.text.toString().toInt()

                        var tamano : Int = 32
                        var printerT = LocalStore.getPrinterBluetooth(this)
                        if (printerT=="50"){tamano=32}
                        if (printerT=="55"){tamano=34}
                        if (printerT=="80"){tamano=40}

                        var printerDocW = LocalStore.getPrinterBluetoothDocumentoWindow(this)
                        var activadoc: Boolean = false
                        if (printerDocW=="1")
                            activadoc = true

                        if (tipopago=="multiple"){
                            cobroViewModel.procesaPrepago(prepago, tipoProceso)
                        } else {
                            cobroViewModel.procesoPedidoFacturar(prepago,prefijo,"000",numerocopia,tamano,activadoc, tipoProceso)
                        }

                    } else {
                        binding.btnfinalizarcobro.isEnabled = true
                    }
                } else {
                    //binding.tvprueba.text = "No se recibió respuesta válida."
                    binding.btnfinalizarcobro.isEnabled = true
                }

            } else {
                showToast("Error o cancelación en la transacción")
                binding.btnfinalizarcobro.isEnabled = true
            }
        }
    }

    private fun buildResultMessage(result: CheckActivityResultDataNiubiz): String {
        val params = result.transactionParams
        return """
            ** Resultado de la Transacción **
            
            Código de respuesta: ${params?.operationType ?: "N/A"}
            Mensaje: ${result.responseMessage ?: "Desconocido"}
            
            ** Parámetros de la Transacción **
            ID Transacción: ${params?.transactionId ?: "N/A"}
            ID Comercio: ${params?.merchantId ?: "N/A"}
            Serie del Terminal: ${params?.terminalSerial ?: "N/A"}
            Tarjeta Enmascarada: ${params?.maskedCardNumber ?: "N/A"}
            Marca de la Tarjeta: ${params?.cardBrand ?: "N/A"}
            Número de Lote: ${params?.batchNumber ?: "N/A"}
            Referencia: ${params?.referenceNumber ?: "N/A"}
            Código de Autorización: ${params?.authorizationCode ?: "N/A"}
            Monto Transacción: ${params?.transactionAmount ?: "N/A"}
            Propina: ${params?.tipAmount ?: "N/A"}
            Pago con QR: ${if (params?.isQrPayment == "1") "Sí" else "No"}
            Fecha: ${params?.transactionDate ?: "N/A"}
            Hora: ${params?.transactionTime ?: "N/A"}
            Moneda: ${params?.currencyCode ?: "N/A"}
            
            Estado: ${interpretarEXTOP(result.errorCode)}
        """.trimIndent()
    }

    private fun interpretarEXTOP(extop: String?): String {
        return when (extop) {
            "00" -> "Transacción exitosa"
            "01" -> "Transacción fallida"
            "07" -> "Inicialización exitosa"
            "08" -> "Inicialización fallida"
            "13" -> "Operación cancelada"
            "99" -> "Error de formato"
            else -> "Código desconocido"
        }
    }
}