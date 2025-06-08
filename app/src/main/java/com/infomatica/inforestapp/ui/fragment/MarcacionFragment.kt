package com.infomatica.inforestapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.FragmentMarcacionBinding
import com.infomatica.inforestapp.ui.BaseActivity


class MarcacionFragment private constructor(
    private val onResult: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentMarcacionBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_INITIAL_VALUE = "ARG_INITIAL_VALUE"
        private const val ARG_DESCRIPCIONL_VALUE = "ARG_DESCRIPCIONL_VALUE"

        fun newInstance(onResult: (String) -> Unit): MarcacionFragment {
            return MarcacionFragment(onResult).apply {
                arguments = Bundle().apply {
//                    putString(ARG_INITIAL_VALUE, titulo)
//                    putString(ARG_DESCRIPCIONL_VALUE, descripcion)
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
        _binding = FragmentMarcacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.tvTituloMensaje.setText(titulo)
//        binding.tvDescripcionMensaje.setText(descripcion)

        binding.btnCancelar.setOnClickListener {
            onResult("")
            dismiss()
        }
        binding.etpassmarcacion.setOnClickListener{
            // Obtener la instancia de BaseActivity
            val baseActivity = activity as? BaseActivity
            baseActivity?.mostrarNumPadDialog(parentFragmentManager, tipo = "login") { cantidad ->
                if (cantidad.toString().length > 0) {
                    onResult(cantidad.toInt().toString())
                    dismiss()
                } else {
                    baseActivity?.showToast("Ingrese su password!")
                    return@mostrarNumPadDialog
                }
            }
        }

        binding.btnAceptar.setOnClickListener{
            val password = binding.etpassmarcacion.text.toString().trim()
            if (password.isNullOrBlank()){
                return@setOnClickListener
            }else {
                onResult(password)
                dismiss()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}