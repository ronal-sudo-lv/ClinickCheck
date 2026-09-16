package com.example.clinickcheck.usecase.paciente


/**
 * Archivo: com/example/clinickcheck/usecase/paciente/GetPacienteDetalleUseCase.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.relation.PacienteConHistorias
import com.example.clinickcheck.data.repository.PacienteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el detalle completo de un paciente, incluyendo historias clínicas.
 */
    /**
     * GetPacienteDetalleUseCase: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class GetPacienteDetalleUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @param idPaciente ID del paciente a buscar.
     * @return [Flow] con el paciente y sus historias, o null si no se encuentra.
     */
    operator fun invoke(idPaciente: Long): Flow<PacienteConHistorias?> =
        pacienteRepository.getPacienteConHistorias(idPaciente)
}

