package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/PacienteRepositoryImpl.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.dao.AlertaDao
import com.example.clinickcheck.local.dao.PacienteDao
import com.example.clinickcheck.local.dao.SignoVitalDao
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.relation.PacienteConHistorias
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta de [PacienteRepository] perteneciente a la capa de datos.
 * Actúa como la única fuente de verdad (Single Source of Truth) para la administración
 * de pacientes, registros de signos vitales y alertas clínicas.
 *
 * Despacha las operaciones de escritura y mutación explícitamente en el hilo de E/S ([Dispatchers.IO])
 * para evitar el bloqueo del hilo principal de la interfaz gráfica (UI Thread).
 *
 * @property pacienteDao Acceso a las operaciones en Room de la entidad Paciente.
 * @property signoVitalDao Acceso a las consultas y registros de constantes vitales.
 * @property alertaDao Acceso al motor de persistencia de alertas hospitalarias.
 */
@Singleton
    /**
     * PacienteRepositoryImpl: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class PacienteRepositoryImpl @Inject constructor(
    private val pacienteDao: PacienteDao,
    private val signoVitalDao: SignoVitalDao,
    private val alertaDao: AlertaDao
) : PacienteRepository {

    // =========================================================================
    // CONSULTAS Y OPERACIONES DE PACIENTES
    // =========================================================================

    /**
     * Obtiene el flujo reactivo de todos los pacientes activos en el sistema (sin soft-delete).
     */
    /**
     * getAllPacientesActivos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getAllPacientesActivos(): Flow<List<PacienteEntity>> {
        return pacienteDao.getAllPacientesActivos()
    }

    /**
     * Filtra la lista de pacientes hospitalizados en cama activa (excluyendo a los dados de Alta).
     */
    /**
     * getPacientesHospitalizados: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getPacientesHospitalizados(): Flow<List<PacienteEntity>> {
        return pacienteDao.getPacientesHospitalizados()
    }

    /**
     * Realiza una búsqueda reactiva de pacientes activos por nombre, apellido o DNI/Cédula.
     */
    /**
     * buscarPacientesActivos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun buscarPacientesActivos(query: String): Flow<List<PacienteEntity>> {
        return pacienteDao.buscarPacientesActivos(query)
    }

    /**
     * Retorna el flujo continuo de datos de un paciente específico por su ID.
     */
    /**
     * getPacienteById: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getPacienteById(idPaciente: Long): Flow<PacienteEntity?> {
        return pacienteDao.getPacienteByIdFlow(idPaciente)
    }

    /**
     * Obtiene el expediente relacional de un paciente junto con todas sus historias clínicas asociadas.
     */
    /**
     * getPacienteConHistorias: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getPacienteConHistorias(idPaciente: Long): Flow<PacienteConHistorias?> {
        return pacienteDao.getPacienteConHistorias(idPaciente)
    }

    /**
     * Modifica el estado clínico del paciente (ESTABLE, CRITICO, ALTA) y registra el timestamp de alta si aplica.
     */
    /**
     * actualizarEstadoClinico: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun actualizarEstadoClinico(idPaciente: Long, nuevoEstado: String, fechaAlta: Long?) =
        withContext(Dispatchers.IO) {
            pacienteDao.actualizarEstadoClinico(idPaciente, nuevoEstado, fechaAlta)
        }

    /**
     * Cuenta reactivamente el número total de pacientes asignados que se encuentran en estado crítico.
     */
    /**
     * countPacientesCriticos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun countPacientesCriticos(): Flow<Int> {
        return pacienteDao.countPacientesCriticos()
    }

    /**
     * Registra un nuevo paciente en la base de datos dentro del contexto I/O.
     */
    /**
     * insertPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun insertPaciente(paciente: PacienteEntity): Long = withContext(Dispatchers.IO) {
        pacienteDao.insertPaciente(paciente)
    }

    /**
     * Actualiza los datos demográficos o administrativos de un paciente.
     */
    /**
     * actualizarPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun actualizarPaciente(paciente: PacienteEntity) = withContext(Dispatchers.IO) {
        pacienteDao.updatePaciente(paciente)
    }

    // =========================================================================
    // CONSULTAS Y OPERACIONES DE SIGNOS VITALES
    // =========================================================================

    /**
     * Recupera el historial completo de tomas de signos vitales vinculadas a un paciente.
     */
    /**
     * getSignosVitalesByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getSignosVitalesByPaciente(pacienteId: Long): Flow<List<SignoVitalEntity>> {
        return signoVitalDao.getSignosVitalesPorPaciente(pacienteId)
    }

    /**
     * Filtra los signos vitales registrados para un paciente por el enfermero activo en sesión.
     */
    /**
     * getSignosVitalesByPacienteYEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getSignosVitalesByPacienteYEnfermero(
        pacienteId: Long,
        enfermeroId: Long
    ): Flow<List<SignoVitalEntity>> {
        return signoVitalDao.getSignosVitalesPorPacienteYEnfermero(pacienteId, enfermeroId)
    }

    /**
     * Registra una nueva toma de signos vitales.
     */
    /**
     * insertarSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun insertarSignoVital(signoVital: SignoVitalEntity): Long = withContext(Dispatchers.IO) {
        signoVitalDao.registrarSignoVital(signoVital)
    }

    /**
     * Aplica la anulación lógica a un registro de signos vitales por error de digitación, preservando la trazabilidad.
     */
    /**
     * anularSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun anularSignoVital(idSignoVital: Long, razon: String) = withContext(Dispatchers.IO) {
        signoVitalDao.anularSignoVital(idSignoVital, razon)
    }

    // =========================================================================
    // CONSULTAS Y OPERACIONES DE ALERTAS CLINICAS
    // =========================================================================

    /**
     * Obtiene la lista de alertas pendientes de atención para un paciente en particular.
     */
    /**
     * getAlertasNoAtendidasByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getAlertasNoAtendidasByPaciente(pacienteId: Long): Flow<List<AlertaEntity>> {
        return alertaDao.getAlertasNoAtendidasByPaciente(pacienteId)
    }

    /**
     * Retorna los IDs de los pacientes que presentan alertas críticas o moderadas activas sin resolver.
     */
    /**
     * getIdsPacientesConAlertasActivas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getIdsPacientesConAlertasActivas(): Flow<List<Long>> {
        return alertaDao.getIdsPacientesConAlertasActivas()
    }

    /**
     * Obtiene todas las alertas pendientes generadas durante el turno de trabajo.
     */
    /**
     * getAlertasPendientes: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getAlertasPendientes(): Flow<List<AlertaEntity>> {
        return alertaDao.getAlertasPendientes()
    }

    /**
     * Inserta una nueva alerta clínica automática (ej. Fiebre o Hipoxia) generada por el motor de reglas.
     */
    /**
     * insertarAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun insertarAlerta(alerta: AlertaEntity): Long = withContext(Dispatchers.IO) {
        alertaDao.insertarAlerta(alerta)
    }

    /**
     * Marca una alerta clínica como atendida por el personal de enfermería.
     */
    /**
     * marcarAlertaAtendida: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun marcarAlertaAtendida(idAlerta: Long) = withContext(Dispatchers.IO) {
        alertaDao.marcarComoAtendida(idAlerta)
    }
}
