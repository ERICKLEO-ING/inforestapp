package com.infomatica.inforestapp.ui.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.CanalVentaModel
import com.infomatica.inforestapp.data.model.MesasModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.databinding.ActivityVentaBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.ScreenCategory
import com.infomatica.inforestapp.ui.adapter.CanalVentaAdapter
import com.infomatica.inforestapp.ui.adapter.MesaAdapter
import com.infomatica.inforestapp.ui.adapter.NombreClienteAdapter
import com.infomatica.inforestapp.ui.adapter.SalonAdapter
import com.infomatica.inforestapp.ui.viewmodel.VentaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VentaActivity : BaseActivity(),
    MesaAdapter.ItemClickListener,
    NombreClienteAdapter.ItemClickListener {

    private lateinit var binding: ActivityVentaBinding
    private val ventaViewModel: VentaViewModel by viewModels()
    private var usuarioModel: UsuarioModel? = null
    private var mesaSeleccionada: MesasModel? = null
    private var isBottomSheetMesaOpen = false
    private var isBottomSheetMenuOpen = false

    private lateinit var salonAdapter: SalonAdapter
    private lateinit var mesaAdapter: MesaAdapter
    private var clientesList = mutableListOf<String>()
    private lateinit var nombreClienteAdapter: NombreClienteAdapter

    lateinit var rootLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarViewModelObservers()
        configurarAdapters()
        configurarBottomNavigation()
        configurarOnBackPressedCerrarsesion()
        ventaViewModel.onCreate()

        binding.bottomNavigation.visibility = View.VISIBLE
        // Configurar listener para el EditText
        binding.etBuscarSalon.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Aquí se ejecuta la acción al presionar Enter
                val textoIngresado = binding.etBuscarSalon.text.toString()
                ventaViewModel.buscarMesa(textoIngresado) // Llamar al método en el ViewModel
                binding.tvDisponibles.isEnabled = true
                binding.tvDisponibles.text = "Disponibles : ${textoIngresado}"

                clientesList.add("Cliente")

                // Habilitar el campo de búsqueda del salón
                binding.etBuscarSalon.isVisible = false
                binding.etBuscarSalon.setText("")
                // Ocultar el teclado
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etBuscarSalon.windowToken, 0)

                true // Indicar que manejaste el evento
            } else {

                false // Permitir que otros listeners manejen el evento
            }
        }

        rootLayout = findViewById(android.R.id.content) // La vista raíz

        // Detectar cuando el teclado se oculta o minimiza
        rootLayout.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (isKeyboardHidden()) {
                if (binding.tvDisponibles.text == "Buscar Mesa"){
                    //binding.tvDisponibles.text = ""
                }else{
                    binding.tvDisponibles.isEnabled = true

                    // Habilitar el campo de búsqueda del salón
                    binding.etBuscarSalon.isVisible = false
                    binding.etBuscarSalon.setText("")
                }
            } else {
                binding.tvDisponibles.text = "."
            }
        }
        binding.ivForo.setOnClickListener{
            // Procesar el resultado si es válido
            val intent = Intent(this, ForoActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
            pedidoCartaLauncher.launch(intent,options)
        }
    }
    // Verifica si el teclado está oculto
    private fun isKeyboardHidden(): Boolean {
        val rect = Rect()
        rootLayout.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootLayout.height
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight <= screenHeight * 0.15 // Si el teclado ocupa menos del 15% de la pantalla, lo consideramos oculto
    }
    private fun configurarViewModelObservers() {
        ventaViewModel.apply {
            listaSalon.observe(this@VentaActivity, Observer { salonAdapter.submitList(it.orEmpty()) })
            listMesa.observe(this@VentaActivity, Observer { mesaAdapter.submitList(it.orEmpty()) })
            isLoading.observe(this@VentaActivity, Observer { binding.pgCargando.isVisible = it })
            disponiblesTexto.observe(this@VentaActivity, Observer { binding.tvDisponibles.text = it })
            usuarioActual.observe(this@VentaActivity, Observer {
                if (it != null) {
                    if (it.dispositivo != obtenerUUID(this@VentaActivity)){
                        val titulo = "Error de inicio de sesión"
                        val descripcion="Usuario ha iniciado sesion en otro dispositivo."//¿Desea continuar en este equipo y cerrar las otras sesiones?"
                        mensajeDialog(this@VentaActivity.supportFragmentManager,titulo,descripcion){ respuesta,respuesta2 ->
//                            if (respuesta=="ok"){
//                                usuarioModel = it
//                                ventaViewModel.cerrarSesion()
//
//                            }else {
//                                finishActivity("error")
//                            }
                            finishActivity("error")
                        }
                    } else {
                        usuarioModel = it
                    }
                }
            })
            resultPedidoModel.observe(this@VentaActivity, Observer { handlePedidoResult(it) })
        }
    }

    private fun configurarAdapters() {
        binding.rvSalones.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        salonAdapter = SalonAdapter { selectedSalon ->
            selectedSalon?.let {
                ventaViewModel.salonSeleccionado.postValue(it)
                ventaViewModel.listMesa.postValue(it.mesas)
                ventaViewModel.disponiblesTexto.postValue("Disponibles: ${it.mesas.count { mesa -> mesa.estado == "LISTA" || mesa.estado == "SUCIA" }}/${it.mesas.size} ${it.nombre}")
            }
        }
        binding.rvSalones.adapter = salonAdapter

        // Determinar spanCount según categoría de pantalla
        val spanCount = when (getScreenCategory()) {
            ScreenCategory.SMALL  -> 3   // < 600dp
            ScreenCategory.SW600  -> 6   // ≥ 600dp && < 720dp
            ScreenCategory.SW720  -> 8   // ≥ 720dp && < 960dp
            ScreenCategory.SW960  -> 10   // ≥ 960dp
        }
        binding.rvMesas.layoutManager = GridLayoutManager(this, spanCount, RecyclerView.VERTICAL, false)
        mesaAdapter = MesaAdapter(this)
        binding.rvMesas.adapter = mesaAdapter
    }

    private fun configurarBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cerrarsesion -> {
                    ventaViewModel.cerrarSesion()
                    finish()
                    false
                }

                R.id.nav_actualizar -> {
                    ventaViewModel.onCreate()
                    false
                }
                R.id.nav_buscar -> {
                    manejarVistaBusquedaSalon()

                    false
                }
                R.id.nav_sinmesa -> {
                    if (!isBottomSheetMesaOpen) showBottomSheetMesaDialog(MesasModel("000", "Sin Mesa", "Sin Mesa", 1, false, "01", "LISTA", "", "", ""))
                    false
                }
                R.id.nav_opciones -> {
                    if (!isBottomSheetMenuOpen) showBottomSheetMenuDialog()
                    false
                }
                else -> false
            }
        }
    }
    private fun manejarVistaBusquedaSalon() {
        // Desactivar la visibilidad de los elementos relacionados a la selección de salones
        binding.tvDisponibles.isEnabled = false
        binding.tvDisponibles.text = "Buscar Mesa"

        // Habilitar el campo de búsqueda del salón
        binding.etBuscarSalon.isVisible = true

        // Solicitar foco para el campo de búsqueda
        binding.etBuscarSalon.requestFocus()

        // Obtener el InputMethodManager para mostrar el teclado
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Mostrar el teclado si el campo tiene el foco
        inputMethodManager.showSoftInput(binding.etBuscarSalon, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun configurarOnBackPressedCerrarsesion() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@VentaActivity)
                    .setMessage("¿Estás seguro de cerrar sesion?")
                    .setPositiveButton("Sí") { _, _ ->
                        ventaViewModel.cerrarSesion()
                        finish()
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        })
    }

    override fun onItemClick(details: MesasModel?) {
        details?.let {
            mesaSeleccionada = it
            if(!isBottomSheetMesaOpen)ventaViewModel.consultaPedido(it.codigo)
        }
    }

    override fun onItemClick(details: String?) {
        // Implementación vacía para el manejo de clientes si es necesario
    }

    private fun showBottomSheetMesaDialog(mesasModel: MesasModel) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_nuevopedido, null)
        bottomSheetDialog.setContentView(view)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val behavior = BottomSheetBehavior.from(view.parent as View)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        isBottomSheetMesaOpen = true

        configurarBottomSheetMesa(view, mesasModel, bottomSheetDialog)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = it.height // Evita que se corte
            }
        }
    }
    private fun configurarBottomSheetMesa(view: View, mesasModel: MesasModel, bottomSheetDialog: BottomSheetDialog) {
        var clientCount = 1//mesasModel.pax
        clientesList = mutableListOf()
        clientesList.add("Cliente")
        nombreClienteAdapter = NombreClienteAdapter(MutableList(clientCount) { "Cliente" }, this)
        view.findViewById<RecyclerView>(R.id.rv_clientes).apply {
            layoutManager = GridLayoutManager(this@VentaActivity, 1, RecyclerView.VERTICAL, false)
            adapter = nombreClienteAdapter
        }
        view.findViewById<TextView>(R.id.tv_NombreMesa).text = mesasModel.nombre
        //view.findViewById<TextView>(R.id.tv_paxclient).text = clientCount.toString()
        ventaViewModel.resultCanalVentaModel.value?.let {
            var lista = it
            if (mesasModel.codigo!="000"){
                lista = it.filter { x -> x?.lObligaMesa==true }
            }
            val adapter =CanalVentaAdapter(this,lista )
            adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            var snniperCanal = view.findViewById<Spinner>(R.id.spinnerCanalVenta)
            snniperCanal.adapter = adapter
            snniperCanal.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    //val selectedTipo = tiposIdentidad[position]
                    // Aquí puedes usar el objeto completo 'selectedTipo'
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Manejar caso cuando no hay nada seleccionado si es necesario
                }
            })
        }



        view.findViewById<ImageView>(R.id.btnIncrement).setOnClickListener {
            clientCount++
            if (clientesList.count()<clientCount){
                clientesList.add("Cliente")
            }
            actualizarClienteAdapter(view, clientesList,clientCount)
        }
        view.findViewById<ImageView>(R.id.btnDecrement).setOnClickListener {
            if (clientCount > 0) {
                clientCount--
                view.findViewById<TextView>(R.id.tv_paxclient).text = clientCount.toString()
                if (clientesList.count()>clientCount){
                    clientesList.remove("Cliente")
                    clientesList.remove("")
                }
                actualizarClienteAdapter(view, clientesList,clientCount)
            }
        }
        view.findViewById<TextView>(R.id.tv_cancelar).setOnClickListener {
            bottomSheetDialog.dismiss()
            isBottomSheetMesaOpen = false
        }
        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            guardarPedido(view, mesasModel, clientCount, bottomSheetDialog)
        }

    }

