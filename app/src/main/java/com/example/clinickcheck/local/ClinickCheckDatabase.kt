package com.example.clinickcheck.local


/**
 * Archivo: com/example/clinickcheck/local/ClinickCheckDatabase.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.clinickcheck.local.converter.ClinickCheckTypeConverters
import com.example.clinickcheck.local.dao.AlertaDao
import com.example.clinickcheck.local.dao.AntecedenteDao
import com.example.clinickcheck.local.dao.DosisRegistroDao
import com.example.clinickcheck.local.dao.FarmacoDao
import com.example.clinickcheck.local.dao.HistoriaClinicaDao
import com.example.clinickcheck.local.dao.PacienteDao
import com.example.clinickcheck.local.dao.PersonalDao
import com.example.clinickcheck.local.dao.PrescripcionDao
import com.example.clinickcheck.local.dao.SignoVitalDao
import com.example.clinickcheck.local.dao.UsuarioDao
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.AntecedenteEntity
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.FarmacoEntity
import com.example.clinickcheck.local.entity.HistoriaClinicaEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.PersonalEntity
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.entity.UsuarioEntity

/**
 * Base de datos principal de ClinickCheck (Versión 5).
 * Centraliza la persistencia local cifrada y expone los DAOs del sistema.
 */
@Database(
    entities = [
        PersonalEntity::class,
        PacienteEntity::class,
        HistoriaClinicaEntity::class,
        FarmacoEntity::class,
        PrescripcionEntity::class,
        PrescripcionDetalleEntity::class,
        DosisRegistroEntity::class,
        AlertaEntity::class,
        SignoVitalEntity::class,
        UsuarioEntity::class,
        AntecedenteEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(ClinickCheckTypeConverters::class)
abstract class ClinickCheckDatabase : RoomDatabase() {

    abstract fun personalDao(): PersonalDao
    abstract fun pacienteDao(): PacienteDao
    abstract fun historiaClinicaDao(): HistoriaClinicaDao
    abstract fun farmacoDao(): FarmacoDao
    abstract fun prescripcionDao(): PrescripcionDao
    abstract fun dosisRegistroDao(): DosisRegistroDao
    abstract fun alertaDao(): AlertaDao
    abstract fun signoVitalDao(): SignoVitalDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun antecedenteDao(): AntecedenteDao
}
