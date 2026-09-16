

package com.example.clinickcheck.remote


/**
 * Archivo: com/example/clinickcheck/remote/RxNormApi.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.remote.dto.RxNormResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para la API RxNorm de la NLM, utilizada para buscar medicamentos.
 */
    /**
     * RxNormApi: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface RxNormApi {

    /**
     * Busca medicamentos por nombre.
     */
    @GET("REST/drugs.json")
    /**
     * searchMedicamentos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun searchMedicamentos(
        @Query("name") query: String
    ): RxNormResponse

    companion object {
        /** URL base para RxNorm API */
        const val BASE_URL = "https://rxnav.nlm.nih.gov/"
    }
}