//    private fun actualizarClienteAdapter(view: View, clientCount: Int) {
//        nombreClienteAdapter = NombreClienteAdapter(MutableList(clientCount) { "Cliente" }, this)
//        view.findViewById<RecyclerView>(R.id.rv_clientes).adapter = nombreClienteAdapter
//        view.findViewById<TextView>(R.id.tv_paxclient).text = clientCount.toString()
//    }
    private fun actualizarClienteAdapter(view: View, clientCount: MutableList<String>, count: Int) {
        nombreClienteAdapter = NombreClienteAdapter(clientCount, this)
        view.findViewById<RecyclerView>(R.id.rv_clientes).adapter = nombreClienteAdapter
        view.findViewById<TextView>(R.id.tv_paxclient).text = count.toString()

        val emptyPos = nombreClienteAdapter.getFirstEmptyPosition()
        if (emptyPos != -1) {
            view.findViewById<RecyclerView>(R.id.rv_clientes).scrollToPosition(emptyPos)
        }
    }
    private fun guardarPedido(view: View, mesasModel: MesasModel, clientCount: Int, bottomSheetDialog: BottomSheetDialog) {
        val observacion = view.findViewById<TextView>(R.id.tv_observacion).text.toString().trim()

        if (ventaViewModel.usuarioActual.value!!.caja == "") {
            showToast("Caja no configurada para Usuario!!.")
            return;
        }

        if (mesasModel.codigo == "000" && observacion.isEmpty()) {
            showToast("Observación Obligatorio")
            view.findViewById<TextView>(R.id.tv_observacion).error = "Observación Obligatorio"
        } else {
            val adapter = nombreClienteAdapter
            val clientesNombres = mutableListOf<String>() // Inicializar como lista mutable
            adapter?.let {
                for (nombre in it.listaNombres) {
                    //showToast("Cliente: $nombre")
                    if (nombre =="Cliente")
                        continue
                    clientesNombres.add(nombre) // Agregar nombre a la lista mutable
                }
            }

            val canalSeleccionado = view.findViewById<Spinner>(R.id.spinnerCanalVenta).selectedItem as CanalVentaModel

            ventaViewModel.generaPedido(mesasModel.codigo, canalSeleccionado.Codigo, clientCount, observacion, clientesNombres)
            bottomSheetDialog.dismiss()
            isBottomSheetMesaOpen = false
        }
    }

    private fun showBottomSheetMenuDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_opciones, null)
        bottomSheetDialog.setContentView(view)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val behavior = BottomSheetBehavior.from(view.parent as View)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        isBottomSheetMenuOpen = true

        view.findViewById<TextView>(R.id.tv_user_name).text = usuarioModel?.nombre
        view.findViewById<TextView>(R.id.tv_user_role).text = usuarioModel?.tipo
        view.findViewById<TextView>(R.id.tv_restaurant_name).text = usuarioModel?.empresa
