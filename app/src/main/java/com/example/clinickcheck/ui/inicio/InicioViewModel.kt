package com.example.clinickcheck.ui.inicio


/**
 * Archivo: com/example/clinickcheck/ui/inicio/InicioViewModel.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinickcheck.local.dao.UsuarioDao
import com.example.clinickcheck.local.entity.EstadoPaciente
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.preferences.UserSessionManager
import com.example.clinickcheck.data.repository.PacienteRepository
import com.example.clinickcheck.data.repository.PrescripcionRepository
import com.example.clinickcheck.ui.paciente.PacienteListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

    /**
     * InicioUiState: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class InicioUiState(
    val nombreUsuario: String = "",
    val turnoTexto: String = "",
    val cargando: Boolean = true
)

/**
 * ViewModel exclusivo de la pantalla de Inicio. Es independiente de
 * SignosVitalesViewModel y de MedicamentosViewModel: no comparte instancia
 * ni logica con esos ViewModels, ni mezcla el DAO de signos vitales con el
 * de dosis. Todo el estado se construye combinando Flows reactivos que
 * vienen directamente de Room (PacienteRepository / PrescripcionRepository),
 * por lo que el Dashboard queda sincronizado en tiempo real: cualquier
 * cambio en pacientes, prescripciones o dosis administradas en cualquier
 * otra pantalla se refleja aqui de inmediato, sin recargas manuales.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
    /**
     * InicioViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class InicioViewModel @Inject constructor(
    private val pacienteRepository: PacienteRepository,
    private val prescripcionRepository: PrescripcionRepository,
    private val usuarioDao: UsuarioDao,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    private val pacientesActivos: Flow<List<PacienteEntity>> = pacienteRepository.getAllPacientesActivos()

    private val idsConAlertaActiva: Flow<Set<Long>> = pacienteRepository.getIdsPacientesConAlertasActivas()
        .map { it.toSet() }

    /** Lista simplificada de "Mis Pacientes" para la pantalla de Inicio. */
    val misPacientes: StateFlow<List<PacienteListItem>> = combine(
        pacientesActivos,
        idsConAlertaActiva
    ) { pacientes, idsConAlerta ->
        pacientes.map { paciente ->
            PacienteListItem(
                paciente = paciente,
                tieneAlertaActiva = idsConAlerta.contains(paciente.idPaciente)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactivo: Room notifica automaticamente cualquier cambio en la tabla
    // "paciente" (por ejemplo, si el estado de un paciente pasa a CRITICO).
    val pacientesCriticosCount: StateFlow<Int> = pacientesActivos
        .map { lista -> lista.count { it.estado.equals(EstadoPaciente.CRITICO.name, ignoreCase = true) } }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    // Reactivo: se combina el total de tomas programadas hoy (que cambia
    // solo cuando se crean/editan prescripciones) con el conteo de dosis
    // administradas hoy (que Room actualiza al instante en cuanto se
    // registra una dosis desde CUALQUIER pantalla). Nunca se lee con
    // .first(): por eso el contador del Dashboard ya no se desincroniza.
    val dosisPendientes: StateFlow<Int> = combine(
        totalProgramadoHoy(),
        prescripcionRepository.countDosisAdministradasHoy(inicioDelDiaEnMillis())
    ) { totalProgramado, administradoHoy ->
        (totalProgramado - administradoHoy).coerceAtLeast(0)
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    init {
        cargarCabecera()
    }

    /**
     * cargarCabecera: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun cargarCabecera() {
        viewModelScope.launch {
            val userId = userSessionManager.userId.first()
            val usuario = userId?.let { usuarioDao.getUsuarioByIdOnce(it) }
            _uiState.value = InicioUiState(
                nombreUsuario = usuario?.let { "${it.nombre} ${it.apellido}" } ?: "Usuario",
                turnoTexto = usuario?.let { "Turno ${it.turno.name} — Sala 3" } ?: "Turno no disponible",
                cargando = false
            )
        }
    }

    /**
     * Total de tomas programadas hoy en todos los pacientes activos, segun
     * la misma heuristica de frecuencia horaria usada en MedicamentosViewModel
     * (calculada aqui de forma independiente, sin depender de esa clase).
     * Es reactivo: se recalcula solo cuando cambian las prescripciones activas.
     */
    /**
     * totalProgramadoHoy: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun totalProgramadoHoy(): Flow<Int> =
        prescripcionRepository.getDetallesActivosGlobal().map { detalles ->
            detalles.sumOf { detalle ->
                val frecuencia = detalle.frecuenciaHoras.coerceAtLeast(1)
                (24 / frecuencia).coerceIn(1, 6)
            }
        }

    /**
     * inicioDelDiaEnMillis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun inicioDelDiaEnMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
