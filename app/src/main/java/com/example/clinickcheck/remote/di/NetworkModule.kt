package com.example.clinickcheck.remote.di


/**
 * Archivo: com/example/clinickcheck/remote/di/NetworkModule.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.BuildConfig
import com.example.clinickcheck.data.remote.TranslationApi
import com.example.clinickcheck.remote.ClinicalTablesApi
import com.example.clinickcheck.remote.MedicalConditionsApiService
import com.example.clinickcheck.remote.RxNormApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer dependencias relacionadas con la red (Retrofit, OkHttp).
 */
@Module
@InstallIn(SingletonComponent::class)
    /**
     * NetworkModule: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
object NetworkModule {

    @Provides
    @Singleton
    /**
     * provideOkHttpClient: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    /**
     * provideRetrofit: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CLINICAL_TABLES_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    /**
     * provideMedicalConditionsApiService: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideMedicalConditionsApiService(retrofit: Retrofit): MedicalConditionsApiService {
        return retrofit.create(MedicalConditionsApiService::class.java)
    }

    @Provides
    @Singleton
    /**
     * provideClinicalTablesApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideClinicalTablesApi(okHttpClient: OkHttpClient): ClinicalTablesApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CLINICAL_TABLES_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClinicalTablesApi::class.java)
    }

    @Provides
    @Singleton
    /**
     * provideTranslationApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideTranslationApi(okHttpClient: OkHttpClient): TranslationApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.TRANSLATION_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApi::class.java)
    }

    @Provides
    @Singleton
    /**
     * provideRxNormApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun provideRxNormApi(okHttpClient: OkHttpClient): RxNormApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.RXNORM_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RxNormApi::class.java)
    }
}
