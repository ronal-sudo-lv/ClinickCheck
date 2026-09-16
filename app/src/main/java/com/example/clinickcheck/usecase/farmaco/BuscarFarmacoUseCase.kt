package com.example.clinickcheck.usecase.farmaco


/**
 * Archivo: com/example/clinickcheck/usecase/farmaco/BuscarFarmacoUseCase.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.FarmacoEntity
import com.example.clinickcheck.data.repository.FarmacoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso encargado de la búsqueda de fármacos del catálogo.
 * Si la consulta está en blanco, devuelve el catálogo completo ordenado
 * por nombre genérico; en caso contrario filtra por nombre genérico o
 * denominación comercial.
 */
    /**
     * BuscarFarmacoUseCase: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class BuscarFarmacoUseCase @Inject constructor(
    private val farmacoRepository: FarmacoRepository
) {
    /**
     * Ejecuta la búsqueda de fármacos.
     * @param query Término de búsqueda.
     * @return [Flow] con la lista de fármacos encontrados.
     */
    operator fun invoke(query: String): Flow<List<FarmacoEntity>> {
        return if (query.isBlank()) {
            farmacoRepository.getAllFarmacos()
        } else {
            farmacoRepository.searchFarmacos(query.trim())
        }
    }
}

