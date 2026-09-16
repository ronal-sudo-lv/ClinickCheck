package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/MedicamentosViewModel.kt
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
import com.example.clinickcheck.local.dao.PersonalDao
import com.example.clinickcheck.local.dao.PrescripcionDao
import com.example.clinickcheck.local.dao.UsuarioDao
import com.example.clinickcheck.local.entity.FarmacoEntity
import com.example.clinickcheck.local.entity.PersonalEntity
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity
import com.example.clinickcheck.local.entity.RolPersonal
import com.example.clinickcheck.local.preferences.UserSessionManager
import com.example.clinickcheck.data.repository.FarmacoRepository
import com.example.clinickcheck.data.repository.PrescripcionRepository
import com.example.clinickcheck.remote.RxNormApi
import com.example.clinickcheck.usecase.prescripcion.RegistrarDosisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/** Modelo de UI para una toma de medicamento en la agenda del paciente. */
    /**
     * Medicamento: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class Medicamento(
    val idDetalle: Long,
    val nombre: String,
    val dosis: String,
    val via: String,
    val horaProgramada: String,
    val administrado: Boolean,
    val enfermero: String? = null
)

/** Version interna previa a resolver el nombre del enfermero (solo el id). */
    /**
     * MedicamentoBase: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
private data class MedicamentoBase(
    val idDetalle: Long,
    val nombre: String,
    val dosis: String,
    val via: String,
    val horaProgramada: String,
    val administrado: Boolean,
    val enfermeroId: Long?
)

/**
 * Reactividad en tiempo real: todo el estado de esta pantalla se construye
 * combinando Flows que vienen directamente de Room (prescripciones activas +
 * registros de dosis), en vez de recargas puntuales con .first(). Asi,
 * cualquier dosis registrada desde OTRA pantalla (por ejemplo, desde el
 * detalle del paciente) se refleja aqui de inmediato, sin necesidad de
 * refrescar manualmente ni de volver a entrar a la pantalla.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
    /**
     * MedicamentosViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class MedicamentosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val prescripcionRepository: PrescripcionRepository,
    private val prescripcionDao: PrescripcionDao,
    private val farmacoRepository: FarmacoRepository,
    private val personalDao: PersonalDao,
    private val usuarioDao: UsuarioDao,
    private val userSessionManager: UserSessionManager,
    private val rxNormApi: RxNormApi,
    private val registrarDosisUseCase: RegistrarDosisUseCase
) : ViewModel() {

    // El parametro de navegacion llega como String; se convierte de forma
    // segura a Long porque asi lo requiere el esquema de la base de datos.
    private val pacienteIdTexto: String? = savedStateHandle["pacienteId"]
    private val pacienteId: Long? = pacienteIdTexto?.toLongOrNull()

    private val _cargando = MutableStateFlow(pacienteId == null)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(
        if (pacienteId == null) "Identificador de paciente inválido." else null
    )
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _queryMedicamento = MutableStateFlow("")
    val queryMedicamento: StateFlow<String> = _queryMedicamento.asStateFlow()

    private val _sugerenciasMedicamentos = MutableStateFlow<List<String>>(emptyList())
    val sugerenciasMedicamentos: StateFlow<List<String>> = _sugerenciasMedicamentos.asStateFlow()

    private val _cargandoSugerencias = MutableStateFlow(false)
    val cargandoSugerencias: StateFlow<Boolean> = _cargandoSugerencias.asStateFlow()

    private val _nombresEnfermeros = MutableStateFlow<Map<Long, String>>(emptyMap())
    private val cacheNombresEnfermeros = mutableMapOf<Long, String>()

    init {
        viewModelScope.launch {
            _queryMedicamento
                .debounce(300)
                .map { it.trim() }
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collect { consulta ->
                    _cargandoSugerencias.value = true
                    try {
                        val response = rxNormApi.searchMedicamentos(consulta)
                        val sugerencias = response.drugGroup
                            ?.conceptGroup
                            ?.flatMap { it.conceptProperties.orEmpty() }
                            ?.map { it.nombreMedicamento }
                            ?.distinct()
                            ?.sorted()
                            ?: emptyList()
                        _sugerenciasMedicamentos.value = sugerencias
                    } catch (_: Exception) {
                        _sugerenciasMedicamentos.value = emptyList()
                    } finally {
                        _cargandoSugerencias.value = false
                    }
                }
        }

        viewModelScope.launch {
            _queryMedicamento
                .map { it.trim() }
                .distinctUntilChanged()
                .collect { consulta ->
                    if (consulta.length < 3) {
                        _sugerenciasMedicamentos.value = emptyList()
                    }
                }
        }
    }

    private val medicamentosBase: Flow<List<MedicamentoBase>> = if (pacienteId == null) {
        flowOf(emptyList())
    } else {
        prescripcionRepository.getPrescripcionesActivasPorPaciente(pacienteId)
            .flatMapLatest { prescripciones ->
                val detalles = prescripciones.flatMap { it.detalles }
                if (detalles.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val inicioDeHoy = inicioDelDiaEnMillis()
                    val flujosPorDetalle = detalles.map { detalleConFarmaco ->
                        prescripcionRepository.getRegistrosDosisPorDetalle(detalleConFarmaco.detalle.idDetalle)
                            .map { registros ->
                                val registrosValidosHoy = registros.filter {
                                    it.timestamp >= inicioDeHoy && !it.esAnulado
                                }
                                val ultimoRegistro = registrosValidosHoy.maxByOrNull { it.timestamp }
                                MedicamentoBase(
                                    idDetalle = detalleConFarmaco.detalle.idDetalle,
                                    nombre = detalleConFarmaco.farmaco.nombreGenerico,
                                    dosis = formatearDosis(detalleConFarmaco.detalle.dosis, detalleConFarmaco.farmaco.unidadMedida),
                                    via = detalleConFarmaco.detalle.viaAdministracion,
                                    horaProgramada = detalleConFarmaco.detalle.notas?.takeIf { it.isNotBlank() } ?: "Sin horario",
                                    administrado = registrosValidosHoy.isNotEmpty(),
                                    enfermeroId = ultimoRegistro?.enfermeroId
                                )
                            }
                    }
                    combine(flujosPorDetalle) { it.toList() }
                }
            }
    }

    val medicamentos: StateFlow<List<Medicamento>> = combine(
        medicamentosBase.onEach { base ->
            actualizarNombresEnfermeros(base.mapNotNull { it.enfermeroId }.toSet())
        },
        _nombresEnfermeros
    ) { base, nombres ->
        base.map { m ->
            Medicamento(
                idDetalle = m.idDetalle,
                nombre = m.nombre,
                dosis = m.dosis,
                via = m.via,
                horaProgramada = m.horaProgramada,
                administrado = m.administrado,
                enfermero = m.enfermeroId?.let { nombres[it] }
            )
        }.sortedBy { it.horaProgramada }
    }.onEach { _cargando.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val progreso: StateFlow<Float> = medicamentos.map { lista ->
        if (lista.isEmpty()) 0f else lista.count { it.administrado }.toFloat() / lista.size.toFloat()
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0f)

    /**
     * actualizarNombresEnfermeros: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun actualizarNombresEnfermeros(idsPresentes: Set<Long>) {
        val faltantes = idsPresentes.filter { it !in cacheNombresEnfermeros.keys }
        if (faltantes.isEmpty()) return
        viewModelScope.launch {
            faltantes.forEach { id ->
                usuarioDao.getUsuarioByIdOnce(id)?.let { usuario ->
                    cacheNombresEnfermeros[id] = "Enf. ${usuario.apellido}, ${usuario.nombre.firstOrNull() ?: ' '}."
                }
            }
            _nombresEnfermeros.value = cacheNombresEnfermeros.toMap()
        }
    }

    /**
     * onQueryMedicamentoChanged: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun onQueryMedicamentoChanged(nuevaQuery: String) {
        _queryMedicamento.value = nuevaQuery
    }

    /**
     * seleccionarSugerenciaMedicamento: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun seleccionarSugerenciaMedicamento(nombre: String) {
        _queryMedicamento.value = nombre
        _sugerenciasMedicamentos.value = emptyList()
    }

    /**
     * limpiarSugerenciasMedicamentos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun limpiarSugerenciasMedicamentos() {
        _queryMedicamento.value = ""
        _sugerenciasMedicamentos.value = emptyList()
    }

    /**
     * Marca una toma como administrada, registrando la dosis con
     * trazabilidad del enfermero activo. No hace falta refrescar
     * manualmente: al insertar en Room, el Flow de "medicamentos" se
     * actualiza solo.
     */
    /**
     * marcarComoAdministrado: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun marcarComoAdministrado(medicamento: Medicamento) {
        if (medicamento.administrado) return
        viewModelScope.launch {
            val enfermeroId = userSessionManager.userId.first()
            registrarDosisUseCase(
                idDetalle = medicamento.idDetalle,
                enfermeroId = enfermeroId,
                dosisRealizada = medicamento.dosis.takeWhile { it.isDigit() || it == '.' }
            )
            _mensaje.value = "${medicamento.nombre} marcado como administrado."
        }
    }

    /**
     * Agrega un nuevo medicamento a la agenda del paciente. Si el farmaco no
     * existe en el catalogo se crea; si el paciente no tiene una
     * prescripcion activa se crea una nueva para alojar el detalle. No hace
     * falta refrescar manualmente: Room notifica el cambio automaticamente.
     */
    /**
     * agregarMedicamento: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun agregarMedicamento(
        nombreFarmaco: String,
        dosisTexto: String,
        via: String,
        horaProgramada: String
    ) {
        val id = pacienteId
        if (id == null || nombreFarmaco.isBlank()) {
            _mensaje.value = "Datos incompletos para agregar el medicamento."
            return
        }

        viewModelScope.launch {
            val dosisValor = dosisTexto.takeWhile { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
            val unidad = dosisTexto.dropWhile { it.isDigit() || it == '.' }.trim().ifBlank { "mg" }

            val idFarmaco = obtenerOCrearFarmaco(nombreFarmaco, dosisValor, unidad)
            val idPrescripcion = obtenerOCrearPrescripcionActiva(id)

            prescripcionDao.insertDetalles(
                listOf(
                    PrescripcionDetalleEntity(
                        idPrescripcion = idPrescripcion,
                        idFarmaco = idFarmaco,
                        dosis = dosisValor,
                        viaAdministracion = via.ifBlank { "Oral" },
                        frecuenciaHoras = 24,
                        duracionDias = 1,
                        notas = horaProgramada.ifBlank { null }
                    )
                )
            )

            _mensaje.value = "Medicamento agregado a la agenda."
        }
    }

    /**
     * obtenerOCrearFarmaco: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun obtenerOCrearFarmaco(nombre: String, dosis: Double, unidad: String): Long {
        val coincidencias = farmacoRepository.searchFarmacos(nombre).first()
        val existente = coincidencias.firstOrNull { it.nombreGenerico.equals(nombre.trim(), ignoreCase = true) }
        if (existente != null) return existente.idFarmaco

        return farmacoRepository.insertFarmaco(
            FarmacoEntity(
                nombreGenerico = nombre.trim(),
                formaFarmaceutica = "No especificada",
                concentracion = dosis,
                unidadMedida = unidad
            )
        )
    }

    /**
     * obtenerOCrearPrescripcionActiva: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun obtenerOCrearPrescripcionActiva(idPaciente: Long): Long {
        val activas = prescripcionRepository.getPrescripcionesActivasPorPaciente(idPaciente).first()
        activas.firstOrNull()?.let { return it.prescripcion.idPrescripcion }

        val idProfesional = obtenerOCrearProfesionalPorDefecto()
        return prescripcionRepository.insertPrescripcion(
            PrescripcionEntity(
                idPaciente = idPaciente,
                idProfesionalResponsable = idProfesional,
                fechaPrescripcion = System.currentTimeMillis(),
                observaciones = "Agregado desde Control de Medicamentos"
            )
        )
    }

    /**
     * obtenerOCrearProfesionalPorDefecto: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun obtenerOCrearProfesionalPorDefecto(): Long {
        val userId = userSessionManager.userId.first()
        val usuario = userId?.let { usuarioDao.getUsuarioByIdOnce(it) }

        return PersonalEntity(
            nombre = usuario?.nombre ?: "Personal",
            apellido = usuario?.apellido ?: "ClinickCheck",
            rol = RolPersonal.ENFERMERO,
            licenciaProfesional = usuario?.numLicencia
        ).let { personalDao.insertPersonal(it) }
    }

    /**
     * formatearDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun formatearDosis(dosis: Double, unidad: String): String {
        val valor = if (dosis == dosis.toLong().toDouble()) dosis.toLong().toString() else dosis.toString()
        return "$valor$unidad"
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

    /**
     * mensajeMostrado: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
