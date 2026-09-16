package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/FarmacoRepositoryImpl.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.dao.FarmacoDao
import com.example.clinickcheck.local.entity.FarmacoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

    /**
     * FarmacoRepositoryImpl: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class FarmacoRepositoryImpl @Inject constructor(
    private val farmacoDao: FarmacoDao
) : FarmacoRepository {

    /**
     * getAllFarmacos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getAllFarmacos(): Flow<List<FarmacoEntity>> =
        farmacoDao.getAllFarmacos()

    /**
     * searchFarmacos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun searchFarmacos(query: String): Flow<List<FarmacoEntity>> =
        farmacoDao.searchFarmacos(query)

    /**
     * insertFarmaco: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun insertFarmaco(farmaco: FarmacoEntity): Long =
        farmacoDao.insertFarmaco(farmaco)
}
