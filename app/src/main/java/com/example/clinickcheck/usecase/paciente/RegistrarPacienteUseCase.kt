package com.example.clinickcheck.usecase.paciente


/**
 * Archivo: com/example/clinickcheck/usecase/paciente/RegistrarPacienteUseCase.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.data.repository.PacienteRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar un nuevo paciente.
 */
    /**
     * RegistrarPacienteUseCase: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class RegistrarPacienteUseCase @Inject constructor(
    private val repository: PacienteRepository
) {
    /**
     * Ejecuta el registro de un paciente.
     * @param paciente La entidad del paciente a registrar.
     * @return [Result] con el ID del paciente insertado o el error ocurrido.
     */
    suspend operator fun invoke(paciente: PacienteEntity): Result<Long> {
        return try {
            val id = repository.insertPaciente(paciente)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
