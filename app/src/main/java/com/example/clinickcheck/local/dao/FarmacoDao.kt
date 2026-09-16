package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/FarmacoDao.kt
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
import androidx.room.Update
import com.example.clinickcheck.local.entity.FarmacoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de fármacos.
 */
@Dao
    /**
     * FarmacoDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface FarmacoDao {

    /**
     * Inserta un fármaco. Si ya existe, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertFarmaco: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertFarmaco(farmaco: FarmacoEntity): Long

    /**
     * Actualiza la información de un fármaco.
     */
    @Update
    /**
     * updateFarmaco: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun updateFarmaco(farmaco: FarmacoEntity)

    /**
     * Obtiene todos los fármacos ordenados alfabéticamente por nombre genérico.
     */
    @Query("SELECT * FROM farmaco ORDER BY nombreGenerico ASC")
    /**
     * getAllFarmacos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAllFarmacos(): Flow<List<FarmacoEntity>>

    /**
     * Busca fármacos por nombre genérico o comercial.
     */
    @Query("SELECT * FROM farmaco WHERE nombreGenerico LIKE '%' || :query || '%' OR denominacionComercial LIKE '%' || :query || '%'")
    /**
     * searchFarmacos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun searchFarmacos(query: String): Flow<List<FarmacoEntity>>

    /**
     * Obtiene un fármaco por su ID.
     */
    @Query("SELECT * FROM farmaco WHERE idFarmaco = :idFarmaco")
    /**
     * getFarmacoById: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getFarmacoById(idFarmaco: Long): FarmacoEntity?
}
