package com.infomatica.inforestapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.FragmentMensajeBinding
import com.infomatica.inforestapp.ui.BaseActivity

class MensajeDialogFragment private constructor(
    private val onResult: (String, String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentMensajeBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_INITIAL_VALUE = "ARG_INITIAL_VALUE"
        private const val ARG_DESCRIPCIONL_VALUE = "ARG_DESCRIPCIONL_VALUE"
        private const val ARG_MARCACION_VALUE = "ARG_MARCACION_VALUE"
        private const val ARG_EXTRA_VALUE = "ARG_EXTRA_VALUE"

        fun newInstance(titulo: String, descripcion: String, tipo: String = "mensaje", extra: String="",onResult: (String, String) -> Unit): MensajeDialogFragment {
            return MensajeDialogFragment(onResult).apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_VALUE, titulo)
                    putString(ARG_DESCRIPCIONL_VALUE, descripcion)
                    putString(ARG_MARCACION_VALUE, tipo)
                    putString(ARG_EXTRA_VALUE, extra)
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
        _binding = FragmentMensajeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var titulo = arguments?.getString(ARG_INITIAL_VALUE) ?: ""
        val descripcion = arguments?.getString(ARG_DESCRIPCIONL_VALUE) ?: ""
        val tipo = arguments?.getString(ARG_MARCACION_VALUE) ?: ""
        val extra = arguments?.getString(ARG_EXTRA_VALUE) ?: ""

        if (tipo=="descuento") {
            binding.passwordmensaje.isVisible = true
            binding.btnAceptar.setText("SI, APLICAR")
        } else {
            binding.btnAceptar.setText("ACEPTAR")
        }

        if (tipo=="marcacion"){
            binding.btnCancelar.isVisible = false
            if (titulo =="0"){
                binding.tvextra.isVisible = true
                titulo = "!Hora registrada!"
                binding.tvTituloMensaje.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                )
                binding.tvextra.setText(extra)
                binding.lottieAnimation.setAnimation(R.raw.exito_600x600)
            } else {
                titulo = "!Ups! Algo salió mal."
                binding.tvTituloMensaje.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.ColorRojo)
                )
                binding.lottieAnimation.setAnimation(R.raw.error_320x320)
            }
        }
        if (tipo == "impresionmensaje"){
            binding.tvTituloMensaje.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
            binding.lottieAnimation.setAnimation(R.raw.exito_600x600)
            binding.btnCancelar.isVisible = false
        }

        binding.tvTituloMensaje.setText(titulo)
        binding.tvDescripcionMensaje.setText(descripcion)

        binding.btnCancelar.setOnClickListener {
            onResult("cancel","")
            dismiss()
        }
        binding.btnAceptar.setOnClickListener{
            val password = binding.passwordEditText.text.toString()
            //observaciontexto = binding.etObservacion.text.toString()
            onResult("ok",password)
            dismiss()
        }

        binding.passwordEditText.setOnClickListener{
            // Obtener la instancia de BaseActivity
            val baseActivity = activity as? BaseActivity
            baseActivity?.mostrarNumPadDialog(parentFragmentManager, tipo = "login") { cantidad ->
                if (cantidad.toString().length > 0) {
                    onResult("ok",cantidad.toInt().toString())
                    dismiss()
                } else {
                    baseActivity?.showToast("Ingrese su password!")
                    return@mostrarNumPadDialog
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}