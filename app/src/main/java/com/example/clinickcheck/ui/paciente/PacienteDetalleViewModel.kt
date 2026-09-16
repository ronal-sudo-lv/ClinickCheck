package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/PacienteDetalleViewModel.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinickcheck.local.dao.DosisRegistroDao
import com.example.clinickcheck.local.dao.UsuarioDao
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.AntecedenteEntity
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.entity.TurnoTrabajo
import com.example.clinickcheck.local.preferences.UserSessionManager
import com.example.clinickcheck.local.relation.PrescripcionConDetalles
import com.example.clinickcheck.data.repository.PacienteRepository
import com.example.clinickcheck.data.repository.PrescripcionRepository
import com.example.clinickcheck.usecase.prescripcion.RegistrarDosisUseCase
import com.example.clinickcheck.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Informacion resumida de un enfermero, cacheada para mostrar en los historiales. */
    /**
     * EnfermeroInfo: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class EnfermeroInfo(
    val nombreCompleto: String,
    val turno: TurnoTrabajo
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
    /**
     * PacienteDetalleViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class PacienteDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pacienteRepository: PacienteRepository,
    private val prescripcionRepository: PrescripcionRepository,
    private val dosisRegistroDao: DosisRegistroDao,
    private val registrarDosisUseCase: RegistrarDosisUseCase,
    private val usuarioDao: UsuarioDao,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val pacienteId: Long = checkNotNull(savedStateHandle["pacienteId"])

    private val _paciente = MutableStateFlow<PacienteEntity?>(null)
    val paciente: StateFlow<PacienteEntity?> = _paciente.asStateFlow()

    private val _prescripciones = MutableStateFlow<List<PrescripcionConDetalles>>(emptyList())
    val prescripciones: StateFlow<List<PrescripcionConDetalles>> = _prescripciones.asStateFlow()

    private val _historialSignos = MutableStateFlow<List<SignoVitalEntity>>(emptyList())
    val historialSignos: StateFlow<List<SignoVitalEntity>> = _historialSignos.asStateFlow()

    private val _alertas = MutableStateFlow<List<AlertaEntity>>(emptyList())
    val alertas: StateFlow<List<AlertaEntity>> = _alertas.asStateFlow()

    private val _historialDosis = MutableStateFlow<List<DosisRegistroEntity>>(emptyList())
    val historialDosis: StateFlow<List<DosisRegistroEntity>> = _historialDosis.asStateFlow()

    // Trazabilidad: filtro "Mis Registros" (true) vs "Todos los Registros" (false)
    private val _soloMisRegistrosSignos = MutableStateFlow(false)
    val soloMisRegistrosSignos: StateFlow<Boolean> = _soloMisRegistrosSignos.asStateFlow()

    private val _soloMisRegistrosDosis = MutableStateFlow(false)
    val soloMisRegistrosDosis: StateFlow<Boolean> = _soloMisRegistrosDosis.asStateFlow()

    // Cache de datos de enfermeros para mostrar en los historiales sin
    // tener que resolver el id en cada fila desde la UI. Incluye el turno
    // para poder filtrar el historial de dosis por turno.
    private val _enfermerosInfo = MutableStateFlow<Map<Long, EnfermeroInfo>>(emptyMap())
    val enfermerosInfo: StateFlow<Map<Long, EnfermeroInfo>> = _enfermerosInfo.asStateFlow()

    init {
        cargarDatosPaciente()
        cargarSignosVitales()
        cargarAlertas()
        cargarHistorialDosis()
    }

    /**
     * cargarDatosPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun cargarDatosPaciente() {
        viewModelScope.launch {
            pacienteRepository.getPacienteById(pacienteId).collect { pacienteObtenido ->
                _paciente.value = pacienteObtenido
            }
        }
        viewModelScope.launch {
            prescripcionRepository.getPrescripcionesActivasPorPaciente(pacienteId).collect { lista ->
                _prescripciones.value = lista
            }
        }
    }

    /**
     * cargarSignosVitales: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun cargarSignosVitales() {
        viewModelScope.launch {
            combine(_soloMisRegistrosSignos, userSessionManager.userId) { soloMios, userId ->
                soloMios to userId
            }.flatMapLatest { (soloMios, userId) ->
                if (soloMios && userId != null) {
                    pacienteRepository.getSignosVitalesByPacienteYEnfermero(pacienteId, userId)
                } else {
                    pacienteRepository.getSignosVitalesByPaciente(pacienteId)
                }
            }.collect { lista ->
                _historialSignos.value = lista
                actualizarNombresEnfermeros(lista.mapNotNull { it.enfermeroId }.toSet())
            }
        }
    }

    /**
     * toggleSoloMisRegistrosSignos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun toggleSoloMisRegistrosSignos() {
        _soloMisRegistrosSignos.value = !_soloMisRegistrosSignos.value
    }

    /**
     * toggleSoloMisRegistrosDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun toggleSoloMisRegistrosDosis() {
        _soloMisRegistrosDosis.value = !_soloMisRegistrosDosis.value
    }

    /**
     * actualizarEstadoPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun actualizarEstadoPaciente(nuevoEstado: String) {
        val estado = nuevoEstado.trim().ifBlank { return }
        viewModelScope.launch {
            val fechaAlta = when {
                estado.equals("EN_ALTA", ignoreCase = true) || estado.equals("ALTA", ignoreCase = true) -> System.currentTimeMillis()
                else -> null
            }
            pacienteRepository.actualizarEstadoClinico(pacienteId, estado, fechaAlta)
        }
    }

    /**
     * guardarSignosVitales: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun guardarSignosVitales(
        pa: String,
        fc: String,
        temp: String,
        spo2: String,
        eva: String
    ) {
        viewModelScope.launch {
            val fcInt = fc.toIntOrNull()
            val tempFloat = temp.toFloatOrNull()
            val spo2Int = spo2.toIntOrNull()
            val evaInt = eva.toIntOrNull()

            // Trazabilidad: se toma automaticamente el usuario de la sesion activa
            val enfermeroId = userSessionManager.userId.first()

            val registro = SignoVitalEntity(
                pacienteId = pacienteId,
                enfermeroId = enfermeroId,
                presionArterial = pa,
                frecuenciaCardiaca = fcInt,
                temperatura = tempFloat,
                saturacionOxigeno = spo2Int,
                dolorEva = evaInt
            )
            pacienteRepository.insertarSignoVital(registro)

            evaluarYGenerarAlertas(fcInt, tempFloat, spo2Int, evaInt)
        }
    }

    /**
     * evaluarYGenerarAlertas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun evaluarYGenerarAlertas(
        fc: Int?,
        temp: Float?,
        spo2: Int?,
        eva: Int?
    ) {
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
            if (it > 100) {
                pacienteRepository.insertarAlerta(
                    AlertaEntity(
                        pacienteId = pacienteId,
                        tipoAlerta = "TAQUICARDIA",
                        mensaje = "Frecuencia cardíaca elevada: $it bpm",
                        nivelSeveridad = "MODERADA"
                    )
                )
            }
        }
    }

    /**
     * registrarAdministracionDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun registrarAdministracionDosis(
        farmacoId: Long,
        idDetalle: Long,
        dosis: String,
        via: String,
        observaciones: String? = null
    ) {
        viewModelScope.launch {
            // Trazabilidad: se toma automaticamente el usuario de la sesion activa
            val enfermeroId = userSessionManager.userId.first()
            registrarDosisUseCase(
                idDetalle = idDetalle,
                enfermeroId = enfermeroId,
                dosisRealizada = dosis,
                observaciones = observaciones
            )
        }
    }

    /**
     * cargarAlertas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun cargarAlertas() {
        viewModelScope.launch {
            pacienteRepository.getAlertasNoAtendidasByPaciente(pacienteId).collect { lista ->
                _alertas.value = lista
            }
        }
    }

    /**
     * cargarHistorialDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun cargarHistorialDosis() {
        viewModelScope.launch {
            combine(_soloMisRegistrosDosis, userSessionManager.userId) { soloMios, userId ->
                soloMios to userId
            }.flatMapLatest { (soloMios, userId) ->
                if (soloMios && userId != null) {
                    dosisRegistroDao.getRegistrosPorPacienteYEnfermero(pacienteId, userId)
                } else {
                    dosisRegistroDao.getRegistrosPorPaciente(pacienteId)
                }
            }.collect { lista ->
                _historialDosis.value = lista
                actualizarNombresEnfermeros(lista.mapNotNull { it.enfermeroId }.toSet())
            }
        }
    }

    /** Resuelve y cachea la info (nombre + turno) de los enfermeros que aun no se conocen. */
    /**
     * actualizarNombresEnfermeros: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun actualizarNombresEnfermeros(idsPresentes: Set<Long>) {
        val faltantes = idsPresentes.filter { it !in _enfermerosInfo.value.keys }
        if (faltantes.isEmpty()) return
        viewModelScope.launch {
            val nuevos = mutableMapOf<Long, EnfermeroInfo>()
            faltantes.forEach { id ->
                usuarioDao.getUsuarioByIdOnce(id)?.let { usuario ->
                    nuevos[id] = EnfermeroInfo(
                        nombreCompleto = "${usuario.nombre} ${usuario.apellido}",
                        turno = usuario.turno
                    )
                }
            }
            if (nuevos.isNotEmpty()) {
                _enfermerosInfo.value = _enfermerosInfo.value + nuevos
            }
        }
    }

    /**
     * atenderAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun atenderAlerta(idAlerta: Long) {
        viewModelScope.launch {
            pacienteRepository.marcarAlertaAtendida(idAlerta)
        }
    }

    /**
     * Anula un registro de signos vitales ingresado por error. No se borra:
     * queda visible en el historial (atenuado/tachado) junto con la razon.
     */
    /**
     * anularSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun anularSignoVital(idSignoVital: Long, razon: String) {
        if (razon.isBlank()) return
        viewModelScope.launch {
            pacienteRepository.anularSignoVital(idSignoVital, razon.trim())
        }
    }

    /**
     * Anula un registro de dosis administrada ingresado por error. No se
     * borra: queda visible en el historial (atenuado/tachado) junto con la
     * razon de anulacion.
     */
    /**
     * anularDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun anularDosis(idDosis: Long, razon: String) {
        if (razon.isBlank()) return
        viewModelScope.launch {
            prescripcionRepository.anularDosis(idDosis, razon.trim())
        }
    }

    /**
     * Genera el PDF de la historia clinica. Los antecedentes se reciben desde
     * la pantalla (ya cargados por AntecedenteViewModel) para no duplicar el
     * acceso a esa fuente de datos aqui; el enfermero responsable se resuelve
     * a partir de la sesion activa para completar el bloque de firma.
     */
    /**
     * exportarPdf: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun exportarPdf(
        context: Context,
        antecedentes: List<AntecedenteEntity> = emptyList(),
        onResultado: (String) -> Unit
    ) {
        val pac = _paciente.value
        if (pac != null) {
            viewModelScope.launch {
                val userId = userSessionManager.userId.first()
                val enfermero = userId?.let { usuarioDao.getUsuarioByIdOnce(it) }
                val pdfGenerator = PdfGenerator(context)
                val archivo = pdfGenerator.generarReportePaciente(
                    paciente = pac,
                    historialSignos = _historialSignos.value,
                    antecedentes = antecedentes,
                    nombreEnfermeroResponsable = enfermero?.let { "${it.nombre} ${it.apellido}" } ?: "No identificado",
                    licenciaEnfermeroResponsable = enfermero?.numLicencia
                )
                if (archivo != null) {
                    onResultado("PDF generado en: ${archivo.absolutePath}")
                } else {
                    onResultado("Error al generar el PDF")
                }
            }
        }
    }
}
