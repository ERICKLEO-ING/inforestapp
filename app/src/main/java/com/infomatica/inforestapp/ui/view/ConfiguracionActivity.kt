package com.infomatica.inforestapp.ui.view

import LocalStore
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.ActivityClienteBinding
import com.infomatica.inforestapp.databinding.ActivityConfiguracionBinding
import com.infomatica.inforestapp.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfiguracionActivity : BaseActivity() {
    private lateinit var binding: ActivityConfiguracionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickOnListener()

        var printerBluetooth = LocalStore.getPrinterBluetooth(this)

        if (printerBluetooth=="50"){
            binding.chk50mm.isChecked = true
        } else if (printerBluetooth=="55"){
            binding.chk55mm.isChecked = true
        } else if (printerBluetooth=="80"){
            binding.chk80mm.isChecked = true
        }

        var printerBluetoothDocumento = LocalStore.getPrinterBluetoothDocumento(this)
        if (printerBluetoothDocumento=="1"){
            binding.chkComprobantes.isChecked = true
        } else{
            binding.chkComprobantes.isChecked = false
        }

        var printerBluetoothPrecuenta = LocalStore.getPrinterBluetoothPrecuenta(this)
        if (printerBluetoothPrecuenta=="1"){
            binding.chkPrecuentas.isChecked = true
        } else {
            binding.chkPrecuentas.isChecked = false
        }

        var printerBluetoothDocumentoWindow = LocalStore.getPrinterBluetoothDocumentoWindow(this)
        if (printerBluetoothDocumentoWindow=="1"){
            binding.chkComprobantesWindows.isChecked = true
        } else{
            binding.chkComprobantesWindows.isChecked = false
        }

        var printerBluetoothPrecuentaWindow = LocalStore.getPrinterBluetoothPrecuentaWindow(this)
        if (printerBluetoothPrecuentaWindow=="1"){
            binding.chkPrecuentasWindows.isChecked = true
        } else {
            binding.chkPrecuentasWindows.isChecked = false
        }

        var urlIp = LocalStore.getBaseUrl(this)
        binding.etUrlIpAddress.setText(urlIp)
    }
    private fun setupClickOnListener() {
        try {
            binding.tvCancelarConfiguracion.setOnClickListener {
                finishActivity("0", R.anim.slide_in_left)
            }
            binding.btnGuardarConfiguracion.setOnClickListener {

                if (binding.chk50mm.isChecked){
                    LocalStore.savePrinterBluetooth(this, "50")
                } else if (binding.chk55mm.isChecked){
                    LocalStore.savePrinterBluetooth(this, "55")
                } else if (binding.chk80mm.isChecked){
                    LocalStore.savePrinterBluetooth(this, "80")
                } else {
                    LocalStore.savePrinterBluetooth(this, "50")
                }

                if(binding.chkPrecuentas.isChecked){
                    LocalStore.savePrinterBluetoothPrecuenta(this, "1")
                } else {
                    LocalStore.savePrinterBluetoothPrecuenta(this, "0")
                }

                if(binding.chkComprobantes.isChecked){
                    LocalStore.savePrinterBluetoothDocumento(this, "1")
                } else {
                    LocalStore.savePrinterBluetoothDocumento(this, "0")
                }

                if(binding.chkPrecuentasWindows.isChecked){
                    LocalStore.savePrinterBluetoothPrecuentaWindow(this, "1")
                } else {
                    LocalStore.savePrinterBluetoothPrecuentaWindow(this, "0")
                }

                if(binding.chkComprobantesWindows.isChecked){
                    LocalStore.savePrinterBluetoothDocumentoWindow(this, "1")
                } else {
                    LocalStore.savePrinterBluetoothDocumentoWindow(this, "0")
                }
                if (binding.etUrlIpAddress.text.toString().isNullOrEmpty()){
                   showToast("Debe ingresar una dirección url.")
                    return@setOnClickListener
                } else {
                    LocalStore.saveBaseUrl(this, binding.etUrlIpAddress.text.toString())
                }

                finishActivity("ok", R.anim.slide_in_left)
            }
            binding.llHeaderPrinter.setOnClickListener {
                binding.llPrinterOptions.isVisible = !binding.llPrinterOptions.isVisible
                binding.llPrinterOptionsDocumentos.isVisible = !binding.llPrinterOptionsDocumentos.isVisible
                binding.ivTogglePrinter.setImageResource(if (binding.llPrinterOptions.isVisible) R.drawable.pedido_menos_36x40 else R.drawable.pedido_mas_36x40)

                // En tu Activity o Fragment:
                val colorRes = if (binding.llPrinterOptions.isVisible)
                    R.color.colorPrimary
                else
                    R.color.colorText

                binding.tvheaderPrinter.setTextColor(
                    ContextCompat.getColor(this /* o requireContext() en Fragment */, colorRes)
                )

            }

            binding.llHeaderWindow.setOnClickListener {
                binding.llWindowOptions.isVisible = !binding.llWindowOptions.isVisible
                binding.ivToggleWindows.setImageResource(if (binding.llWindowOptions.isVisible) R.drawable.pedido_menos_36x40 else R.drawable.pedido_mas_36x40)
                // En tu Activity o Fragment:
                val colorRes = if (binding.llWindowOptions.isVisible)
                    R.color.colorPrimary
                else
                    R.color.colorText

                binding.tvheaderWindow.setTextColor(
                    ContextCompat.getColor(this /* o requireContext() en Fragment */, colorRes)
                )
            }

            val checkBoxes = listOf(binding.chk50mm, binding.chk55mm, binding.chk80mm)

            checkBoxes.forEach { cb ->
                cb.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        // Desmarca el resto
                        checkBoxes
                            .filter { it != buttonView }
                            .forEach { it.isChecked = false }
                    }
                }
            }

        } catch (Ex: Exception){

        }
    }
}