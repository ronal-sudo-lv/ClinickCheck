package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/FarmacoRepository.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.FarmacoEntity
import kotlinx.coroutines.flow.Flow

    /**
     * FarmacoRepository: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface FarmacoRepository {
    /**
     * getAllFarmacos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAllFarmacos(): Flow<List<FarmacoEntity>>
    /**
     * searchFarmacos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun searchFarmacos(query: String): Flow<List<FarmacoEntity>>
    /**
     * insertFarmaco: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertFarmaco(farmaco: FarmacoEntity): Long
}
