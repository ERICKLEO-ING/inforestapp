package com.infomatica.inforestapp.ui.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.infomatica.inforestapp.GlobalVariables
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.GrupoModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.SubGrupoModel
import com.infomatica.inforestapp.data.model.ToppingModel
import com.infomatica.inforestapp.databinding.ActivityPedidoBinding
import com.infomatica.inforestapp.databinding.BottomSheetDetallepedidoBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.ScreenCategory
import com.infomatica.inforestapp.ui.adapter.CanalVentaAdapter
import com.infomatica.inforestapp.ui.adapter.DetallePedidoAdapter
import com.infomatica.inforestapp.ui.adapter.GrupoAdapter
import com.infomatica.inforestapp.ui.adapter.ModificadorAdapter
import com.infomatica.inforestapp.ui.adapter.NombreClienteAdapter
import com.infomatica.inforestapp.ui.adapter.ProductoAdapter
import com.infomatica.inforestapp.ui.adapter.SubGrupoAdapter
import com.infomatica.inforestapp.ui.fragment.ImpresoraCajaFragment
import com.infomatica.inforestapp.ui.viewmodel.PedidoViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PedidoActivity : BaseActivity(),
    GrupoAdapter.ItemClickListener,
    ProductoAdapter.ItemClickListener,
    SubGrupoAdapter.ItemClickListener,
    NombreClienteAdapter.ItemClickListener,
    DetallePedidoAdapter.ItemClickListener{

    private lateinit var binding: ActivityPedidoBinding
    private val pedidoViewModel: PedidoViewModel by viewModels()
    private var productoSeleccionado: ProductoModel? = null
    private var subgrupoSeleccionado: SubGrupoModel? = null
    private lateinit var modificadorAdapter: ModificadorAdapter
    private lateinit var nombreClienteAdapter: NombreClienteAdapter

    private var isBottomSheetProductoModal= false
    private var isBottomSheetPedidoOpen = false

    private var isAbrirPedidodetalleOpen = false

    private var isModificaPedido = false

    private var isImpresionPrecuenta = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pedidoViewModel.onCreate()
        configureRecyclerViews()
        setupObservers()
        setupBottomNavigation()
        setupBottomNavigationtablet()
        binding.ivNumeroorden.setText("${GlobalVariables.ordenGeneral}°")
        // Configurar listener para el EditText
        binding.etBuscarProducto.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Aquí se ejecuta la acción al presionar Enter
                val textoIngresado = binding.etBuscarProducto.text.toString()
                pedidoViewModel.fetchListaProductos("01","|${textoIngresado}") // Llamar al método en el ViewModel
                binding.rvGrupos.isVisible = true
                binding.rvSubgrupos.isVisible = true

                // Habilitar el campo de búsqueda del salón
                binding.etBuscarProducto.isVisible = false
                binding.etBuscarProducto.setText("")
                // Ocultar el teclado
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etBuscarProducto.windowToken, 0)

                true // Indicar que manejaste el evento
            } else {
                false // Permitir que otros listeners manejen el evento
            }
        }

        binding.ivForo.setOnClickListener{

            val intent = Intent(this, ForoActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
            pedidoLauncher.launch(intent, options)
        }
        binding.ivNumeroorden.setOnClickListener{
            mostrarNumPadDialog(this.supportFragmentManager,"orden"){ cantidad ->
                var enterocantidad = cantidad.toInt()
                if (enterocantidad<0){
                    showToast("El orden debe ser mayor a 0")
                    return@mostrarNumPadDialog
                }
               GlobalVariables.ordenGeneral = enterocantidad
                binding.ivNumeroorden.setText("${GlobalVariables.ordenGeneral}°")
            }
        }
        binding.ivDeletePedido.setOnClickListener{
            ModalAnulacionItem_o_Pedido(false)
        }
        binding.llEnviarOrder.setOnClickListener {

            val texto = binding.tvEnviarOrder.text.toString()
            if (texto == "ENVIAR") {
                isImpresionPrecuenta =  true
                pedidoViewModel.imprimeComanda()
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

                pedidoViewModel.imprimePrecuenta("000",tamano,false )
                return@setOnClickListener
            }
            val fragment = ImpresoraCajaFragment.newInstance(pedidoViewModel.resultImpresoraCaja.value!!) { result ->
                // Este callback se ejecuta cuando el fragmento devuelve el resultado

                if (!result.codigo.isNullOrBlank()) {
                    // Resaltar la tarjeta seleccionada
                    isImpresionPrecuenta = true

                    var tamano : Int = 32
                    var printerT = LocalStore.getPrinterBluetooth(this)
                    if (printerT=="50"){tamano=32}
                    if (printerT=="55"){tamano=34}
                    if (printerT=="80"){tamano=40}

                    pedidoViewModel.imprimePrecuenta(result.codigo,tamano,true)
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
        // Oculta o muestra según categoría de pantalla
        when (getScreenCategory()) {
            ScreenCategory.SMALL -> {
                // <600dp → teléfono
                binding.bottomNavigation.isVisible = true
                binding.drawerLayout.isVisible = false
//                binding.ivNumeroorden.isVisible=true
                binding.ivForo.isVisible = true
            }
            else -> {
                // ≥600dp → tablet o más grande
                binding.bottomNavigation.isVisible = false
                binding.drawerLayout.isVisible = true
//                binding.ivNumeroorden.isVisible=true
                binding.ivForo.isVisible = false
            }
        }
        binding.ivBuscarProducto.setOnClickListener {
            manejarVistaBusquedaProducto()
        }

    }

    private fun configureRecyclerViews() {
        binding.rvGrupos.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        binding.rvSubgrupos.layoutManager = GridLayoutManager(this, if (isTablet()) 1 else 2, RecyclerView.HORIZONTAL, false)
        binding.rvProductos.layoutManager = GridLayoutManager(this, if (isTablet()) 4 else 2, RecyclerView.VERTICAL, false)
        binding.rvDetallpedido.setLayoutManager(
            GridLayoutManager(
                applicationContext,
                1,
                RecyclerView.VERTICAL,
                false
            )
        )
    }

    private fun setupObservers() {
        pedidoViewModel.listaGrupo.observe(this, Observer { groups ->
            if (groups != null) {
                if (groups.isNotEmpty()){groups.orEmpty()[0].seleccionado = true}
            }
            val grupoAdapter = GrupoAdapter(groups.orEmpty(), this)
            binding.rvGrupos.adapter = grupoAdapter

            groups.orEmpty()[0].subgrupos[0].seleccionado = true
            val subgrupoAdapter = SubGrupoAdapter(groups.orEmpty()[0].subgrupos.toMutableList(), this)
            binding.rvSubgrupos.adapter = subgrupoAdapter

            subgrupoSeleccionado = groups.orEmpty()[0].subgrupos.orEmpty()[0]
        })

        pedidoViewModel.listaProductos.observe(this, Observer { products ->
            val productoAdapter = ProductoAdapter(products.orEmpty(), this)
            binding.rvProductos.adapter = productoAdapter
        })

        pedidoViewModel.isLoading.observe(this, Observer { isLoading ->
            binding.pgCargando.isVisible = isLoading
        })

        pedidoViewModel.resultModificadorModel.observe(this, Observer { modifiers ->
            productoSeleccionado?.let { modificadorAdapter.submitList(modifiers.orEmpty(), it) }
        })

        pedidoViewModel.resultPedidoModel.observe(this, Observer { result ->
            handlePedidoResult(result)
        })

        pedidoViewModel.resultEliminaPedidoModel.observe(this, Observer { currentCute ->
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

                    AlertDialog.Builder(this@PedidoActivity)
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

        pedidoViewModel.resultImpresionPedidoModel.observe(this, Observer{currentCute ->
            when (currentCute?.status) {
                0 -> {
                    if (currentCute.impresionprecuenta.isNotEmpty()) {

                        //niubizViewModel.sendPrintPrecuenta(this, currentCute.impresionprecuenta)

                        var printerPrecuenta = LocalStore.getPrinterBluetoothPrecuenta(this)
                        if (printerPrecuenta=="1"){
                            imprimirEnPOS(currentCute.impresionprecuenta, "",this)
                        }
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

                    AlertDialog.Builder(this@PedidoActivity)
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

    private fun setupBottomNavigation() {


        binding.bottomNavigation.menu.setGroupCheckable(0, false, true)
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cerrarsesionpedido -> {
                    finishActivity("cerrarsesion")
                    false
                }
                R.id.nav_atras -> showExitConfirmationDialog()
                R.id.nav_carrito -> navigateToDetallePedido()
                R.id.nav_buscar_producto -> manejarVistaBusquedaProducto()
                R.id.nav_menupedido -> showFloatingLeftDialog()
                else -> false
            }
        }
    }
    private fun setupBottomNavigationtablet() {


        binding.bottomNavigationDetalle.menu.setGroupCheckable(0, false, true)
        binding.bottomNavigationDetalle.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navatrastablet -> showExitConfirmationDialog()
                R.id.navforotablet -> {
                    // Procesar el resultado si es válido
                    val intent = Intent(this, ForoActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                    pedidoLauncher.launch(intent, options)
                    return@setOnItemSelectedListener false
                }
                R.id.navsinmesatablet -> navegacionActualizaPedido()
                R.id.navopcionestablet -> showFloatingLeftDialog()
                else -> false
            }
        }
    }
    private fun showExitConfirmationDialog(): Boolean {
        finishActivity("0")
        return false
    }
    private fun showFloatingLeftDialog():Boolean {
        val dialog = Dialog(this)
        val dialogBinding = BottomSheetDetallepedidoBinding.inflate(LayoutInflater.from(this))

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        val window = dialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Configuración del tamaño y posición
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        var fraction_opciopnes : Double = 0.6
        when (getScreenCategory()) {
            ScreenCategory.SMALL -> {
                // <600dp → teléfono
                fraction_opciopnes = 0.6
            }
            else -> {
                // ≥600dp → tablet o más grande
                fraction_opciopnes = 0.3
            }
        }

        // Aumentar el tamaño del modal (ajustar según necesidad)
        layoutParams.width = (resources.displayMetrics.widthPixels *fraction_opciopnes).toInt()  // 60% del ancho de la pantalla
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
        return true
    }
    private fun navigateToDetallePedido(): Boolean {

        if (!isAbrirPedidodetalleOpen){
            isAbrirPedidodetalleOpen = true
            val intent = Intent(this, DetallePedidoActivity::class.java)
            intent.putExtra("Key_Carta", "Si");
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
            pedidoLauncher.launch(intent, options)
            //pedidoLauncher.launch(intent)
        }
        return false
    }

//    private fun finishActivity(value: String) {
//        setResult(Activity.RESULT_OK, Intent().putExtra("key", value))
//        finish()
//    }

    private fun handlePedidoResult(result: PedidoModel?) {
        when (result?.status) {
            0 -> showSuccessMessage()
            1 -> showFailureMessage()
            99 -> showErrorMessage(result.mensaje)
            //101 -> navigateToDetallePedido()
        }

        if (result?.detalle?.isNotEmpty() == true){
            binding.bottomNavigation.menu.getItem(2).icon = ContextCompat.getDrawable(this, R.drawable.bolsa_activo_30x30)
        } else {
            binding.bottomNavigation.menu.getItem(2).icon = ContextCompat.getDrawable(this, R.drawable.bolsa_pasivo_30x30)
        }
        result?.detalle?.let { detalle ->
            val detallePedidotoAdapter = DetallePedidoAdapter(detalle, this)
            binding.rvDetallpedido.adapter = detallePedidotoAdapter
        }
        binding.tvCodigomesa.setText(result?.nombremesa)
        binding.tvCodigopedido.setText("Pedido ${result?.codigopedido}")
        binding.tvOrderSubtotal.text = "Subtotal: ${result?.monedapedido} ${ String.format("%.2f", result?.detalle?.sumOf { it.itemventa })}"

        result?.detalle?.filter { x -> !x.isimpreso }.let {

            if (it != null) {
                if (it.isNotEmpty() || !pedidoViewModel.resultUsuarioModel.value!!.activacajero){
                    binding.tvEnviarOrder.text = "ENVIAR"
                }else {
                    binding.tvEnviarOrder.text = "REGISTRAR COBRO"
                }
            }
        }

        if ( result?.detalle!!.isEmpty()){
            binding.tvEnviarOrder.text = "ENVIAR"
            binding.tvEnviarOrder.isEnabled = false
        }
        val descuento =  result?.detalle!!.sumOf { it.descuento }

        if (descuento>0){
            binding.tvOrderDescuento.isVisible = true
            binding.tvOrderDescuento.text = "descuento: ${result?.monedapedido} ${String.format("%.2f", descuento)}"
        }
    }
    // Click  para eleccion  de Item pedido
    override fun onItemClickProducto(details: ProductoModel?) {
        // Handle the item click event here
        details?.let {
            productoSeleccionado = details
        }
    }
    // Click  para eleccion  de Item pedido para ordenamiento
    override fun onItemClickOrden(details: ProductoModel?) {
        // Handle the item click event here
        details?.let {
            productoSeleccionado = details
            isModificaPedido =true
            mostrarNumPadDialog(this.supportFragmentManager,"orden"){ cantidad ->
                var enterocantidad = cantidad.toInt()
                if (enterocantidad<0){
                    showToast("El orden debe ser mayor a 0")
                    return@mostrarNumPadDialog
                }
                it.orden = enterocantidad
                pedidoViewModel.generaPedidoDetalle(it)
            }
        }
    }
    override fun onItemClickElimina(details: ProductoModel?) {
        // Handle the item click event here
        details?.let {
            productoSeleccionado = details
            isModificaPedido =true
            if (!details.isimpreso){
                pedidoViewModel.eliminaItemPedidoDetalle(details);
            }
            else {
                ModalAnulacionItem_o_Pedido (true)
            }

        }
    }
    override fun onItemClickEdita(details: ProductoModel?) {
        // Handle the item click event here
        details?.let {
            if (details.isimpreso){

            }
            isModificaPedido =true
            productoSeleccionado = details
            if (!isBottomSheetProductoModal){
                isBottomSheetProductoModal = true
                pedidoViewModel.actualizarProducto(it)
                val intent = Intent(this, ProductoActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                pedidoLauncher.launch(intent, options)
            }
        }
    }
    private fun showSuccessMessage() {
        if (isImpresionPrecuenta){
            showToast(">Impresión enviada <" )
        } else if (isModificaPedido){
            showToast("> Pedido Modificado <" )
        }else
        {
            showToast("> ${productoSeleccionado?.nombre} - Agregado <" )
        }

        isModificaPedido =false
        isImpresionPrecuenta = false

        //navigateToDetallePedido()
    }

    private fun showFailureMessage() {
        showToast( "Operación fallida")
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this@PedidoActivity)
            .setTitle("INFOREST APP")
            .setMessage(message)
            .setNegativeButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onItemClick(details: GrupoModel?) {
        details?.let {
            val subgrupoAdapter = SubGrupoAdapter(it.subgrupos.toMutableList(), this)
            binding.rvSubgrupos.adapter = subgrupoAdapter
            if (it.subgrupos.isNotEmpty()) {
                it.subgrupos.forEach { it.seleccionado = false }
                it.subgrupos[0].seleccionado = true
                getProductos(it.subgrupos[0].codigo)
                subgrupoSeleccionado = it.subgrupos[0]
            }
        }
    }
    override fun onItemClick(details: String?) {
        details?.let {

        }
    }

    override fun onItemClick(details: ProductoModel?) {
        details?.let {
            productoSeleccionado = it
            if (!isBottomSheetProductoModal) pedidoViewModel.actualizarProducto(it)
            val intent = Intent(this, ProductoActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
            pedidoLauncher.launch(intent, options)
//            if (it.lpropiedad > 0) {
//                //if (!isBottomSheetProductoModal) showSheetProductoDialog(it)
//                if (!isBottomSheetProductoModal) pedidoViewModel.actualizarProducto(it)
//                val intent = Intent(this, ProductoActivity::class.java)
//                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
//                pedidoLauncher.launch(intent, options)
//
//            } else {
//                pedidoViewModel.generaPedidoDetalle(it)
//            }
        }
    }

    override fun onItemClick(details: SubGrupoModel?) {
        details?.let {
            getProductos(it.codigo)
            subgrupoSeleccionado = it
        }
    }

    private fun getProductos(codigosubgrupo: String) {
        pedidoViewModel.fetchListaProductos(pedidoViewModel.resultPedidoModel.value!!.canalventa, codigosubgrupo)
        pedidoViewModel.listaProductos.observe(this) { products ->
            val productoAdapter = ProductoAdapter(products.orEmpty(), this)
            binding.rvProductos.adapter = productoAdapter
        }
    }
    private fun manejarVistaBusquedaProducto(): Boolean {
        // Desactivar la visibilidad de los elementos relacionados a la selección de salones
        binding.rvGrupos.isVisible = false
        binding.rvSubgrupos.isVisible = false

        // Habilitar el campo de búsqueda del salón
        binding.etBuscarProducto.isVisible = true

        // Solicitar foco para el campo de búsqueda
        binding.etBuscarProducto.requestFocus()

        // Obtener el InputMethodManager para mostrar el teclado
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Mostrar el teclado si el campo tiene el foco
        inputMethodManager.showSoftInput(binding.etBuscarProducto, InputMethodManager.SHOW_IMPLICIT)

        return false;
    }
    private fun navegacionActualizaPedido(): Boolean {
        pedidoViewModel.resultPedidoModel.value?.let { showBottomSheetModificaPedidoDialog(it) }
        return false;
    }

    private fun showBottomSheetModificaPedidoDialog(pedidoModel: PedidoModel) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_nuevopedido, null)
        bottomSheetDialog.setContentView(view)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val behavior = BottomSheetBehavior.from(view.parent as View)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        isBottomSheetPedidoOpen = true

        configurarBottomSheetModificaPedido(view, pedidoModel, bottomSheetDialog)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }
    private fun configurarBottomSheetModificaPedido(view: View, pedidoModel: PedidoModel, bottomSheetDialog: BottomSheetDialog) {
        var clientCount = pedidoModel.pax

        nombreClienteAdapter = NombreClienteAdapter(pedidoModel.clientes, this)
        view.findViewById<RecyclerView>(R.id.rv_clientes).apply {
            layoutManager = GridLayoutManager(this@PedidoActivity, 1, RecyclerView.VERTICAL, false)
            adapter = nombreClienteAdapter
        }
        view.findViewById<TextView>(R.id.tv_NombreMesa).text = pedidoModel.nombremesa
        view.findViewById<TextView>(R.id.tv_paxclient).text = clientCount.toString()
        view.findViewById<TextView>(R.id.tv_observacion).text = pedidoModel.observacion

        pedidoViewModel.resultCanalVentaModel.value?.let { lista ->
            val adapter = CanalVentaAdapter(this, lista)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val snniperCanal = view.findViewById<Spinner>(R.id.spinnerCanalVenta)
            snniperCanal.adapter = adapter
            snniperCanal.isEnabled = false

            // Buscar la posición del elemento con código "03"
            val posicion = lista.indexOfFirst { it?.Codigo  == pedidoViewModel.resultPedidoModel.value!!.canalventa }

            // Si existe en la lista, seleccionarlo
            if (posicion != -1) {
                snniperCanal.setSelection(posicion)
            }
        }


        view.findViewById<ImageView>(R.id.btnIncrement).setOnClickListener {
            clientCount++
            view.findViewById<TextView>(R.id.tv_paxclient).text = clientCount.toString()
            if (pedidoModel.clientes.count()<clientCount){
                pedidoModel.clientes.add("Cliente")
            }
            actualizarClienteAdapter(view, pedidoModel.clientes,clientCount)
        }
        view.findViewById<ImageView>(R.id.btnDecrement).setOnClickListener {
            if (clientCount > 0) {
                clientCount--
                view.findViewById<TextView>(R.id.tv_paxclient).text = clientCount.toString()
                if (pedidoModel.clientes.count()>clientCount){
                    pedidoModel.clientes.remove("Cliente")
                    pedidoModel.clientes.remove("")
                }
                actualizarClienteAdapter(view, pedidoModel.clientes,clientCount)
            }
        }

        view.findViewById<TextView>(R.id.tv_cancelar).setOnClickListener {
            bottomSheetDialog.dismiss()
            isBottomSheetPedidoOpen = false
        }
        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            isModificaPedido = true
            ModificaPedido(view, pedidoModel, clientCount, bottomSheetDialog)
        }
    }
    private fun actualizarClienteAdapter(view: View, clientCount: MutableList<String>, count: Int) {
        nombreClienteAdapter = NombreClienteAdapter(clientCount, this)
        view.findViewById<RecyclerView>(R.id.rv_clientes).adapter = nombreClienteAdapter
        view.findViewById<TextView>(R.id.tv_paxclient).text = count.toString()
    }
    private fun ModificaPedido(view: View, pedidoModel: PedidoModel, clientCount: Int, bottomSheetDialog: BottomSheetDialog) {
        val observacion = view.findViewById<TextView>(R.id.tv_observacion).text.toString().trim()
        if (pedidoModel.codigomesa == "000" && observacion.isEmpty()) {
            showToast("Observación Obligatorio")
            view.findViewById<TextView>(R.id.tv_observacion).error = "Observación Obligatorio"
        } else {
//            val adapter = nombreClienteAdapter
//            val clientesNombres = mutableListOf<String>() // Inicializar como lista mutable
//            adapter?.let {
//                for (nombre in it.listaNombres) {
//                    //showToast("Cliente: $nombre")
//                    if (nombre =="Cliente")
//                        continue
//                    clientesNombres.add(nombre) // Agregar nombre a la lista mutable
//                }
//            }
            pedidoModel.observacion = observacion
            pedidoModel.pax = clientCount
            pedidoViewModel.modificaPedido(pedidoModel)

            //showToast("pedido modificado")
            bottomSheetDialog.dismiss()
            isBottomSheetPedidoOpen = false
        }
    }
    private fun ModalAnulacionItem_o_Pedido (itemparametro: Boolean){
        // Create a LinearLayout to hold both the EditText fields
        val linearLayout = LinearLayout(this@PedidoActivity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50)

        // Create the password EditText
        val passwordEditText = EditText(this@PedidoActivity).apply {
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
        val spinnerTitle = TextView(this@PedidoActivity).apply {
            text = "Seleccione un motivo:"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@PedidoActivity, R.color.colorBlack)) // Cambia el color según tu preferencia
            setPadding(0, 20, 0, 10) // Agrega algo de separación
            textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }


        // Crear el Spinner y asignar un adaptador
        val spinner = Spinner(this@PedidoActivity).apply {
            val motivos = pedidoViewModel.resultMotivoEliminacionModel.value?.toList()
            adapter = ArrayAdapter(
                this@PedidoActivity,
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
        AlertDialog.Builder(this@PedidoActivity)
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
                val selectedMotivo = pedidoViewModel.resultMotivoEliminacionModel.value?.toList()?.getOrNull(selectedIndex) // Obtener el motivo según la posición

                // Validar los campos requeridos
                if (password.isEmpty() || selectedMotivo == null) {
                    Toast.makeText(this@PedidoActivity, "Password y motivo obligatorios!", Toast.LENGTH_SHORT).show()
                } else {
                    // Usar el "codigo" del motivo seleccionado
                    val codigoMotivo = selectedMotivo.codigo
                    if (codigoMotivo.isEmpty()){
                        Toast.makeText(this@PedidoActivity, "Motivo obligatorio!", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (itemparametro){
                        productoSeleccionado?.motivoanulacion = codigoMotivo
                        productoSeleccionado?.password = password
                        productoSeleccionado?.let { pedidoViewModel.eliminaItemPedidoDetalle(it) };
                    }else {
                        pedidoViewModel.eliminaPedido(password, codigoMotivo)
                    }
                    dialog.dismiss()
                }
            }
            .show()
    }

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val keystring = result.data?.getStringExtra("key")
                if (keystring=="elimina" || keystring=="facturado"){
                    finishActivity("1")
                    return@registerForActivityResult
                }
                if (keystring=="cerrarsesion"){
                    finishActivity("cerrarsesion")
                    return@registerForActivityResult
                }
            }
            isAbrirPedidodetalleOpen = false
            isBottomSheetProductoModal = false
            pedidoViewModel.fetchPedidoActual()
            subgrupoSeleccionado?.let { getProductos(it.codigo) }
        }

}