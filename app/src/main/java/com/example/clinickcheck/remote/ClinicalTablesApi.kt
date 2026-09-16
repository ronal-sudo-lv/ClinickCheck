
package com.example.clinickcheck.remote


/**
 * Archivo: com/example/clinickcheck/remote/ClinicalTablesApi.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para la API Clinical Tables de la NLM, utilizada para buscar códigos ICD-10.
 *
 * La API devuelve un JSON raíz en formato Array (no un objeto). Por eso se devuelve
 * JsonElement para que Gson no falle intentando deserializar un objeto cuando la
 * respuesta real es una lista de arrays.
 */
    /**
     * ClinicalTablesApi: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface ClinicalTablesApi {

    @GET("api/icd10cm/v3/search")
    /**
     * searchIcd10: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun searchIcd10(
        @Query("terms") query: String,
        @Query("sf") sf: String = "code,name",
        @Query("df") df: String = "code,name",
        @Query("maxList") maxList: Int = 10
    ): JsonElement

    companion object {
        const val BASE_URL = "https://clinicaltables.nlm.nih.gov/"
    }
}
