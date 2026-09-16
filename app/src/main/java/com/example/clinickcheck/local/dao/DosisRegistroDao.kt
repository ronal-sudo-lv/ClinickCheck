package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/DosisRegistroDao.kt
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
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.relation.DetalleConDosis
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de registros de administración de dosis.
 */
@Dao
    /**
     * DosisRegistroDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface DosisRegistroDao {

    /**
     * Inserta un nuevo registro de administración de dosis.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertDosisRegistro: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertDosisRegistro(dosis: DosisRegistroEntity): Long

    /**
     * Anula un registro de dosis (eliminación lógica con razón).
     */
    @Query("UPDATE dosis_registro SET esAnulado = 1, razonAnulacion = :razon WHERE idDosis = :idDosis")
    /**
     * anularDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun anularDosis(idDosis: Long, razon: String)

    /**
     * Obtiene los registros de dosis para un detalle de prescripción específico.
     */
    @Query("SELECT * FROM dosis_registro WHERE idDetalle = :idDetalle ORDER BY timestamp DESC")
    /**
     * getRegistrosByDetalle: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getRegistrosByDetalle(idDetalle: Long): Flow<List<DosisRegistroEntity>>

    /**
     * Obtiene todos los registros de dosis de un paciente específico.
     */
    @Query("""
        SELECT dr.* FROM dosis_registro dr
        INNER JOIN prescripcion_detalle pd ON dr.idDetalle = pd.idDetalle
        INNER JOIN prescripcion p ON pd.idPrescripcion = p.idPrescripcion
        WHERE p.idPaciente = :pacienteId
        ORDER BY dr.timestamp DESC
    """)
    /**
     * getRegistrosPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getRegistrosPorPaciente(pacienteId: Long): Flow<List<DosisRegistroEntity>>

    /**
     * Obtiene los registros de dosis de un paciente administrados por un enfermero específico.
     */
    @Query("""
        SELECT dr.* FROM dosis_registro dr
        INNER JOIN prescripcion_detalle pd ON dr.idDetalle = pd.idDetalle
        INNER JOIN prescripcion p ON pd.idPrescripcion = p.idPrescripcion
        WHERE p.idPaciente = :pacienteId AND dr.enfermeroId = :enfermeroId
        ORDER BY dr.timestamp DESC
    """)
    /**
     * getRegistrosPorPacienteYEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getRegistrosPorPacienteYEnfermero(pacienteId: Long, enfermeroId: Long): Flow<List<DosisRegistroEntity>>

    /**
     * Obtiene un detalle de prescripción junto con su historial de dosis.
     */
    @Transaction
    @Query("SELECT * FROM prescripcion_detalle WHERE idDetalle = :idDetalle")
    /**
     * getDetalleConDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getDetalleConDosis(idDetalle: Long): Flow<DetalleConDosis?>

    /**
     * Cuenta el número de dosis administradas por un enfermero específico.
     */
    @Query("SELECT COUNT(*) FROM dosis_registro WHERE enfermeroId = :enfermeroId")
    /**
     * countPorEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun countPorEnfermero(enfermeroId: Long): Int

    /**
     * Cuenta el número de dosis administradas hoy que no han sido anuladas.
     */
    @Query("SELECT COUNT(*) FROM dosis_registro WHERE timestamp >= :inicioDeHoy AND esAnulado = 0")
    /**
     * countAdministradasHoy: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun countAdministradasHoy(inicioDeHoy: Long): Flow<Int>
}
