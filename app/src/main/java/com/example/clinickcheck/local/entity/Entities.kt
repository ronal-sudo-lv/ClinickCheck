package com.example.clinickcheck.local.entity


/**
 * Archivo: com/example/clinickcheck/local/entity/Entities.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "personal")
    /**
     * PersonalEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PersonalEntity(
    @PrimaryKey(autoGenerate = true)
    val idPersonal: Long = 0,
    val nombre: String,
    val apellido: String,
    val rol: RolPersonal,
    val licenciaProfesional: String? = null,
    val activo: Boolean = true,
    val turno: TurnoTrabajo = TurnoTrabajo.MANANA
)

@Entity(tableName = "paciente")
    /**
     * PacienteEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PacienteEntity(
    @PrimaryKey(autoGenerate = true)
    val idPaciente: Long = 0,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: Long,
    val sexo: String,
    val dni: String,
    val cama: String? = null,
    val estado: String = EstadoPaciente.ESTABLE.name,
    val fechaAlta: Long? = null,
    val eliminadoEn: Long? = null,
    val alergias: String? = null,
    val observaciones: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "historia_clinica",
    indices = [Index(value = ["idPaciente"])],
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["idPaciente"],
            childColumns = ["idPaciente"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * HistoriaClinicaEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class HistoriaClinicaEntity(
    @PrimaryKey(autoGenerate = true)
    val idHistoria: Long = 0,
    val idPaciente: Long,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val titulo: String? = null,
    val descripcion: String,
    val tipo: String? = null,
    val profesionalId: Long? = null
)

@Entity(tableName = "farmaco")
    /**
     * FarmacoEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class FarmacoEntity(
    @PrimaryKey(autoGenerate = true)
    val idFarmaco: Long = 0,
    val nombreGenerico: String,
    val denominacionComercial: String? = null,
    val formaFarmaceutica: String? = null,
    val concentracion: Double = 0.0,
    val unidadMedida: String = "mg"
)

@Entity(
    tableName = "prescripcion",
    indices = [Index(value = ["idPaciente"])],
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["idPaciente"],
            childColumns = ["idPaciente"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * PrescripcionEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PrescripcionEntity(
    @PrimaryKey(autoGenerate = true)
    val idPrescripcion: Long = 0,
    val idPaciente: Long,
    val idProfesionalResponsable: Long? = null,
    val fechaPrescripcion: Long = System.currentTimeMillis(),
    val activa: Boolean = true,
    val observaciones: String? = null
)

@Entity(
    tableName = "prescripcion_detalle",
    indices = [Index(value = ["idPrescripcion", "idFarmaco"])],
    foreignKeys = [
        ForeignKey(
            entity = PrescripcionEntity::class,
            parentColumns = ["idPrescripcion"],
            childColumns = ["idPrescripcion"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FarmacoEntity::class,
            parentColumns = ["idFarmaco"],
            childColumns = ["idFarmaco"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * PrescripcionDetalleEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PrescripcionDetalleEntity(
    @PrimaryKey(autoGenerate = true)
    val idDetalle: Long = 0,
    val idPrescripcion: Long,
    val idFarmaco: Long,
    val dosis: Double,
    val viaAdministracion: String,
    val frecuenciaHoras: Int,
    val duracionDias: Int,
    val horaProgramada: String? = null,
    val notas: String? = null
)

@Entity(
    tableName = "dosis_registro",
    indices = [Index(value = ["idDetalle"])],
    foreignKeys = [
        ForeignKey(
            entity = PrescripcionDetalleEntity::class,
            parentColumns = ["idDetalle"],
            childColumns = ["idDetalle"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * DosisRegistroEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class DosisRegistroEntity(
    @PrimaryKey(autoGenerate = true)
    val idDosis: Long = 0,
    val idDetalle: Long,
    val idPersonalAdministra: Long? = null,
    val enfermeroId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val dosisRealizada: Double? = null,
    val estado: EstadoDosis = EstadoDosis.COMPLETADA,
    val observaciones: String? = null,
    val esAnulado: Boolean = false,
    val razonAnulacion: String? = null,
    val registradoPor: Long? = null
)

@Entity(
    tableName = "alerta",
    indices = [Index(value = ["pacienteId"])],
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["idPaciente"],
            childColumns = ["pacienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * AlertaEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true)
    val idAlerta: Long = 0,
    val pacienteId: Long,
    val tipoAlerta: String,
    val mensaje: String,
    val nivelSeveridad: String = "MEDIA",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val atendida: Boolean = false
)

@Entity(
    tableName = "signo_vital",
    indices = [Index(value = ["pacienteId"])],
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["idPaciente"],
            childColumns = ["pacienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * SignoVitalEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class SignoVitalEntity(
    @PrimaryKey(autoGenerate = true)
    val idSignoVital: Long = 0,
    val pacienteId: Long,
    val enfermeroId: Long? = null,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val presionArterial: String? = null,
    val frecuenciaCardiaca: Int? = null,
    val temperatura: Float? = null,
    val saturacionOxigeno: Int? = null,
    val dolorEva: Int? = null,
    val esAnulado: Boolean = false,
    val razonAnulacion: String? = null,
    val registradoPor: Long? = null
)

@Entity(tableName = "usuarios")
    /**
     * UsuarioEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Long = 0,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val nombre: String,
    val apellido: String,
    val rol: String,
    val turno: TurnoTrabajo = TurnoTrabajo.MANANA,
    val numLicencia: String? = null,
    val activo: Boolean = true
)

@Entity(
    tableName = "antecedente",
    indices = [Index(value = ["pacienteId"])],
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["idPaciente"],
            childColumns = ["pacienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
    /**
     * AntecedenteEntity: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class AntecedenteEntity(
    @PrimaryKey(autoGenerate = true)
    val idAntecedente: Long = 0,
    val pacienteId: Long,
    val nombreEnfermedad: String,
    val codigoICD10: String? = null,
    val descripcion: String? = null,
    val esRelevanteSignosVitales: Boolean = false,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val origen: String = "MANUAL",
    val esAnulado: Boolean = false,
    val razonAnulacion: String? = null,
    val registradoPor: Long? = null
)

    /**
     * RolPersonal: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
enum class RolPersonal {
    ADMINISTRADOR,
    MEDICO,
    ENFERMERO
}

    /**
     * EstadoPaciente: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
enum class EstadoPaciente {
    ESTABLE,
    CRITICO,
    OBSERVACION,
    EN_ALTA,
    ALTA
}

    /**
     * EstadoDosis: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
enum class EstadoDosis {
    COMPLETADA,
    PENDIENTE,
    ANULADA
}

    /**
     * TipoAlerta: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
enum class TipoAlerta {
    HIPOXIA,
    FEBRIL,
    HIPOTENSION,
    OTRO
}

    /**
     * TurnoTrabajo: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
enum class TurnoTrabajo {
    MANANA,
    TARDE,
    NOCHE
}