//        view.findViewById<CheckBox>(R.id.cb_vercuentas).isChecked = if(usuarioModel?.vercuentas!! >0.0){true} else {false}
        view.findViewById<TextView>(R.id.tv_vercuentas).text = if(usuarioModel?.vercuentas!! >0.0){"SI puede ver todos los pedidos"} else {"NO puede ver todos los pedidos"}

        view.findViewById<LinearLayout>(R.id.ll_cerrar_sesion).setOnClickListener {
            AlertDialog.Builder(this@VentaActivity)
                .setMessage("¿Estás seguro de cerrar sesion?")
                .setPositiveButton("Sí") { _, _ ->
                    ventaViewModel.cerrarSesion()
                    finish() }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        view.findViewById<LinearLayout>(R.id.ll_mi_turno).setOnClickListener {
            // Procesar el resultado si es válido
            bottomSheetDialog.dismiss()
            isBottomSheetMenuOpen = false
            val intent = Intent(this, TurnoActivity::class.java)

            pedidoCartaLauncher.launch(intent)

        }

        view.findViewById<ImageButton>(R.id.btn_salir).setOnClickListener {
            bottomSheetDialog.dismiss()
            isBottomSheetMenuOpen = false
        }
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = it.height // Evita que se corte
            }
        }

    }

    private fun handlePedidoResult(currentCute: PedidoModel?) {
        when (currentCute?.status) {
            0 -> {
                //showToast("Pedido: ${currentCute.codigopedido}")
                isBottomSheetMesaOpen =true
                if (currentCute.detalle.isNotEmpty()){
                    // Oculta o muestra según categoría de pantalla
                    when (getScreenCategory()) {
                        ScreenCategory.SMALL -> {
                            val intent = Intent(this, DetallePedidoActivity::class.java)
                            //pedidoLauncher.launch(intent)
                            intent.putExtra("Key_Carta", "No");

                            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                            pedidoLauncher.launch(intent, options)
                        }
                        else -> {
                            // ≥600dp → tablet o más grande
                            val intent = Intent(this, PedidoActivity::class.java)
                            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                            pedidoLauncher.launch(intent, options)
                        }
                    }


                } else{

                    val intent = Intent(this, PedidoActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, 0)
                    pedidoLauncher.launch(intent, options)
                }
            }
            1 -> showToast("Operación fallida")
            2 -> mesaSeleccionada?.let {  if (!isBottomSheetMesaOpen)showBottomSheetMesaDialog(it) }
            99 -> showToast(currentCute.mensaje)
        }
        ventaViewModel.fetchSalonList()
    }

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultValue = data?.getStringExtra(KEY_RESULT)
                if (resultValue=="cierre_turno" || resultValue=="cerrarsesion"){
                    ventaViewModel.cerrarSesion()
                    finishActivity("cierre_turno")
                    return@registerForActivityResult
                }
                // Verificar si el valor resultante es nulo o vacío, si es necesario
                if (resultValue =="carta") {
                    // Procesar el resultado si es válido
                    val intent = Intent(this, PedidoActivity::class.java)
                    //intent.putExtra("Key_ConDetalle", "No");
                    pedidoCartaLauncher.launch(intent)
                } else {
                    // Mostrar un mensaje o manejar el caso donde no se reciba un valor válido
                    //showToast("Resultado vacío o nulo recibido")
                    // Toast.makeText(this, "Resultado no válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Manejar caso cuando el resultado no es OK
                //showToast("La actividad no devolvió un resultado OK")
                // Toast.makeText(this, "Operación fallida", Toast.LENGTH_SHORT).show()
            }
            ventaViewModel.fetchSalonList()
            isBottomSheetMesaOpen =false
        }
    private val pedidoCartaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val keystring = result.data?.getStringExtra(KEY_RESULT)
                if (keystring=="cierre_turno" || keystring=="cerrarsesion"){
                    finishActivity("cierre_turno")
                    ventaViewModel.cerrarSesion()
                    return@registerForActivityResult
                }
                ventaViewModel.fetchSalonList()
            }
        }
    // Definir la constante KEY_RESULT para evitar errores de escritura
    companion object {
        const val KEY_RESULT = "key"
    }

}
