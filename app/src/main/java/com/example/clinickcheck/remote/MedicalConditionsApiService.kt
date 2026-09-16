package com.example.clinickcheck.remote


/**
 * Archivo: com/example/clinickcheck/remote/MedicalConditionsApiService.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio de la API publica de NIH Clinical Tables para autocompletar
 * enfermedades/diagnosticos y obtener su codigo ICD-10.
 *
 * Documentacion: https://clinicaltables.nlm.nih.gov/apidoc/conditions/v3/doc.html
 */
    /**
     * MedicalConditionsApiService: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface MedicalConditionsApiService {

    /**
     * Busca condiciones médicas por términos.
     * Retorna un Response<JsonArray> que debe ser parseado manualmente debido a su estructura heterogénea.
     */
    @GET("api/conditions/v3/search")
    /**
     * buscarCondiciones: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun buscarCondiciones(
        @Query("terms") terminos: String,
        @Query("df") camposMostrar: String = "consumer_name,icd10cm_codes",
        @Query("maxList") maxResultados: Int = 10
    ): Response<JsonArray>

    companion object {
        /** URL base para Clinical Tables API */
        const val BASE_URL = "https://clinicaltables.nlm.nih.gov/"
    }
}

