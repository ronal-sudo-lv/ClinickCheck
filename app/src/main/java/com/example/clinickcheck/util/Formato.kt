package com.example.clinickcheck.util


/**
 * Archivo: com/example/clinickcheck/util/Formato.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
/**
 * Normaliza el texto de la cama de un paciente para que siempre se muestre
 * con el formato estandar "Cama X", sin importar si el valor guardado ya
 * traia el prefijo, venia en otra capitalizacion, o era solo un numero
 * suelto (ej. "12", "cama12", "Cama 12" -> todos se muestran "Cama 12").
 */
    /**
     * formatearCama: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun formatearCama(cama: String): String {
    val valor = cama.trim()
    if (valor.isEmpty()) return "Cama S/N"
    val sinPrefijo = valor.replace(Regex("(?i)^cama\\s*"), "").trim()
    return if (sinPrefijo.isEmpty()) "Cama S/N" else "Cama $sinPrefijo"
}
