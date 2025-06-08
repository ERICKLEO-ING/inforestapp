package com.infomatica.inforestapp.ui.view

import LocalStore
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.databinding.ActivityDetallePedidoBinding
import com.infomatica.inforestapp.databinding.BottomSheetDetallepedidoBinding
import com.infomatica.inforestapp.extension.ConstansNiubiz.VNP_REQUEST_CODE
import com.infomatica.inforestapp.ui.BaseActivity

import com.infomatica.inforestapp.ui.adapter.DetallePedidoAdapter
import com.infomatica.inforestapp.ui.adapter.ModificadorAdapter
import com.infomatica.inforestapp.ui.fragment.ImpresoraCajaFragment
import com.infomatica.inforestapp.ui.view.VentaActivity.Companion.KEY_RESULT
import com.infomatica.inforestapp.ui.viewmodel.DetallePedidoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetallePedidoActivity : BaseActivity(),
    DetallePedidoAdapter.ItemClickListener{

    private lateinit var binding: ActivityDetallePedidoBinding
    private val detalleViewModel: DetallePedidoViewModel by viewModels()
    private var productoSeleccionado: ProductoModel? = null
    private lateinit var modificadorAdapter: ModificadorAdapter
    private var isBottomSheetProductoModal= false
    var receivedVariableActivity:String = ""
    var MontoTotal: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityDetallePedidoBinding.inflate(layoutInflater)
            // Obtiene el color desde recursos
//            val bgColor = ContextCompat.getColor(this, R.color.ColorTranslucido)
//            // Asigna un ColorDrawable al fondo de la ventana
//            window.setBackgroundDrawable(ColorDrawable(bgColor))

            setContentView(binding.root)
            detalleViewModel.onCreate()

//

            val intent = intent // Obtiene el Intent que inició la Activity
            receivedVariableActivity =  intent.getStringExtra("Key_Carta").toString() // Recupera el valor usando la misma clave

            binding.rvDetallpedido.setLayoutManager(
                GridLayoutManager(
                    applicationContext,
                    1,
                    RecyclerView.VERTICAL,
                    false
                )
            )

            setupObservers()
            setupClickOnListener()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setupObservers() {
        detalleViewModel.resultPedidoModel.observe(this, Observer { currentCute ->
            when (currentCute?.status) {
                0 -> {
                    // Acción si el resultado es true
                    //Toast.makeText(this, "Eliminado: ${productoSeleccionado?.nombre}" , Toast.LENGTH_SHORT).show()
                    binding.tvCodigopedido.text = "Pedido ${currentCute.codigopedido}"

                    binding.tvCodigomesa.text = currentCute.nombremesa

                    currentCute.detalle.filter { x -> !x.isimpreso }.let {

                        if (it.isNotEmpty() || !detalleViewModel.resultUsuarioModel.value!!.activacajero){
                            binding.btnEnviarOrder.text = "ENVIAR"
                        }else {
                            binding.btnEnviarOrder.text = "REGISTRAR COBRO"
                        }
                    }

                    if ( currentCute.detalle.isEmpty()){
                        binding.btnEnviarOrder.text = "ENVIAR"
                        binding.btnEnviarOrder.isEnabled = false
                    }

                    currentCute.detalle.let {
                        val detallePedidotoAdapter = DetallePedidoAdapter(currentCute.detalle, this)
                        binding.rvDetallpedido.adapter = detallePedidotoAdapter
                    }

                    binding.tvOrderSubtotal.text = "Subtotal: ${currentCute.monedapedido} ${ String.format("%.2f", currentCute.detalle.sumOf { it.itemventa })}"

                }
                100 -> {
                    // Acción si el resultado es true
                    binding.tvCodigopedido.text = "Pedido ${currentCute.codigopedido}"

                    binding.tvCodigomesa.text = currentCute.nombremesa
                    currentCute.detalle.let {
                        val detallePedidotoAdapter = DetallePedidoAdapter(currentCute.detalle, this)
                        binding.rvDetallpedido.adapter = detallePedidotoAdapter

                    }
                    currentCute.detalle.filter { x -> !x.isimpreso }.let {
                        if (it.isNotEmpty() || !detalleViewModel.resultUsuarioModel.value!!.activacajero){
                            binding.btnEnviarOrder.text = "ENVIAR"
                        } else {
                            binding.btnEnviarOrder.text = "REGISTRAR COBRO"
                        }
                    }

                    if ( currentCute.detalle.isEmpty()){
                        binding.btnEnviarOrder.text = "ENVIAR"
                        binding.btnEnviarOrder.isEnabled = false
                    }

                    binding.tvOrderSubtotal.text = "Subtotal: ${currentCute.monedapedido} ${String.format("%.2f", currentCute.detalle.sumOf { it.itemventa })}"
                    MontoTotal =  currentCute.detalle.sumOf { it.itemventa }

                    val descuento =  currentCute.detalle.sumOf { it.descuento }

                    if (descuento>0){
                        binding.tvOrderDescuento.isVisible = true
                        binding.tvOrderDescuento.text = "descuento: ${currentCute.monedapedido} ${String.format("%.2f", descuento)}"
                    }

                }
                1 -> {
                    // Acción si el resultado es false
                    Toast.makeText(this, "Operación fallida", Toast.LENGTH_LONG).show()
                }
                99 -> {

                    AlertDialog.Builder(this@DetallePedidoActivity)
                        .setTitle("INFOREST APP")
                        .setMessage(currentCute.mensaje)
                        .setNegativeButton("Ok") { dialog, _ ->
                            // Cierra el diálogo si el usuario cancela
                            dialog.dismiss()
                        }
                        .show()

                }
            }

        })

        detalleViewModel.resultEliminaPedidoModel.observe(this, Observer { currentCute ->
            when (currentCute?.status) {
                0 -> {
                    // Acción si el resultado es true
                    Toast.makeText(this, "Pedido eliminado: ${currentCute.codigopedido}" , Toast.LENGTH_SHORT).show()
                    finishActivity("elimina")
                }
                1 -> {
                    // Acción si el resultado es false
                    Toast.makeText(this, "Operación fallida", Toast.LENGTH_LONG).show()
                }
                99 -> {

                    AlertDialog.Builder(this@DetallePedidoActivity)
                        .setTitle("INFOREST APP")
                        .setMessage(currentCute.mensaje)
                        .setNegativeButton("Ok") { dialog, _ ->
                            // Cierra el diálogo si el usuario cancela
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        })

        detalleViewModel.resultModificadorModel.observe(this, Observer { modifiers ->
            productoSeleccionado?.let { modificadorAdapter.submitList(modifiers.orEmpty(), it) }
        })

        detalleViewModel.isLoading.observe(this, Observer { x ->
            binding.pgCargando.isVisible = x
        })

        detalleViewModel.resultImpresionPedidoModel.observe(this, Observer{currentCute ->
            when (currentCute?.status) {
                0 -> {
                    if (currentCute.impresionprecuenta.isNotEmpty()) {

                        //niubizViewModel.sendPrintPrecuenta(this, currentCute.impresionprecuenta)
                        var printerPrecuenta = LocalStore.getPrinterBluetoothPrecuenta(this)
                        if (printerPrecuenta=="1"){
                            imprimirEnPOS(currentCute.impresionprecuenta, "",this)
                        }
                        //imprimirEnPOS(currentCute.impresionprecuenta, "",this)

                    //niubizViewModel.sendTestPrint(this)

                    //niubizViewModel.sendTestPrintImage(this)

                    //imprimirEnPOS("HOLA MUNDO", this)
                    }
                    // Acción si el resultado es true
                    Toast.makeText(this, "${currentCute.mensaje}" , Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    // Acción si el resultado es false
                    Toast.makeText(this, "Operación fallida", Toast.LENGTH_LONG).show()
                }
                99 -> {

                    AlertDialog.Builder(this@DetallePedidoActivity)
                        .setTitle("INFOREST APP")
                        .setMessage(currentCute.mensaje)
                        .setNegativeButton("Ok") { dialog, _ ->
                            // Cierra el diálogo si el usuario cancela
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        })

    }
    private fun setupClickOnListener() {

        binding.btnEnviarOrder.setOnClickListener {

            if (binding.btnEnviarOrder.text == "ENVIAR" ){
                detalleViewModel.imprimeComanda()
            } else {
                val intent = Intent(this, ClienteActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
                pedidoLauncher.launch(intent, options)
                //val monto = MontoTotal // Monto en soles (convertido a céntimos en el ViewModel)
                //niubizViewModel.sendSale(this, monto, isQr = false, merchantCode = null)
            }

        }

        binding.llPrecuentaOrder.setOnClickListener {
            var printerPrecuentaW = LocalStore.getPrinterBluetoothPrecuentaWindow(this)
            if (printerPrecuentaW=="0")
            {
                var tamano : Int = 32
                var printerT = LocalStore.getPrinterBluetooth(this)
                if (printerT=="50"){tamano=32}
                if (printerT=="55"){tamano=34}
                if (printerT=="80"){tamano=40}

                detalleViewModel.imprimePrecuenta("000",tamano,false )
               return@setOnClickListener
            }

            val fragment = ImpresoraCajaFragment.newInstance(detalleViewModel.resultImpresoraCaja.value!!) { result ->
                // Este callback se ejecuta cuando el fragmento devuelve el resultado

                if (!result.codigo.isNullOrBlank()) {
                    // Resaltar la tarjeta seleccionada
                    var tamano : Int = 32
                    var printerT = LocalStore.getPrinterBluetooth(this)
                    if (printerT=="50"){tamano=32}
                    if (printerT=="55"){tamano=34}
                    if (printerT=="80"){tamano=40}

                    var printerPrecuentaW = LocalStore.getPrinterBluetoothPrecuentaWindow(this)
                    var activapre: Boolean = false
                    if (printerPrecuentaW=="1")
                        activapre = true
                    detalleViewModel.imprimePrecuenta(result.codigo,tamano,activapre )
                } else {
                    // Resaltar la tarjeta seleccionada
                    showToast("Seleccione una impresora")
                }
            }

            // Mostrar el BottomSheetDialogFragment
            fragment.show(supportFragmentManager, "ImpresoraCajaFragment")

            //detalleViewModel.imprimePrecuenta()

            //niubizViewModel.sendTestPrint(this)
        }

        binding.ivDeletePedido.setOnClickListener{

            ModalAnulacionItem_o_Pedido(false)

        }

        binding.ivAtrasDetallepedido.setOnClickListener{
            finishActivity("atras")
        }

        binding.llVercartaOrder.setOnClickListener{
            finishActivity("carta")
        }

        // Botón para abrir el BottomSheet
        binding.btnOpenBottomSheet.setOnClickListener {
            showFloatingLeftDialog()
        }

    }
    // Click  para eleccion  de Item pedido
    override fun onItemClickProducto(details: ProductoModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
            productoSeleccionado = details

        }
    }
    // Click  para eleccion  de Item pedido para ordenamiento
    override fun onItemClickOrden(details: ProductoModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
            productoSeleccionado = details

            mostrarNumPadDialog(this.supportFragmentManager,"orden"){ cantidad ->
                var enterocantidad = cantidad.toInt()
                if (enterocantidad<0){
                    showToast("El orden debe ser mayor a 0")
                    return@mostrarNumPadDialog
                }
                it.orden = enterocantidad
                detalleViewModel.generaPedidoDetalle(it)
            }
        }
    }
    override fun onItemClickElimina(details: ProductoModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
            productoSeleccionado = details
            if (!details.isimpreso){
                detalleViewModel.eliminaItemPedidoDetalle(details);
            }
            else {
                ModalAnulacionItem_o_Pedido (true)
            }

        }
    }
    override fun onItemClickEdita(details: ProductoModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
            if (details.isimpreso){

            }
            productoSeleccionado = details
            if (!isBottomSheetProductoModal){
                isBottomSheetProductoModal = true
                detalleViewModel.actualizarProducto(it)
                val intent = Intent(this, ProductoActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                pedidoLauncher.launch(intent, options)
            }
        }
    }
    private fun ModalAnulacionItem_o_Pedido (itemparametro: Boolean){
        // Create a LinearLayout to hold both the EditText fields
        val linearLayout = LinearLayout(this@DetallePedidoActivity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50)

        // Create the password EditText
        val passwordEditText = EditText(this@DetallePedidoActivity).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Ingrese su password"
            textAlignment =  View.TEXT_ALIGNMENT_CENTER
            isFocusable = false
            isFocusableInTouchMode=false
        }

        // Agregar el OnClickListener para el passwordEditText
        passwordEditText.setOnClickListener {
            mostrarNumPadDialog(this.supportFragmentManager,"login"){ cantidad ->
                var enterocantidad = cantidad.toInt()
                passwordEditText.setText(enterocantidad.toString())
            }
        }

        // Crear el título para el Spinner
        val spinnerTitle = TextView(this@DetallePedidoActivity).apply {
            text = "Seleccione un motivo:"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@DetallePedidoActivity, R.color.colorBlack)) // Cambia el color según tu preferencia
            setPadding(0, 20, 0, 10) // Agrega algo de separación
            textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }


        // Crear el Spinner y asignar un adaptador
        val spinner = Spinner(this@DetallePedidoActivity).apply {
            val motivos = detalleViewModel.resultMotivoEliminacionModel.value?.toList()
            adapter = ArrayAdapter(
                this@DetallePedidoActivity,
                android.R.layout.simple_spinner_item,
                motivos?.map { it.detalle } ?: listOf("No hay motivos disponibles") // Manejar lista vacía o nula
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        // Agregar márgenes al Spinner
        val spinnerLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 20, 0, 10) // Márgenes (izquierda, arriba, derecha, abajo)
        }
        spinner.layoutParams = spinnerLayoutParams


        // Add both EditTexts to the LinearLayout
        linearLayout.addView(passwordEditText)
        //linearLayout.addView(reasonEditText)
        linearLayout.addView(spinnerTitle) // Agregar el título del Spinner
        linearLayout.addView(spinner) // Add Spinner to the layout

        var titlemodal = if (!itemparametro){"¿Esta seguro de eliminar el pedido?"} else {"¿Esta seguro de eliminar el producto: " + productoSeleccionado?.nombre + "?"}
        // Build the AlertDialog
        AlertDialog.Builder(this@DetallePedidoActivity)
            .setTitle("INFOREST APP")
            .setMessage(titlemodal)
            .setView(linearLayout)  // Set the LinearLayout containing both EditTexts
            .setCancelable(false)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Dismiss dialog on cancel
            }
            .setPositiveButton("Confirmar") { dialog, _ ->
                val password = passwordEditText.text.toString().trim() // Get the entered password
                //val reason = reasonEditText.text.toString().trim() // Get the entered reason
                val selectedIndex = spinner.selectedItemPosition // Obtener la posición seleccionada
                val selectedMotivo = detalleViewModel.resultMotivoEliminacionModel.value?.toList()?.getOrNull(selectedIndex) // Obtener el motivo según la posición

                // Validar los campos requeridos
                if (password.isEmpty() || selectedMotivo == null) {
                    Toast.makeText(this@DetallePedidoActivity, "Password y motivo obligatorios!", Toast.LENGTH_SHORT).show()
                } else {
                    // Usar el "codigo" del motivo seleccionado
                    val codigoMotivo = selectedMotivo.codigo
                    if (codigoMotivo.isEmpty()){
                        Toast.makeText(this@DetallePedidoActivity, "Motivo obligatorio!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (itemparametro){
                        productoSeleccionado?.motivoanulacion = codigoMotivo
                        productoSeleccionado?.password = password
                        productoSeleccionado?.let { detalleViewModel.eliminaItemPedidoDetalle(it) };
                    }else {
                        detalleViewModel.eliminaPedido(password, codigoMotivo)
                    }
                    dialog.dismiss()
                }
            }
            .show()
    }

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isBottomSheetProductoModal = false
            detalleViewModel.onCreate()
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultValue = data?.getStringExtra(KEY_RESULT)
                // Verificar si el valor resultante es nulo o vacío, si es necesario
                if (resultValue =="facturado") {
                    finishActivity("facturado")
                }
                if (resultValue =="okdescuento") {
                    finishActivity("facturado")
                }
            }
        }

    private fun showFloatingLeftDialog() {
        val dialog = Dialog(this)
        val dialogBinding = BottomSheetDetallepedidoBinding.inflate(LayoutInflater.from(this))

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        val window = dialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Configuración del tamaño y posición
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)

        // Aumentar el tamaño del modal (ajustar según necesidad)
        layoutParams.width = (resources.displayMetrics.widthPixels * 0.6).toInt()  // 60% del ancho de la pantalla
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT  // Ajuste automático de altura

        // Posicionar en el lado derecho e inferior con margen
        layoutParams.gravity = Gravity.END or Gravity.BOTTOM

        // Agregar margen (convertir dp a píxeles)
        val marginDp = 10  // Margen en dp
        val scale = resources.displayMetrics.density
        val marginPx = (marginDp * scale).toInt()

        layoutParams.x = marginPx  // Separación desde la derecha
        layoutParams.y = marginPx  // Separación desde abajo

        window?.attributes = layoutParams

        // Acciones de los botones
        dialogBinding.llTransferirOrder.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, TransferirActivity::class.java)
            intent.putExtra("tipo", "transferencia") // Aquí pasas el valor

            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
            pedidoLauncher.launch(intent, options)
        }
        dialogBinding.llDescuentoOrder.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, TransferirActivity::class.java)
            intent.putExtra("tipo", "descuento") // Aquí pasas el valor

            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
            pedidoLauncher.launch(intent, options)
        }
        dialogBinding.llDividirOrder.setOnClickListener {
            dialog.dismiss()
            //startActivity(DivisionActivity::class.java)

            val intent = Intent(this, DivisionActivity::class.java)
            intent.putExtra("tipo", "division") // Aquí pasas el valor
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
            pedidoLauncher.launch(intent, options)
        }
        dialogBinding.llcerrarSesion.setOnClickListener {
            finishActivity("cerrarsesion")
        }
        dialogBinding.llmensajemesero.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, MensajeMeseroActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
            pedidoLauncher.launch(intent, options)
        }

        dialog.show()
    }


}