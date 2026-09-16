package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/SignosVitalesViewModel.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.preferences.UserSessionManager
import com.example.clinickcheck.data.repository.PacienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**x
 * Estado de la pantalla de Signos Vitales.
 */
    /**
     * SignosVitalesUiState: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class SignosVitalesUiState(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensaje: String? = null
)

@HiltViewModel
    /**
     * SignosVitalesViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class SignosVitalesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pacienteRepository: PacienteRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    // El parametro de navegacion llega como String; se convierte de forma
    // segura a Long porque asi lo requiere el esquema de la base de datos
    // (idPaciente es un Long autogenerado por Room).
    private val pacienteIdTexto: String? = savedStateHandle["pacienteId"]
    private val pacienteId: Long? = pacienteIdTexto?.toLongOrNull()

    private val _historialSignos = MutableStateFlow<List<SignoVitalEntity>>(emptyList())
    val historialSignos: StateFlow<List<SignoVitalEntity>> = _historialSignos.asStateFlow()

    private val _uiState = MutableStateFlow(SignosVitalesUiState())
    val uiState: StateFlow<SignosVitalesUiState> = _uiState.asStateFlow()

    init {
        cargarHistorial()
    }

    /**
     * cargarHistorial: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun cargarHistorial() {
        val id = pacienteId
        if (id == null) {
            _uiState.value = _uiState.value.copy(
                cargando = false,
                mensaje = "Identificador de paciente inválido."
            )
            return
        }
        viewModelScope.launch {
            pacienteRepository.getSignosVitalesByPaciente(id).collect { lista ->
                _historialSignos.value = lista
                _uiState.value = _uiState.value.copy(cargando = false)
            }
        }
    }

    /**
     * Guarda un nuevo registro de signos vitales para el paciente actual y
     * evalua automaticamente si los valores ameritan generar una alerta
     * clinica (hipoxia, fiebre o taquicardia). El enfermero que registra el
     * dato se toma automaticamente de la sesion activa.
     */
    /**
     * guardarSignosVitales: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun guardarSignosVitales(
        presionArterial: String,
        frecuenciaCardiaca: String,
        temperatura: String,
        saturacionOxigeno: String,
        dolorEva: String
    ) {
        val id = pacienteId
        if (id == null) {
            _uiState.value = _uiState.value.copy(mensaje = "No se puede guardar: paciente inválido.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(guardando = true)

            val fc = frecuenciaCardiaca.toIntOrNull()
            val temp = temperatura.toFloatOrNull()
            val spo2 = saturacionOxigeno.toIntOrNull()
            val eva = dolorEva.toIntOrNull()

            val enfermeroId = userSessionManager.userId.first()

            pacienteRepository.insertarSignoVital(
                SignoVitalEntity(
                    pacienteId = id,
                    enfermeroId = enfermeroId,
                    presionArterial = presionArterial.trim(),
                    frecuenciaCardiaca = fc,
                    temperatura = temp,
                    saturacionOxigeno = spo2,
                    dolorEva = eva,
                    fechaRegistro = System.currentTimeMillis()
                )
            )

            procesarAlertas(id, fc, temp, spo2)

            _uiState.value = _uiState.value.copy(
                guardando = false,
                mensaje = "Signos vitales registrados correctamente."
            )
        }
    }

    /**
     * procesarAlertas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun procesarAlertas(pacienteId: Long, fc: Int?, temp: Float?, spo2: Int?) {
        spo2?.let {
            if (it < 90) {
                pacienteRepository.insertarAlerta(
                    AlertaEntity(
                        pacienteId = pacienteId,
                        tipoAlerta = "HIPOXIA",
                        mensaje = "Saturación de oxígeno crítica: $it%",
                        nivelSeveridad = "CRITICA"
                    )
                )
            }
        }
        temp?.let {
            if (it >= 38.0f) {
                pacienteRepository.insertarAlerta(
                    AlertaEntity(
                        pacienteId = pacienteId,
                        tipoAlerta = "FIEBRE",
                        mensaje = "Temperatura elevada: $it °C",
                        nivelSeveridad = "MODERADA"
                    )
                )
            }
        }
        fc?.let {
            if (it > 100 || it < 50) {
                pacienteRepository.insertarAlerta(
                    AlertaEntity(
                        pacienteId = pacienteId,
                        tipoAlerta = "FRECUENCIA_ANORMAL",
                        mensaje = "Frecuencia cardíaca fuera de rango: $it bpm",
                        nivelSeveridad = "MODERADA"
                    )
                )
            }
        }
    }

    /**
     * mensajeMostrado: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun mensajeMostrado() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }

    /**
     * Anula un registro ingresado por error. No se borra: queda visible en
     * el historial (atenuado/tachado) junto con la razón de anulación.
     */
    /**
     * anularSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun anularSignoVital(idSignoVital: Long, razon: String) {
        if (razon.isBlank()) return
        viewModelScope.launch {
            pacienteRepository.anularSignoVital(idSignoVital, razon.trim())
            _uiState.value = _uiState.value.copy(mensaje = "Registro anulado correctamente.")
        }
    }

    /** true si algun valor del registro esta fuera de rango clinico normal. */
    /**
     * esAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun esAlerta(signo: SignoVitalEntity): Boolean {
        val fcAnormal = signo.frecuenciaCardiaca?.let { it > 100 || it < 50 } ?: false
        val tempAnormal = signo.temperatura?.let { it >= 38.0f || it < 35.0f } ?: false
        val spo2Anormal = signo.saturacionOxigeno?.let { it < 90 } ?: false
        val dolorAnormal = signo.dolorEva?.let { it >= 7 } ?: false
        return fcAnormal || tempAnormal || spo2Anormal || dolorAnormal
    }
}
