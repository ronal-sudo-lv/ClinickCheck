package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/SignoVitalDao.kt
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
import com.example.clinickcheck.local.entity.SignoVitalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de signos vitales.
 */
@Dao
    /**
     * SignoVitalDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface SignoVitalDao {

    /**
     * Registra un nuevo conjunto de signos vitales.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * registrarSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun registrarSignoVital(signoVital: SignoVitalEntity): Long

    /**
     * Obtiene todos los signos vitales de un paciente, ordenados por fecha de registro descendente.
     */
    @Query("SELECT * FROM signo_vital WHERE pacienteId = :pacienteId ORDER BY fechaRegistro DESC")
    /**
     * getSignosVitalesPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getSignosVitalesPorPaciente(pacienteId: Long): Flow<List<SignoVitalEntity>>

    /**
     * Obtiene los signos vitales registrados por un enfermero específico para un paciente.
     */
    @Query("SELECT * FROM signo_vital WHERE pacienteId = :pacienteId AND enfermeroId = :enfermeroId ORDER BY fechaRegistro DESC")
    /**
     * getSignosVitalesPorPacienteYEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getSignosVitalesPorPacienteYEnfermero(pacienteId: Long, enfermeroId: Long): Flow<List<SignoVitalEntity>>

    /**
     * Obtiene un registro de signo vital por su ID.
     */
    @Query("SELECT * FROM signo_vital WHERE idSignoVital = :idSignoVital LIMIT 1")
    /**
     * getSignoVitalPorId: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getSignoVitalPorId(idSignoVital: Long): SignoVitalEntity?

    /**
     * Obtiene el último registro de signo vital activo (no anulado) de un paciente.
     */
    @Query("SELECT * FROM signo_vital WHERE pacienteId = :pacienteId AND esAnulado = 0 ORDER BY fechaRegistro DESC LIMIT 1")
    /**
     * getUltimoSignoVitalActivo: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getUltimoSignoVitalActivo(pacienteId: Long): Flow<SignoVitalEntity?>

    /**
     * Cuenta el número de registros realizados por un enfermero que no han sido anulados.
     */
    @Query("SELECT COUNT(*) FROM signo_vital WHERE enfermeroId = :enfermeroId AND esAnulado = 0")
    /**
     * countPorEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun countPorEnfermero(enfermeroId: Long): Int

    /**
     * Cuenta el número de pacientes distintos atendidos por un enfermero.
     */
    @Query("SELECT COUNT(DISTINCT pacienteId) FROM signo_vital WHERE enfermeroId = :enfermeroId AND esAnulado = 0")
    /**
     * countPacientesDistintosPorEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun countPacientesDistintosPorEnfermero(enfermeroId: Long): Int

    /**
     * Anula un registro de signo vital por auditoría médica.
     */
    @Query("UPDATE signo_vital SET esAnulado = 1, razonAnulacion = :razon WHERE idSignoVital = :idSignoVital")
    /**
     * anularSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun anularSignoVital(idSignoVital: Long, razon: String)
}

