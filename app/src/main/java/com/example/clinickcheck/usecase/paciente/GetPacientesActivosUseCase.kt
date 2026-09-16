package com.example.clinickcheck.usecase.paciente


/**
 * Archivo: com/example/clinickcheck/usecase/paciente/GetPacientesActivosUseCase.kt
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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener todos los pacientes activos en el sistema.
 */
    /**
     * GetPacientesActivosUseCase: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class GetPacientesActivosUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @return [Flow] con la lista de pacientes activos.
     */
    operator fun invoke(): Flow<List<PacienteEntity>> =
        pacienteRepository.getAllPacientesActivos()
}

