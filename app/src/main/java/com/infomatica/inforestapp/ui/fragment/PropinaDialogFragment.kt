package com.infomatica.inforestapp.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.FragmentPropinaBinding

class PropinaDialogFragment private constructor(
    private val onResult: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentPropinaBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_INITIAL_VALUE = "0.0"
        private const val ARG_MONEDA = "0.0"
        private const val ARG_MONTOPEDIDO = "0.00"
        fun newInstance(initialValue: String,Moneda: String, montopedido: Double, onResult: (String) -> Unit): PropinaDialogFragment {
            return PropinaDialogFragment(onResult).apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_VALUE, initialValue)
                    putString(ARG_MONEDA, Moneda)
                    putDouble(ARG_MONTOPEDIDO, montopedido)
                }
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false) // Desactivar el cierre al tocar fuera del modal
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val percent = resources.getFraction(R.fraction.dialog_width_percent, 1, 1) // Obtiene 0.95
        val width = (resources.displayMetrics.widthPixels * percent).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Hacer que el fondo sea transparente
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val MonedaPropina = arguments?.getString(ARG_MONEDA) ?: ""
        val montoPedidoCobro = arguments?.getDouble(ARG_MONTOPEDIDO) ?: 0.00
        binding.tvMonto2.setText("${MonedaPropina}2")
        binding.tvMonto5.setText("${MonedaPropina}5")
        binding.tvMonto10.setText("${MonedaPropina}10")
        binding.tvMonto20.setText("${MonedaPropina}20")

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }

        binding.btnAgregar.isEnabled = false // Deshabilitado al inicio
        // Variable para almacenar el monto seleccionado
        var selectedAmount: String? = null

        val botonesMonto = listOf(
            binding.cvMonto2 to "2",
            binding.cvMonto5 to "5",
            binding.cvMonto10 to "10",
            binding.cvMonto20 to "20",
            binding.cvMontoOtro to "Otro"
        )

        botonesMonto.forEach { (button, amount) ->
            button.setOnClickListener {
                botonesMonto.forEach { it.first.isSelected = false }

                binding.tvMonto2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ColorFondoPantalla))
                binding.tvMonto5.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ColorFondoPantalla))
                binding.tvMonto10.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ColorFondoPantalla))
                binding.tvMonto20.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ColorFondoPantalla))
                binding.tvMontoOtro.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ColorFondoPantalla))

                binding.tvMonto2.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.tvMonto5.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.tvMonto10.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.tvMonto20.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.tvMontoOtro.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))

                if (amount =="Otro"){
                    binding.llPropinaManual.isVisible = true
                    //binding.btnAgregar.isEnabled = false

                    binding.tvMontoOtro.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary))
                    binding.tvMontoOtro.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    if (amount =="2"){
                        binding.tvMonto2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary))
                        binding.tvMonto2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    } else if (amount =="5"){
                        binding.tvMonto5.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary))
                        binding.tvMonto5.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    } else if (amount =="10"){
                        binding.tvMonto10.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary))
                        binding.tvMonto10.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    } else if (amount =="20"){
                        binding.tvMonto20.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondary))
                        binding.tvMonto20.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }

                    binding.llPropinaManual.isVisible = false
                     // Habilitar el botón cuando se selecciona un monto
                }
                binding.btnAgregar.isEnabled = true
                // Establecer el monto seleccionado
                selectedAmount = amount
            }
        }

        binding.btnAgregar.setOnClickListener {
            selectedAmount?.let { amount ->
                ocultarTecladoDesdeFragment()
                if (amount =="Otro"){
                    var montoPropina = binding.etMonto.text.toString()
                    var montoPorcentaje = binding.etPorcentaje.text.toString()
                    if (montoPropina.isNullOrEmpty() && montoPorcentaje.isNullOrEmpty() ){
                        binding.etMonto.error = "Ingrese monto"
                        binding.etPorcentaje.error = "Ingrese porcentaje"
                        return@setOnClickListener
                    }
                    if (montoPropina.isNullOrEmpty()){
                        montoPropina = ((montoPorcentaje.toDouble()/100.00)*montoPedidoCobro).toString()
                    }
                    onResult(montoPropina)
                } else {
                    onResult(amount) // Envía el monto seleccionado al callback
                }
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun ocultarTecladoDesdeFragment() {
        val activity = this.activity
        if (activity != null) {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = activity.currentFocus ?: View(activity) // Evita null en caso de que no haya foco
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
