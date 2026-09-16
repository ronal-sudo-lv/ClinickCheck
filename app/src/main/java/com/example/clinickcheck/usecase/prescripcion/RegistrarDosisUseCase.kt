package com.example.clinickcheck.usecase.prescripcion


/**
 * Archivo: com/example/clinickcheck/usecase/prescripcion/RegistrarDosisUseCase.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.EstadoDosis
import com.example.clinickcheck.data.repository.PrescripcionRepository
import javax.inject.Inject

/**
 * Caso de uso encargado de registrar la administración de una dosis
 * prescrita a un paciente. Encapsula la construcción de la entidad
 * de registro y delega la persistencia al repositorio de prescripciones.
 */
    /**
     * RegistrarDosisUseCase: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class RegistrarDosisUseCase @Inject constructor(
    private val prescripcionRepository: PrescripcionRepository
) {
    /**
     * Ejecuta el registro de administración de una dosis.
     * @param idDetalle ID del detalle de prescripción.
     * @param enfermeroId ID del enfermero que registra la dosis.
     * @param idPersonalAdministra ID del personal que físicamente administró la dosis.
     * @param dosisRealizada Cantidad de dosis administrada.
     * @param observaciones Notas adicionales sobre la administración.
     * @return [Result] con el ID del registro insertado o el error ocurrido.
     */
    suspend operator fun invoke(
        idDetalle: Long,
        enfermeroId: Long? = null,
        idPersonalAdministra: Long? = null,
        dosisRealizada: String,
        observaciones: String? = null
    ): Result<Long> {
        return try {
            val registro = DosisRegistroEntity(
                idDetalle = idDetalle,
                idPersonalAdministra = idPersonalAdministra,
                enfermeroId = enfermeroId,
                timestamp = System.currentTimeMillis(),
                dosisRealizada = dosisRealizada.toDoubleOrNull(),
                estado = EstadoDosis.COMPLETADA,
                observaciones = observaciones
            )
            val id = prescripcionRepository.registrarDosis(registro)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

