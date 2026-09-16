package com.example.clinickcheck.local.di


/**
 * Archivo: com/example/clinickcheck/local/di/RepositoryModule.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.data.repository.AntecedenteRepository
import com.example.clinickcheck.data.repository.AntecedenteRepositoryImpl
import com.example.clinickcheck.data.repository.FarmacoRepository
import com.example.clinickcheck.data.repository.FarmacoRepositoryImpl
import com.example.clinickcheck.data.repository.PacienteRepository
import com.example.clinickcheck.data.repository.PacienteRepositoryImpl
import com.example.clinickcheck.data.repository.PrescripcionRepository
import com.example.clinickcheck.data.repository.PrescripcionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para vincular las interfaces de repositorio con sus implementaciones.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPacienteRepository(
        pacienteRepositoryImpl: PacienteRepositoryImpl
    ): PacienteRepository

    @Binds
    @Singleton
    abstract fun bindPrescripcionRepository(
        prescripcionRepositoryImpl: PrescripcionRepositoryImpl
    ): PrescripcionRepository

    @Binds
    @Singleton
    abstract fun bindFarmacoRepository(
        farmacoRepositoryImpl: FarmacoRepositoryImpl
    ): FarmacoRepository

    @Binds
    @Singleton
    abstract fun bindAntecedenteRepository(
        antecedenteRepositoryImpl: AntecedenteRepositoryImpl
    ): AntecedenteRepository
}

