package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/PrescripcionRepositoryImpl.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.dao.DosisRegistroDao
import com.example.clinickcheck.local.dao.PrescripcionDao
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity
import com.example.clinickcheck.local.relation.PrescripcionConDetalles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación de [PrescripcionRepository] utilizando Room DAOs.
 */
    /**
     * PrescripcionRepositoryImpl: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class PrescripcionRepositoryImpl @Inject constructor(
    private val prescripcionDao: PrescripcionDao,
    private val dosisRegistroDao: DosisRegistroDao
) : PrescripcionRepository {

    /**
     * getPrescripcionesActivasPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getPrescripcionesActivasPorPaciente(idPaciente: Long): Flow<List<PrescripcionConDetalles>> =
        prescripcionDao.getPrescripcionesActivasByPaciente(idPaciente)

    /**
     * getDetallesActivosGlobal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getDetallesActivosGlobal(): Flow<List<PrescripcionDetalleEntity>> =
        prescripcionDao.getDetallesActivosGlobal()

    /**
     * insertPrescripcion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun insertPrescripcion(prescripcion: PrescripcionEntity): Long = withContext(Dispatchers.IO) {
        prescripcionDao.insertPrescripcion(prescripcion)
    }

    /**
     * registrarDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun registrarDosis(dosis: DosisRegistroEntity): Long = withContext(Dispatchers.IO) {
        dosisRegistroDao.insertDosisRegistro(dosis)
    }

    /**
     * getRegistrosDosisPorDetalle: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getRegistrosDosisPorDetalle(idDetalle: Long): Flow<List<DosisRegistroEntity>> =
        dosisRegistroDao.getRegistrosByDetalle(idDetalle)

    /**
     * countDosisAdministradasHoy: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun countDosisAdministradasHoy(inicioDeHoy: Long): Flow<Int> =
        dosisRegistroDao.countAdministradasHoy(inicioDeHoy)

    /**
     * anularDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun anularDosis(idDosis: Long, razon: String) = withContext(Dispatchers.IO) {
        dosisRegistroDao.anularDosis(idDosis, razon)
    }
}
