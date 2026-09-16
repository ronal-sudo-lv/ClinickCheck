package com.example.clinickcheck.remote.dto


/**
 * Archivo: com/example/clinickcheck/remote/dto/RxNormResponse.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.google.gson.annotations.SerializedName

/**
 * Respuesta raíz de la API RxNorm.
 */
    /**
     * RxNormResponse: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class RxNormResponse(
    @SerializedName("drugGroup") val drugGroup: DrugGroup?
)

/**
 * Grupo de medicamentos en la respuesta de RxNorm.
 */
    /**
     * DrugGroup: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class DrugGroup(
    @SerializedName("conceptGroup") val conceptGroup: List<ConceptGroup>?
)

/**
 * Grupo de conceptos que categorizan medicamentos.
 */
    /**
     * ConceptGroup: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class ConceptGroup(
    @SerializedName("conceptProperties") val conceptProperties: List<ConceptProperty>?
)

/**
 * Propiedades específicas de un concepto de medicamento (nombre, RXCUI).
 */
    /**
     * ConceptProperty: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class ConceptProperty(
    @SerializedName("rxcui") val rxcui: String,
    @SerializedName("name") val nombreMedicamento: String
)
