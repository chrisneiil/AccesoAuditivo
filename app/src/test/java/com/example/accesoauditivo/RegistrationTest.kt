package com.example.accesoauditivo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrationTest {
    @Test
    fun emailRequiresACompleteDomain() {
        assertTrue("ana@demo.cl".hasEmailFormat())
        assertTrue(" ana@demo.cl ".hasEmailFormat())
        assertFalse("ana@".hasEmailFormat())
        assertFalse("ana@demo".hasEmailFormat())
        assertFalse("ana@demo..cl".hasEmailFormat())
    }

    @Test
    fun registrationRejectsAnExistingEmail() {
        val result = validateRegistration("Ana", "ana@demo.cl", "1234", true) { true }
        assertEquals("Este correo ya esta registrado.", result)
    }

    @Test
    fun registrationRequiresConsentAndPasswordLength() {
        assertEquals(
            "Debes aceptar el uso de datos para crear la cuenta.",
            validateRegistration("Ana", "ana@demo.cl", "1234", false) { false }
        )
        assertEquals(
            "La contrasena debe tener al menos 4 caracteres.",
            validateRegistration("Ana", "ana@demo.cl", "123", true) { false }
        )
    }

    @Test
    fun validRegistrationHasNoError() {
        assertEquals("", validateRegistration("Ana", "ana@demo.cl", "1234", true) { false })
    }

    @Test
    fun visualMessageIncludesTheCurrentText() {
        assertEquals(
            "ALERTA VISUAL: Necesidad - Mostrar texto: Necesito indicaciones",
            buildAccessibleMessage("Necesidad", "Mostrar texto", " Necesito indicaciones ", true)
        )
    }
}
