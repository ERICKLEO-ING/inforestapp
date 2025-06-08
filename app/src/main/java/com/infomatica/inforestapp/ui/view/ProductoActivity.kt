package com.infomatica.inforestapp.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.infomatica.inforestapp.GlobalVariables
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.ToppingModel
import com.infomatica.inforestapp.databinding.ActivityProductoBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.ModificadorAdapter
import com.infomatica.inforestapp.ui.fragment.TercerNivelDialogFragment
import com.infomatica.inforestapp.ui.viewmodel.ProductoViewModel
import java.text.DecimalFormat

class ProductoActivity : BaseActivity(),ModificadorAdapter.ItemClickListener {

    private lateinit var binding: ActivityProductoBinding
    private val productoViewModel: ProductoViewModel by viewModels()
    private lateinit var modificadorAdapter: ModificadorAdapter

    private var isModificaPedido = false
    private var observacionPedido = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productoViewModel.onCreate()
        overridePendingTransition(R.anim.slide_in_right, 0)
        setupObservers()
        setupClickOnListener()
        configurarAdapters()

        val editText = binding.tvCantidadproducto

// Crear un filtro para permitir solo números, punto y coma
        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            // Permitir solo números, punto y coma
            if (source.matches("[0-9.,]*".toRegex())) {
                null // El texto es válido
            } else {
                "" // El texto no es válido, lo eliminamos
            }
        }

