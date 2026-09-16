package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/AntecedenteDao.kt
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
import com.example.clinickcheck.local.entity.AntecedenteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de antecedentes médicos.
 */
@Dao
    /**
     * AntecedenteDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface AntecedenteDao {

    /**
     * Inserta un nuevo antecedente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertAntecedente(antecedente: AntecedenteEntity): Long

    /**
     * Anula un antecedente (eliminación lógica con razón).
     */
    @Query("UPDATE antecedente SET esAnulado = 1, razonAnulacion = :razon WHERE idAntecedente = :idAntecedente")
    /**
     * anularAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun anularAntecedente(idAntecedente: Long, razon: String)

    /**
     * Obtiene todos los antecedentes de un paciente en tiempo real (Flow).
     */
    @Query("SELECT * FROM antecedente WHERE pacienteId = :pacienteId ORDER BY fechaRegistro DESC")
    /**
     * getAntecedentesPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAntecedentesPorPaciente(pacienteId: Long): Flow<List<AntecedenteEntity>>

    /**
     * Obtiene todos los antecedentes de un paciente una única vez.
     */
    @Query("SELECT * FROM antecedente WHERE pacienteId = :pacienteId ORDER BY fechaRegistro DESC")
    /**
     * getAntecedentesPorPacienteOnce: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getAntecedentesPorPacienteOnce(pacienteId: Long): List<AntecedenteEntity>
}

