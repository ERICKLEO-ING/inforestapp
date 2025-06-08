import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.FragmentNumpadBinding

class NumPadDialogFragment private constructor(
    private val onResult: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentNumpadBinding? = null
    private val binding get() = _binding!!

    var textoarmado: String = ""
    var cantidadAnterior: Double? = null // Variable para almacenar el valor anterior

    companion object {
        private const val ARG_INITIAL_VALUE = "0.0"
        private const val ARG_TIPO = "ARG_TIPO"
        fun newInstance(initialValue: String, tipo: String, onResult: (String) -> Unit): NumPadDialogFragment {
            return NumPadDialogFragment(onResult).apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_VALUE, initialValue)
                    putString(ARG_TIPO, tipo)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumpadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración inicial del valor
        val initialValue = arguments?.getString(ARG_INITIAL_VALUE) ?: ""
        val valueTipo = arguments?.getString(ARG_TIPO) ?: ""

        binding.etInput.text = initialValue
        if (valueTipo == "login") {
            binding.etInput.apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
            binding.btnComma.apply {
                setText("✔")
                textSize = 26f
                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            binding.etInput.setText("")  // Utiliza .clear() para borrar el texto.
            binding.tvAceptar.isVisible = false  // Asegúrate de que 'tvAceptar' sea un View o un TextView con esta propiedad.
        }

        if (valueTipo=="orden"){
            binding.btnComma.apply {
                isVisible =false
            }
            binding.tvAceptar.isVisible = false
            binding.etInput.isVisible =false
            binding.btnDelete.isVisible=false
        }
        val buttons = listOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9,
            binding.btnComma, binding.btn0
        )

        // Configurar los botones para capturar el texto
        buttons.forEach { button ->
            button.setOnClickListener {
                if (button.text == "✔" && valueTipo == "login") {
                    onResult(binding.etInput.text.toString())
                    dismiss()  // Cerrar el diálogo
                    return@setOnClickListener
                }

                if (textoarmado.isNotEmpty()) {
                    textoarmado += button.text
                } else {
                    textoarmado = button.text.toString()
                }

                val cantidad = textoarmado.toDoubleOrNull()
                if (cantidad != null) {
                    cantidadAnterior = cantidad
                    if (valueTipo=="login"){
                        binding.etInput.text = String.format("%.0f", cantidad)
                    } else {
                        binding.etInput.text = String.format("%.3f", cantidad)
                    }

                } else {
                    if (cantidadAnterior != null) {
                        if (valueTipo=="login"){
                            binding.etInput.text = String.format("%.0f", cantidadAnterior)
                        } else {
                            binding.etInput.text = String.format("%.3f", cantidadAnterior)
                        }
                        textoarmado = cantidadAnterior.toString()
                    }
                }

                if (valueTipo=="orden"){
                    onResult(binding.etInput.text.toString())
                    dismiss()  // Cerrar el diálogo
                    return@setOnClickListener
                }
            }
        }

        // Configurar el botón de borrar
        binding.btnDelete.setOnClickListener {
            if (valueTipo=="login"){
                binding.etInput.text = ""
            } else {
                binding.etInput.text = "0.000"
            }
            //binding.etInput.text = "0.000"
            textoarmado = ""
            cantidadAnterior = 0.0
        }

        // Configurar el botón de aceptar
        binding.tvAceptar.setOnClickListener {
            onResult(binding.etInput.text.toString())
            dismiss()  // Cerrar el diálogo
        }
    }

    override fun onStart() {
        super.onStart()

        // Hacer que el diálogo ocupe un tamaño adecuado
        val window = dialog?.window
        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            // Ajusta el tamaño del diálogo
//            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            // Coloca el diálogo en la parte inferior de la pantalla
            it.setGravity(Gravity.BOTTOM)
//
//            // Agregar un ancho máximo para el diálogo
//            val maxWidth = resources.displayMetrics.widthPixels * 0.90 // 90% del ancho de la pantalla
//            it.setLayout(maxWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            // Agregar un ancho máximo para el diálogo

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
