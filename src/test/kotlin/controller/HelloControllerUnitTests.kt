package es.unizar.webeng.hello.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ui.Model
import org.springframework.ui.ExtendedModelMap
import java.time.LocalDateTime
import java.util.Locale
import java.time.format.DateTimeFormatter;
import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.times



class HelloControllerUnitTests {
    private lateinit var controller: HelloController
    private lateinit var model: Model
    
    @BeforeEach
    fun setup() {
        val mockRepo = mock<GreetingHistoryRepository>()
        controller = HelloController("Test Message", mockRepo)
        model = ExtendedModelMap()
    }

    // Función que genera un saludo personalizado dependiendo de la hora actual.
    private fun getGreeting(name: String = ""): String {
        val now = LocalDateTime.now()  // Obtenemos la fecha y hora actual
        val hour = now.hour // Extraemos la hora (0–23)

        // Determinamos el saludo base según el rango horario
        val baseGreeting = when (hour) {
            in 6..13 -> "Buenos días"
            in 13..21 -> "Buenas tardes"
            else -> "Buenas noches"
        }

        // Si el nombre no está en blanco, lo agregamos al saludo.  Si está vacío, solo devolvemos el saludo base.
        return if (name.isNotBlank()) "$baseGreeting, $name" else baseGreeting
    }
    
    @Test
    fun `should return welcome view with default message`() {
        val view = controller.welcome(model, "")
        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message")).isEqualTo("Test Message")
        assertThat(model.getAttribute("name")).isEqualTo("")
    }
    
    @Test
    fun `should return welcome view with personalized message`() {
        val name = "Developer"
        val expectedMessage = getGreeting(name)
        val view = controller.welcome(model, "Developer")
        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message")).isEqualTo(expectedMessage)
        assertThat(model.getAttribute("name")).isEqualTo("Developer")
    }
    
    @Test
    fun `should return API response with timestamp`() {
        val name = "Test"
        val expectedMessage = getGreeting(name)
        val mockRepo = mock<GreetingHistoryRepository>()
        val apiController = HelloApiController(mockRepo)
        val response = apiController.helloApi("Test")
        
        assertThat(response).containsKey("message")
        assertThat(response).containsKey("timestamp")
        assertThat(response["message"]).isEqualTo(expectedMessage)
        assertThat(response["timestamp"]).isNotNull()
    }
}
