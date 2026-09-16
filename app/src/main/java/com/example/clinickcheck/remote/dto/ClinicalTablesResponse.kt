package com.example.clinickcheck.remote.dto


/**
 * Archivo: com/example/clinickcheck/remote/dto/ClinicalTablesResponse.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.google.gson.JsonArray
import com.google.gson.JsonElement

/**
 * Modelo compatible con la respuesta real de Clinical Tables.
 *
 * La API NO devuelve un objeto JSON raíz, sino un Array de arrays. Por eso este
 * DTO se usa como un contenedor utilitario para convertir ese array a una forma
 * más simple, evitando que Gson intente leer un objeto con propiedades "0","1".
 */
    /**
     * ClinicalTablesResponse: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class ClinicalTablesResponse(
    val totalResults: Int = 0,
    val codes: List<String> = emptyList(),
    val extraData: JsonArray? = null,
    val displayResults: List<List<String>> = emptyList()
) {
    companion object {
    /**
     * fromJsonElement: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
        fun fromJsonElement(element: JsonElement): ClinicalTablesResponse {
            if (!element.isJsonArray) return ClinicalTablesResponse()

            val array = element.asJsonArray
            val displayResults = if (array.size() > 3 && array[3].isJsonArray) {
                array[3].asJsonArray.mapNotNull { row ->
                    if (!row.isJsonArray) return@mapNotNull null
                    row.asJsonArray.mapNotNull { value ->
                        value.takeIf { it.isJsonPrimitive }?.asString
                    }
                }
            } else {
                emptyList()
            }

            val codes = if (array.size() > 1 && array[1].isJsonArray) {
                array[1].asJsonArray.mapNotNull { value ->
                    value.takeIf { it.isJsonPrimitive }?.asString
                }
            } else {
                emptyList()
            }

            val totalResults = if (array.size() > 0 && array[0].isJsonPrimitive && array[0].asJsonPrimitive.isNumber) {
                array[0].asInt
            } else {
                0
            }

            val extraData = if (array.size() > 2 && array[2].isJsonArray) {
                array[2].asJsonArray
            } else {
                null
            }

            return ClinicalTablesResponse(
                totalResults = totalResults,
                codes = codes,
                extraData = extraData,
                displayResults = displayResults
            )
        }
    }
}

/**
 * Representa un ítem ICD-10 procesado.
 */
    /**
     * Icd10Item: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class Icd10Item(
    val codigo: String,
    val nombre: String
)
