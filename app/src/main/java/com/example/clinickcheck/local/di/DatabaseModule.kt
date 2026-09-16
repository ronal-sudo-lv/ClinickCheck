package com.example.clinickcheck.local.di


/**
 * Archivo: com/example/clinickcheck/local/di/DatabaseModule.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.clinickcheck.SecurityUtils
import com.example.clinickcheck.local.ClinickCheckDatabase
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
import com.example.clinickcheck.local.entity.EstadoDosis
import com.example.clinickcheck.local.entity.EstadoPaciente
import com.example.clinickcheck.local.entity.FarmacoEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.PersonalEntity
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity
import com.example.clinickcheck.local.entity.RolPersonal
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.entity.TurnoTrabajo
import com.example.clinickcheck.local.entity.UsuarioEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Módulo de Inyección de Dependencias (Hilt) para la persistencia local del sistema ClinickCheck.
 * Se encarga de instanciar e inyectar de forma segura la base de datos cifrada con SQLCipher
 * y proveer de manera singleton cada uno de los DAOs de la arquitectura.
 */
@Module
@InstallIn(SingletonComponent::class)
    /**
     * DatabaseModule: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
object DatabaseModule {

    /**
     * Provee la fábrica de soporte cifrado utilizando SQLCipher.
     * Genera o recupera la frase de paso (passphrase) administrada mediante [SecurityUtils].
     *
     * @param context Contexto global de la aplicación Android.
     * @return [SupportFactory] configurado con la clave simétrica de cifrado.
     */
    @Provides
    @Singleton
    /**
     * provideSupportFactory: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideSupportFactory(@ApplicationContext context: Context): SupportFactory {
        val passphrase = SQLiteDatabase.getBytes(
            SecurityUtils.getOrCreateDatabasePassphrase(context).toCharArray()
        )
        return SupportFactory(passphrase)
    }

    /**
     * Construye la instancia única Singleton de la base de datos Room ([ClinickCheckDatabase]).
     * Aplica cifrado de disco completo, migraciones destructivas de respaldo y precarga inicial.
     *
     * @param context Contexto global de la aplicación.
     * @param supportFactory Fábrica con soporte para cifrado SQLite con SQLCipher.
     * @param databaseProvider Proveedor perezoso ([Provider]) para evitar ciclos de dependencia al sembrar datos.
     * @return Instancia única de [ClinickCheckDatabase].
     */
    @Provides
    @Singleton
    /**
     * provideClinickCheckDatabase: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideClinickCheckDatabase(
        @ApplicationContext context: Context,
        supportFactory: SupportFactory,
        databaseProvider: Provider<ClinickCheckDatabase>
    ): ClinickCheckDatabase {
        return Room.databaseBuilder(
            context,
            ClinickCheckDatabase::class.java,
            "clinickcheck.db"
        )
            .openHelperFactory(supportFactory)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
    /**
     * onCreate: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Precarga asíncrona de datos de demostración en un hilo secundario de I/O
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDemoData(databaseProvider.get())
                    }
                }
            })
            .build()
    }

    /**
     * Semilla inicial de datos de prueba (Seed Data) para facilitar la demostración del sistema.
     * Inserta usuarios (Admin, Enfermero), médicos, pacientes (Estable y Crítico),
     * fármacos de prueba, prescripciones con sus dosis, signos vitales y antecedentes.
     *
     * @param database Instancia activa de la base de datos [ClinickCheckDatabase].
     */
    /**
     * seedDemoData: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun seedDemoData(database: ClinickCheckDatabase) {
        // 1. Registro de Usuario Administrador
        val saltAdmin = SecurityUtils.generateSalt()
        database.usuarioDao().registrarUsuario(
            UsuarioEntity(
                username = "admin",
                passwordHash = SecurityUtils.hashPassword("123", saltAdmin),
                salt = saltAdmin,
                nombre = "Administrador",
                apellido = "Sistema",
                rol = RolPersonal.ADMINISTRADOR.name,
                turno = TurnoTrabajo.MANANA
            )
        )

        // 2. Registro de Usuario Enfermero
        val saltEnfermero = SecurityUtils.generateSalt()
        val idUsuarioEnfermero = database.usuarioDao().registrarUsuario(
            UsuarioEntity(
                username = "enfermero1",
                passwordHash = SecurityUtils.hashPassword("123456", saltEnfermero),
                salt = saltEnfermero,
                nombre = "Carla",
                apellido = "Mendoza",
                rol = RolPersonal.ENFERMERO.name,
                numLicencia = "ENF-2024-001",
                turno = TurnoTrabajo.MANANA
            )
        )

        // 3. Registro de Personal Médico en Plantilla
        val idMedico = database.personalDao().insertPersonal(
            PersonalEntity(
                nombre = "Laura",
                apellido = "Gomez",
                rol = RolPersonal.MEDICO,
                licenciaProfesional = "MP-10234"
            )
        )
        database.personalDao().insertPersonal(
            PersonalEntity(
                nombre = "Carla",
                apellido = "Mendoza",
                rol = RolPersonal.ENFERMERO,
                licenciaProfesional = "ENF-2024-001"
            )
        )

        // 4. Registro de Paciente en Estado Estable
        val idPacienteEstable = database.pacienteDao().insertPaciente(
            PacienteEntity(
                nombre = "David",
                apellido = "Aleman",
                fechaNacimiento = 631152000000L,
                sexo = "M",
                dni = "30111222",
                cama = "Cama 101",
                estado = EstadoPaciente.ESTABLE.name
            )
        )

        // 5. Registro de Paciente en Estado Crítico
        val idPacienteCritico = database.pacienteDao().insertPaciente(
            PacienteEntity(
                nombre = "Ronal",
                apellido = "Linares",
                fechaNacimiento = 315532800000L,
                sexo = "M",
                dni = "27555888",
                cama = "Cama 205",
                estado = EstadoPaciente.CRITICO.name,
                alergias = "Penicilina"
            )
        )

        // 6. Catálogo Inicial de Fármacos
        val idParacetamol = database.farmacoDao().insertFarmaco(
            FarmacoEntity(
                nombreGenerico = "Paracetamol",
                denominacionComercial = "Tempra",
                formaFarmaceutica = "Tableta",
                concentracion = 500.0,
                unidadMedida = "mg"
            )
        )
        val idIbuprofeno = database.farmacoDao().insertFarmaco(
            FarmacoEntity(
                nombreGenerico = "Ibuprofeno",
                denominacionComercial = "Actron",
                formaFarmaceutica = "Tableta",
                concentracion = 400.0,
                unidadMedida = "mg"
            )
        )

        // 7. Prescripciones y Detalles de Administración
        val idPrescripcion = database.prescripcionDao().insertPrescripcion(
            PrescripcionEntity(
                idPaciente = idPacienteEstable,
                idProfesionalResponsable = idMedico,
                fechaPrescripcion = System.currentTimeMillis(),
                observaciones = "Control post-operatorio de rutina"
            )
        )
        val idDetalleParacetamol = database.prescripcionDao().insertDetalles(
            listOf(
                PrescripcionDetalleEntity(
                    idPrescripcion = idPrescripcion,
                    idFarmaco = idParacetamol,
                    dosis = 500.0,
                    viaAdministracion = "Oral",
                    frecuenciaHoras = 8,
                    duracionDias = 5
                ),
                PrescripcionDetalleEntity(
                    idPrescripcion = idPrescripcion,
                    idFarmaco = idIbuprofeno,
                    dosis = 400.0,
                    viaAdministracion = "Oral",
                    frecuenciaHoras = 12,
                    duracionDias = 5
                )
            )
        ).firstOrNull() ?: 0L

        // 8. Trazabilidad de Dosis Aplicadas
        if (idDetalleParacetamol != 0L) {
            database.dosisRegistroDao().insertDosisRegistro(
                DosisRegistroEntity(
                    idDetalle = idDetalleParacetamol,
                    enfermeroId = idUsuarioEnfermero,
                    timestamp = System.currentTimeMillis(),
                    dosisRealizada = 500.0,
                    estado = EstadoDosis.COMPLETADA,
                    observaciones = "Primera dosis administrada sin incidentes"
                )
            )
        }

        // 9. Tomas de Signos Vitales (Paciente Estable y Paciente Crítico)
        database.signoVitalDao().registrarSignoVital(
            SignoVitalEntity(
                pacienteId = idPacienteEstable,
                enfermeroId = idUsuarioEnfermero,
                presionArterial = "118/76",
                frecuenciaCardiaca = 72,
                temperatura = 36.6f,
                saturacionOxigeno = 98,
                dolorEva = 1
            )
        )
        database.signoVitalDao().registrarSignoVital(
            SignoVitalEntity(
                pacienteId = idPacienteCritico,
                enfermeroId = idUsuarioEnfermero,
                presionArterial = "150/95",
                frecuenciaCardiaca = 118,
                temperatura = 38.9f,
                saturacionOxigeno = 87,
                dolorEva = 7
            )
        )

        // 10. Antecedentes Médicos Diagnosticados
        database.antecedenteDao().insertAntecedente(
            AntecedenteEntity(
                pacienteId = idPacienteEstable,
                nombreEnfermedad = "Hipertension esencial",
                codigoICD10 = "I10",
                descripcion = "Diagnosticada hace 3 años, en tratamiento",
                esRelevanteSignosVitales = true,
                origen = "MANUAL"
            )
        )
        database.antecedenteDao().insertAntecedente(
            AntecedenteEntity(
                pacienteId = idPacienteCritico,
                nombreEnfermedad = "Diabetes mellitus tipo 2",
                codigoICD10 = "E11",
                descripcion = "Requiere control glicemico estricto",
                esRelevanteSignosVitales = true,
                origen = "MANUAL"
            )
        )

        // 11. Generación de Alerta de Urgencia
        database.alertaDao().insertarAlerta(
            AlertaEntity(
                pacienteId = idPacienteCritico,
                tipoAlerta = "HIPOXIA",
                mensaje = "Saturacion de oxigeno critica: 87%",
                nivelSeveridad = "CRITICA"
            )
        )
    }

    // =========================================================================
    // PROVEEDORES DE DAOS PARA INYECCIÓN EN REPOSITORIOS
    // =========================================================================

    @Provides
    /**
     * providePersonalDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun providePersonalDao(db: ClinickCheckDatabase): PersonalDao = db.personalDao()

    @Provides
    /**
     * providePacienteDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun providePacienteDao(db: ClinickCheckDatabase): PacienteDao = db.pacienteDao()

    @Provides
    /**
     * provideHistoriaClinicaDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideHistoriaClinicaDao(db: ClinickCheckDatabase): HistoriaClinicaDao = db.historiaClinicaDao()

    @Provides
    /**
     * provideFarmacoDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideFarmacoDao(db: ClinickCheckDatabase): FarmacoDao = db.farmacoDao()

    @Provides
    /**
     * providePrescripcionDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun providePrescripcionDao(db: ClinickCheckDatabase): PrescripcionDao = db.prescripcionDao()

    @Provides
    /**
     * provideDosisRegistroDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideDosisRegistroDao(db: ClinickCheckDatabase): DosisRegistroDao = db.dosisRegistroDao()

    @Provides
    /**
     * provideAlertaDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideAlertaDao(db: ClinickCheckDatabase): AlertaDao = db.alertaDao()

    @Provides
    /**
     * provideSignoVitalDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideSignoVitalDao(db: ClinickCheckDatabase): SignoVitalDao = db.signoVitalDao()

    @Provides
    /**
     * provideUsuarioDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideUsuarioDao(db: ClinickCheckDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    /**
     * provideAntecedenteDao: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideAntecedenteDao(db: ClinickCheckDatabase): AntecedenteDao = db.antecedenteDao()
}

