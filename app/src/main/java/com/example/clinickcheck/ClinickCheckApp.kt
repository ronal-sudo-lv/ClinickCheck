package com.example.clinickcheck


/**
 * Archivo: com/example/clinickcheck/ClinickCheckApp.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
    /**
     * ClinickCheckApp: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class ClinickCheckApp : Application()
