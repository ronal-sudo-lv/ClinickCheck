package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/PacienteDao.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.relation.PacienteConAlertas
import com.example.clinickcheck.local.relation.PacienteConHistorias
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión integral de pacientes en Room Database.
 * Proporciona métodos para inserción, actualización, filtrado por estado clínico,
 * eliminación lógica (auditoría) y relaciones complejas con historias y alertas.
 */
@Dao
    /**
     * PacienteDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface PacienteDao {

    /**
     * Inserta un nuevo paciente en la base de datos local.
     * Si ocurre un conflicto de unicidad (ej. documento duplicado), la transacción se aborta.
     *
     * @param paciente Entidad del paciente a registrar.
     * @return El ID autogenerado del paciente insertado.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    /**
     * insertPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertPaciente(paciente: PacienteEntity): Long

    /**
     * Actualiza la información demográfica o de ubicación de un paciente existente.
     *
     * @param paciente Entidad del paciente con los campos actualizados.
     */
    @Update
    /**
     * updatePaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun updatePaciente(paciente: PacienteEntity)

    /**
     * Obtiene todos los pacientes activos registrados que no han sido eliminados del sistema,
     * ordenados alfabéticamente por apellido y nombre.
     *
     * @return [Flow] reactivo con la lista completa de pacientes activos.
     */
    @Query("SELECT * FROM paciente WHERE eliminadoEn IS NULL ORDER BY apellido, nombre ASC")
    /**
     * getAllPacientesActivos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAllPacientesActivos(): Flow<List<PacienteEntity>>

    /**
     * Consulta síncrona/suspendida de un paciente por su ID único, verificando que no esté borrado.
     *
     * @param idPaciente Identificador único del paciente.
     * @return La entidad [PacienteEntity] o null si no existe.
     */
    @Query("SELECT * FROM paciente WHERE idPaciente = :idPaciente AND eliminadoEn IS NULL")
    /**
     * getPacienteById: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getPacienteById(idPaciente: Long): PacienteEntity?

    /**
     * Consulta reactiva mediante [Flow] para observar cambios en tiempo real en la ficha de un paciente.
     *
     * @param idPaciente Identificador único del paciente.
     * @return [Flow] que emite la entidad del paciente o null si no se encuentra.
     */
    @Query("SELECT * FROM paciente WHERE idPaciente = :idPaciente AND eliminadoEn IS NULL")
    /**
     * getPacienteByIdFlow: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacienteByIdFlow(idPaciente: Long): Flow<PacienteEntity?>

    /**
     * Busca un paciente en la base de datos según su número de documento de identidad (DNI/Cédula).
     *
     * @param dni Número de documento a verificar.
     * @return La entidad [PacienteEntity] correspondiente o null si no hay coincidencia.
     */
    @Query("SELECT * FROM paciente WHERE dni = :dni AND eliminadoEn IS NULL")
    /**
     * getPacienteByDni: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getPacienteByDni(dni: String): PacienteEntity?

    /**
     * Actualiza el estado clínico del paciente ('ESTABLE', 'CRITICO' o 'ALTA').
     * Si el estado asignado es 'ALTA', se guarda la marca de tiempo correspondiente.
     *
     * @param idPaciente Identificador del paciente a actualizar.
     * @param nuevoEstado Texto del nuevo estado clínico.
     * @param fechaAlta Marca de tiempo (Epoch ms) del alta médica o null si permanece ingresado.
     */
    @Query("UPDATE paciente SET estado = :nuevoEstado WHERE idPaciente = :pacienteId")
    /**
     * actualizarEstadoClinico: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun actualizarEstadoClinico(pacienteId: Long, nuevoEstado: String)

    @Query("UPDATE paciente SET estado = :nuevoEstado, fechaAlta = :fechaAlta WHERE idPaciente = :idPaciente")
    /**
     * actualizarEstadoClinico: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun actualizarEstadoClinico(idPaciente: Long, nuevoEstado: String, fechaAlta: Long? = null)

    /**
     * Obtiene únicamente la lista de pacientes que se encuentran actualmente hospitalizados
     * (excluye a los pacientes que ya fueron dados de alta médica) para el tablero del turno.
     *
     * @return [Flow] con la lista de pacientes ingresados ordenados por cama.
     */
    @Query("SELECT * FROM paciente WHERE estado != 'ALTA' AND eliminadoEn IS NULL ORDER BY cama ASC")
    /**
     * getPacientesHospitalizados: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacientesHospitalizados(): Flow<List<PacienteEntity>>

    /**
     * Cuenta el número de pacientes asignados que se encuentran en estado crítico durante el turno.
     *
     * @return Cantidad total de pacientes en estado 'CRITICO'.
     */
    @Query("SELECT COUNT(*) FROM paciente WHERE estado = 'CRITICO' AND eliminadoEn IS NULL")
    /**
     * countPacientesCriticos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun countPacientesCriticos(): Flow<Int>

    /**
     * Aplica un marcado de eliminación lógica (Soft Delete) al paciente fijando el timestamp.
     * El registro se conserva intacto por motivos de trazabilidad e historial médico legal.
     *
     * @param idPaciente Identificador del paciente a inactivar.
     * @param timestamp Fecha y hora de la eliminación en milisegundos.
     */
    @Query("UPDATE paciente SET eliminadoEn = :timestamp WHERE idPaciente = :idPaciente")
    /**
     * softDeletePaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun softDeletePaciente(idPaciente: Long, timestamp: Long)

    /**
     * Obtiene el expediente de un paciente con todas sus historias clínicas vinculadas en una sola transacción.
     *
     * @param idPaciente Identificador del paciente.
     * @return [Flow] con la relación [PacienteConHistorias] cargada.
     */
    @Transaction
    @Query("SELECT * FROM paciente WHERE idPaciente = :idPaciente AND eliminadoEn IS NULL")
    /**
     * getPacienteConHistorias: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacienteConHistorias(idPaciente: Long): Flow<PacienteConHistorias?>

    /**
     * Consulta relacional para obtener un paciente junto con sus alertas activas (fiebre, hipoxia, etc.).
     *
     * @param idPaciente Identificador del paciente.
     * @return [Flow] con la relación [PacienteConAlertas] cargada.
     */
    @Transaction
    @Query("SELECT * FROM paciente WHERE idPaciente = :idPaciente AND eliminadoEn IS NULL")
    /**
     * getPacienteConAlertas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacienteConAlertas(idPaciente: Long): Flow<PacienteConAlertas?>

    /**
     * Búsqueda dinámica de pacientes activos mediante coincidencia parcial (LIKE) por nombre, apellido o documento.
     *
     * @param query Texto o números ingresados en la barra de búsqueda de la UI.
     * @return [Flow] con la lista filtrada de coincidencias.
     */
    @Query("SELECT * FROM paciente WHERE eliminadoEn IS NULL AND (nombre LIKE '%' || :query || '%' OR apellido LIKE '%' || :query || '%' OR dni LIKE '%' || :query || '%') ORDER BY apellido, nombre ASC")
    /**
     * buscarPacientesActivos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun buscarPacientesActivos(query: String): Flow<List<PacienteEntity>>
}
