package com.example.clinickcheck.local.relation


/**
 * Archivo: com/example/clinickcheck/local/relation/Relations.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.room.Embedded
import androidx.room.Relation
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.FarmacoEntity
import com.example.clinickcheck.local.entity.HistoriaClinicaEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity

/**
 * Representa la relación entre un paciente y sus registros de historia clínica.
 */
    /**
     * PacienteConHistorias: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PacienteConHistorias(
    @Embedded val paciente: PacienteEntity,
    @Relation(parentColumn = "idPaciente", entityColumn = "idPaciente")
    val historias: List<HistoriaClinicaEntity>
)

/**
 * Representa la relación entre un paciente y sus alertas activas.
 */
    /**
     * PacienteConAlertas: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PacienteConAlertas(
    @Embedded val paciente: PacienteEntity,
    @Relation(parentColumn = "idPaciente", entityColumn = "pacienteId")
    val alertas: List<AlertaEntity>
)

/**
 * Representa el detalle de una prescripción junto con la información del fármaco asociado.
 */
    /**
     * DetalleConFarmaco: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class DetalleConFarmaco(
    @Embedded val detalle: PrescripcionDetalleEntity,
    @Relation(parentColumn = "idFarmaco", entityColumn = "idFarmaco")
    val farmaco: FarmacoEntity
)

/**
 * Representa una prescripción completa, incluyendo todos sus detalles de fármacos.
 */
    /**
     * PrescripcionConDetalles: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PrescripcionConDetalles(
    @Embedded val prescripcion: PrescripcionEntity,
    @Relation(
        entity = PrescripcionDetalleEntity::class,
        parentColumn = "idPrescripcion",
        entityColumn = "idPrescripcion"
    )
    val detalles: List<DetalleConFarmaco>
)

/**
 * Representa el detalle de una prescripción y su historial de dosis administradas.
 */
    /**
     * DetalleConDosis: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class DetalleConDosis(
    @Embedded val detalle: PrescripcionDetalleEntity,
    @Relation(parentColumn = "idDetalle", entityColumn = "idDetalle")
    val dosisRegistradas: List<DosisRegistroEntity>
)