// Aplicar el filtro al EditText
        editText.filters = arrayOf(inputFilter)



    }
    private fun configurarAdapters() {
        binding.rvDetallmodificadores.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        modificadorAdapter = ModificadorAdapter(this,this)
        binding.rvDetallmodificadores.adapter = modificadorAdapter
    }
    private fun setupObservers() {
        try {
            productoViewModel.resultProductoModel.observe(this, Observer {
                if (it != null) {
                    binding.btnmodificarprecio.isVisible = it.lcambiaprecio
                    binding.btnmodificarprecio.setText(it.moneda)
                    Glide.with(this)
                        .load(it.urlimagen)
                        .placeholder(R.drawable.ver_imagen_36x36)
                        .error(R.drawable.ver_imagen_36x36)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivproducto)
                    //binding.tvNombreproductoDialog.text = it.nombre
                    if (it.descripcion.isNotEmpty()){
                        binding.tvDescripcionproductodialog.text = it.descripcion
                    } else {
                        binding.tvDescripcionproductodialog.isVisible =false
                    }

                    val cantidad = it.cantidad // El valor puede ser Double o Float
                    binding.tvCantidadproducto.setText(
                        if (cantidad % 1 == 0.0) {
                            // Si es un número entero
                            cantidad.toInt().toString()
                        } else {
                            // Si tiene decimales, muestra como máximo 3
                            DecimalFormat("#.###").format(cantidad)
                        }
                    )


                    if (it.lcontrolcritico && it.stockcritico>0){
                        binding.cvalertacontenedor.isVisible =true
                        binding.tvalertamensaje.setText("Stock Crítico: ${String.format("%.2f", it.stockcritico)} -> Solicitado: ${String.format("%.2f", it.cantidad)}")
                        // O cerrar automáticamente después de 3 segundos
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.cvalertacontenedor.isVisible = false
                        }, 3000)
                    }


                    binding.btnAgregarproducto.text = "${it.moneda} ${String.format("%.2f", it.precio*it.cantidad)}"
                    binding.tvObservacion.setText(it.observacion)
                    observacionPedido = it.observacion
                    if (it.isimpreso){
                        binding.btnAgregarproducto.isEnabled =false
                        binding.btnAgregarproducto.background = ContextCompat.getDrawable(this, R.drawable.boton_redondo_gris)
                        //binding.btnAgregarproducto.text = "${it.moneda} ${String.format("%.2f", it.preciounitario*it.cantidad)}"

                        binding.btnIncrementa.isEnabled = false
                        binding.btnDisminuye.isEnabled = false
                        binding.tvCantidadproducto.isEnabled = false

                        binding.btnDisminuye.setColorFilter(ContextCompat.getColor(this, R.color.lightgray_600))
                        binding.btnIncrementa.setColorFilter(ContextCompat.getColor(this, R.color.lightgray_600))

                        if (it.observacion.isEmpty()){
                            //view.findViewById<EditText>(R.id.tv_observacion).isVisible =false
                            binding.tvObservacion.isVisible = false
                        } else  {
                            //view.findViewById<EditText>(R.id.tv_observacion).isEnabled =false
                            binding.tvObservacion.isEnabled = false
                        }
                        binding.tvNombreproductoDialog.text =
                        it.nombre.substringBefore("||") + " ${it.moneda} ${String.format("%.2f", it.preciounitario)}"
                    } else{
                        binding.tvNombreproductoDialog.text = it.nombre
                    }


                    val adapter = binding.spinnerClientes.adapter as? ArrayAdapter<String> // Casting seguro
                    val nombrecliente = it.clientenombre ?: ""

                    if (adapter != null && !adapter.isEmpty && nombrecliente.isNotEmpty()) {
                        val position = adapter.getPosition(nombrecliente) // Busca la posición del cliente
                        if (position >= 0) {
                            binding.spinnerClientes.setSelection(position) // Selecciona el valor en el Spinner
                        } else {
                            showToast( "El valor '$nombrecliente' no está en la lista del Spinner")
                        }
                    }

                    binding.tvOrdenItem.setText("${it.orden}°")

                }
            })
            productoViewModel.resultModificadoresModel.observe(this) {
                if (!it.isNullOrEmpty()) {
                    productoViewModel.resultProductoModel.value?.let { prod ->
                        modificadorAdapter.submitList(it,prod)
                        updateDialogProductInfo(prod)
                    }
                }
            }
            productoViewModel.resultPedidoModel.observe(this) {
                handlePedidoResult(it)
            }
            productoViewModel.isLoading.observe(this@ProductoActivity, Observer { binding.pgCargando.isVisible = it })
        } catch (ex:Exception) {
            ex.message?.let { showToast(it) }
        }
    }

    private fun handlePedidoResult(result: PedidoModel?) {
        when (result?.status) {
            0 -> showSuccessMessage()
            1 -> showFailureMessage()
            99 -> showErrorMessage(result.mensaje)
            100 -> {
                val spinnerItems = result?.clientes?.toList()
                // Verificar si spinnerItems no es nulo
                if (!spinnerItems.isNullOrEmpty()) {
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        spinnerItems
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerClientes.adapter = adapter
                } else {
                    binding.cvSpinnerClientes.isVisible = false
                }
            }
        }
    }

    private fun showSuccessMessage() {
        if (isModificaPedido){
            showToast("> Pedido Modificado <" )
        }else
        {
            showToast("> ${productoViewModel.resultProductoModel.value?.nombre} - Agregado <" )
            finishActivity("0")
        }
        isModificaPedido =false
    }

    private fun showFailureMessage() {
        showToast( "Operación fallida")
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this@ProductoActivity)
            .setTitle("INFOREST APP")
            .setMessage(message)
            .setNegativeButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    override fun onToppingClick(topping: ToppingModel){
        productoViewModel.resultProductoModel.value?.let { it1 ->
            updateDialogProductInfo(it1)
        }
    }

    override fun onToppingModificadoresClick(topping: ToppingModel) {
        topping.let {
            abrirModalTercerNivel(it.nombre, topping.modificadores)
        }
    }

    private fun setupClickOnListener() {
        try {
            binding.btnCerrarDetallepedido.setOnClickListener {
                finishActivity("0")
            }

            binding.btncerraralerta.setOnClickListener {
                binding.cvalertacontenedor.isVisible = false
            }

            binding.btnIncrementa.setOnClickListener{
                productoViewModel.resultProductoModel.value?.let { productoActual ->
                    if (productoActual.cantidad+1>productoActual.stockcritico && productoActual.lcontrolcritico){
                        showToast("Stock Crítico: ${String.format("%.2f", productoActual.stockcritico)}")
                        return@setOnClickListener
                    }
                    productoActual.cantidad += 1
                    updateDialogProductInfo(productoActual)
                }
            }

            binding.btnDisminuye.setOnClickListener{
                productoViewModel.resultProductoModel.value?.let { productoActual ->
                    if (productoActual.cantidad > 1) {
                        productoActual.cantidad--
                    } else
                        return@setOnClickListener
                    updateDialogProductInfo(productoActual)
                }
            }

            binding.btnAgregarproducto.setOnClickListener {
                if (!binding.btnAgregarproducto.isEnabled) {
                    return@setOnClickListener
                }
                productoViewModel.resultProductoModel.value?.let { productoActual ->
                    val modificadores = modificadorAdapter.listaModificador
                    modificadores.forEach { modificador ->
                        var toppingsseleccionados = modificador.toppings.sumOf { topping ->
                            topping.cantidad
                        }
                        if (modificador.isobligatorio && toppingsseleccionados == 0.0){
                            showToast(modificador.nombre + " es obligatorio!")
                            modificadorAdapter.notifyDataSetChanged()
                            return@setOnClickListener
                        }
                        if (modificador.iscombomodificador){
                          modificador.toppings.filter { x -> x.modificadores.size>0 && x.cantidad>0 }.forEach { itemcombo ->
                              itemcombo.modificadores.forEach {
                                  toppingsseleccionados = it.toppings.sumOf { topping ->
                                      topping.cantidad
                                  }
                                  if (it.isobligatorio && toppingsseleccionados == 0.0){
                                      abrirModalTercerNivel(itemcombo.nombre, itemcombo.modificadores)
                                      return@setOnClickListener
                                  }

                              }
                          }
                        }
                    }

                    binding.btnAgregarproducto.isEnabled = false

                    if ( modificadores.isNotEmpty()){
                        productoActual.modificadores = mutableListOf()
                        productoActual.modificadores = modificadores.toMutableList()
                    }

                    val selectedItem = binding.spinnerClientes.selectedItem?.toString()?.trim() ?: ""
                    if (selectedItem.isNotEmpty()) {
                        productoActual.clientenombre = selectedItem
                    } else {
                        productoActual.clientenombre = ""
                    }
                    productoViewModel.generaPedidoDetalle(productoActual)
                }
            }

            binding.tvCantidadproducto.setOnClickListener {
                mostrarNumPadDialog(this.supportFragmentManager){ cantidad ->
                    productoViewModel.resultProductoModel.value?.let { productoActual ->
                        if (cantidad>productoActual.stockcritico && productoActual.lcontrolcritico){
                            showToast("Stock Crítico: ${String.format("%.2f", productoActual.stockcritico)}")
                            return@let
                        }
                        productoActual.cantidad = String.format("%.3f", cantidad).toDouble() // Redondeo y conversión a Double
                        updateDialogProductInfo(productoActual)
                    }
                }
            }

            binding.btnObservacionPedido.setOnClickListener {
                observacionDialog(this.supportFragmentManager,observacionPedido){ respuesta ->
                    productoViewModel.resultProductoModel.value?.let { productoActual ->
                        productoActual.observacion= respuesta
                        updateDialogProductInfo(productoActual)
                    }
                }
            }

            binding.ivproducto.setOnClickListener {
//                val context = binding.root.context
//                val scale = context.resources.displayMetrics.density
//
//                val heightDpActual = (binding.flImagenProducto.height / scale).toInt()
//
//                val nuevaAlturaDp = if (heightDpActual == 180) 300 else 180
//                val nuevaAlturaPx = (nuevaAlturaDp * scale + 0.5f).toInt()
//
//                val params = binding.flImagenProducto.layoutParams
//                params.height = nuevaAlturaPx
//                binding.flImagenProducto.layoutParams = params
                productoViewModel.resultProductoModel.value?.let { productoActual ->
                    if (productoActual.urlimagen.isNotEmpty())
                        abrirImagen(productoActual.urlimagen)
                }
            }

            binding.btnmodificarprecio.setOnClickListener {
                mostrarNumPadDialog(this.supportFragmentManager){ cantidad ->
                    productoViewModel.resultProductoModel.value?.let { productoActual ->
                        if (productoActual.preciounitario<=0){
                            showToast("El precio no puede ser 0.00")
                            return@let
                        }
                        productoActual.montocambiaprecio = String.format("%.3f", cantidad).toDouble() // Redondeo y conversión a Double
                        productoActual.descuento = 0.00
                        productoActual.descuentounitario = 0.00
                        updateDialogProductInfo(productoActual)
                    }
                }
            }

            binding.tvOrdenItem.setOnClickListener {
                mostrarNumPadDialog(this.supportFragmentManager,"orden"){ cantidad ->
                    var enterocantidad = cantidad.toInt()
                    if (enterocantidad<0){
                        showToast("El orden debe ser mayor a 0")
                        return@mostrarNumPadDialog
                    }
                    productoViewModel.resultProductoModel.value?.let { productoActual ->
                        productoActual.orden = enterocantidad
                        updateDialogProductInfo(productoActual)
                    }

                }
            }


        } catch (ex: Exception) {
            ex.message?.let { showToast(it) }
        }
    }
    private fun abrirImagen(url: String) {
        val context = binding.root.context

        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            setPadding(10, 10, 10, 10)
        }

        // Validar que la URL no esté vacía
        if (url.isEmpty()) {
            Toast.makeText(context, "Imagen no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear y mostrar el diálogo
        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(imageView)

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ver_imagen_36x36) // opcional
            .error(R.drawable.ver_imagen_36x36)             // opcional
            .into(imageView)

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun abrirModalTercerNivel (
        initialValue: String,
        listModificadores: MutableList<ModificadorModel>
    ){
        val fragment = supportFragmentManager.findFragmentByTag("TercerNivelDialogFragment")
        if (fragment != null) return  // Si ya existe, no hacer nada
        val modal = productoViewModel.resultProductoModel.value?.let { it1 ->
            TercerNivelDialogFragment.newInstance(initialValue, listModificadores, it1) { _, _ ->
                modificadorAdapter.notifyDataSetChanged()
                updateDialogProductInfo(it1)
            }
        }
        // Mostrar el modal si no está abierto
        modal?.show(this.supportFragmentManager, "TercerNivelDialogFragment")
    }

    private fun updateDialogProductInfo( productoModel: ProductoModel) {
        var modificadores = modificadorAdapter.listaModificador
        try {
            val preciomodificadores = (
                    modificadores.sumOf { modificador ->
                        modificador.toppings.filter { x -> x.cantidad>0 }.sumOf { topping ->
                            if (modificador.iscombomodificador){
                                ((topping.cantidad * topping.precio)/productoModel.cantidad)
                                topping.modificadores.sumOf {
                                    it.toppings.sumOf { to -> to.cantidad * to.precio }
                                }
                            } else {
                                topping.cantidad * topping.precio
                            }
                        }
                    } )

            val precioTotal =  if (productoModel.montocambiaprecio > 0.00)  { productoModel.montocambiaprecio } else { ( preciomodificadores + productoModel.preciounitario )}
            productoModel.montocambiaprecio -= preciomodificadores
            // Modificar directamente la lista del adaptador
            modificadores.forEach { modificador ->
                modificador.toppings.filter { it.cantidad <= 0 }.forEach { top ->
                    top.descripciontercernivel = ""
                    top.modificadores?.flatMap { it.toppings }?.forEach { it.cantidad = 0.0 }
                }

                if (modificador.iscombomodificador) {
                    modificador.toppings.forEach { topping ->
                        if (topping.cargoauttopping && topping.eliminafijotopping) {
                            topping.cantidad = topping.aumentatopping* productoModel.cantidad // Multiplicar directamente
                        }
                    }
                }
            }
            val modificadoresCombo = modificadores.filter { it.iscombomodificador}
//            val modificadoresComboObligatorios = modificadores.filter { it.iscombomodificador && it.isobligatorio }
            // Calculamos la suma total de los toppings antes del forEach
            val cantidadToppings = modificadoresCombo
                .flatMap { it.toppings }
                .sumOf { it.cantidad }

            // Calculamos el valor máximo y asignamos a cada modificador
            modificadoresCombo.filter { x -> !x.isobligatorio }.forEach { mod ->
                mod.hastaCombo = ((productoModel.cantidadmax * productoModel.cantidad).toInt() - cantidadToppings).toInt()
            }

            if ((productoModel.cantidadmin*productoModel.cantidad)>cantidadToppings){
                binding.btnAgregarproducto.isEnabled = false
            } else {
                binding.btnAgregarproducto.isEnabled = true
            }

            // Notificar al adaptador que los datos han cambiado
            modificadorAdapter.notifyDataSetChanged()

            productoModel.precio =precioTotal//*productoModel.cantidad
            productoViewModel.resultProductoModel.value = productoModel
        } catch (_:Exception){

        }

    }
}