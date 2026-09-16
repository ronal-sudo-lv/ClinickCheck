package com.example.clinickcheck.data.remote


/**
 * Archivo: com/example/clinickcheck/data/remote/TranslationApi.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

    /**
     * TranslationApi: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface TranslationApi {

    @GET("get")
    /**
     * translate: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun translate(
        @Query("q") q: String,
        @Query("langpair") langpair: String = "en|es"
    ): TranslationResponse

    companion object {
        const val BASE_URL = "https://api.mymemory.translated.net/"
    }
}

    /**
     * TranslationResponse: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class TranslationResponse(
    @SerializedName("responseData") val responseData: TranslationResponseData? = null,
    @SerializedName("matches") val matches: List<TranslationMatch> = emptyList()
)

    /**
     * TranslationResponseData: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class TranslationResponseData(
    @SerializedName("translatedText") val translatedText: String? = null
)

    /**
     * TranslationMatch: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class TranslationMatch(
    @SerializedName("translation") val translation: String? = null
)
