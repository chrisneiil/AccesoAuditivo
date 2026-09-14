package com.example.accesoauditivo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun click(text: String) {
        closeSoftKeyboard()
        compose.onNodeWithText(text).performScrollTo().performClick()
    }

    private fun fill(label: String, value: String) {
        compose.onNodeWithText(label).performScrollTo().performTextReplacement(value)
    }

    @Test
    fun fiveRegisteredAccountsSurviveRecreationAndCanLogIn() {
        for (number in 1..5) {
            click("Registrarme")
            fill("Nombre completo", "Estudiante $number")
            fill("Correo electronico", "estudiante$number@demo.cl")
            fill("Contrasena minimo 4 caracteres", "clave$number")
            closeSoftKeyboard()
            compose.onNode(isToggleable()).performScrollTo().performClick()
            click("Crear cuenta")
            compose.onNodeWithText("Usuario registrado. Ahora puedes iniciar sesion.")
                .performScrollTo().assertIsDisplayed()
        }

        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Usuarios registrados: 5").performScrollTo().assertIsDisplayed()

        for (number in 1..5) {
            fill("Correo electronico", "estudiante$number@demo.cl")
            fill("Contrasena", "clave$number")
            click("Ingresar")
            compose.onNodeWithText("Bienvenido/a Estudiante $number").assertIsDisplayed()
            click("Cerrar sesion")
        }

        click("Recuperar contrasena")
        fill("Correo electronico", "estudiante5@demo.cl")
        click("Buscar cuenta")
        compose.onNodeWithText("Cuenta encontrada. Pista temporal: clave5").assertIsDisplayed()
        fill("Correo electronico", "desconocido@demo.cl")
        click("Buscar cuenta")
        compose.onNodeWithText("No existe una cuenta asociada a ese correo.").assertIsDisplayed()
    }

    @Test
    fun invalidRegistrationShowsAnErrorWithoutCreatingAnAccount() {
        click("Registrarme")
        click("Crear cuenta")
        compose.onNodeWithText("Ingresa el nombre completo.").performScrollTo().assertIsDisplayed()
        fill("Nombre completo", "Estudiante")
        fill("Correo electronico", "correo-invalido")
        click("Crear cuenta")
        compose.onNodeWithText("Ingresa un correo electronico valido.").performScrollTo().assertIsDisplayed()
        click("Volver al login")
        compose.onNodeWithText("Usuarios registrados: 0").performScrollTo().assertIsDisplayed()
    }
}
