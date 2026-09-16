package com.example.clinickcheck.local.converter


/**
 * Archivo: com/example/clinickcheck/local/converter/ClinickCheckTypeConverters.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.room.TypeConverter
import com.example.clinickcheck.local.entity.EstadoDosis
import com.example.clinickcheck.local.entity.RolPersonal
import com.example.clinickcheck.local.entity.TipoAlerta
import com.example.clinickcheck.local.entity.TurnoTrabajo

/**
 * Convertidores de tipos para Room, permitiendo persistir enums como Strings.
 */
    /**
     * ClinickCheckTypeConverters: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class ClinickCheckTypeConverters {

    @TypeConverter
    /**
     * fromRolPersonal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun fromRolPersonal(rol: RolPersonal?): String? = rol?.name

    @TypeConverter
    /**
     * toRolPersonal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun toRolPersonal(value: String?): RolPersonal? =
        value?.let { runCatching { enumValueOf<RolPersonal>(it) }.getOrNull() }

    @TypeConverter
    /**
     * fromEstadoDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun fromEstadoDosis(estado: EstadoDosis?): String? = estado?.name

    @TypeConverter
    /**
     * toEstadoDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun toEstadoDosis(value: String?): EstadoDosis? =
        value?.let { runCatching { enumValueOf<EstadoDosis>(it) }.getOrNull() }

    @TypeConverter
    /**
     * fromTipoAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun fromTipoAlerta(tipo: TipoAlerta?): String? = tipo?.name

    @TypeConverter
    /**
     * toTipoAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun toTipoAlerta(value: String?): TipoAlerta? =
        value?.let { runCatching { enumValueOf<TipoAlerta>(it) }.getOrNull() }

    @TypeConverter
    /**
     * fromTurnoTrabajo: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun fromTurnoTrabajo(turno: TurnoTrabajo?): String? = turno?.name

    @TypeConverter
    /**
     * toTurnoTrabajo: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun toTurnoTrabajo(value: String?): TurnoTrabajo? =
        value?.let { runCatching { enumValueOf<TurnoTrabajo>(it) }.getOrNull() }
}
