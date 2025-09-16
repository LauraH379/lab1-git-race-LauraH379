package es.unizar.webeng.hello.controller

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.util.Locale
import java.time.format.DateTimeFormatter;
import org.springframework.boot.test.mock.mockito.MockBean
import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository


@WebMvcTest(HelloController::class, HelloApiController::class)
class HelloControllerMVCTests {
    @Value("\${app.message:Welcome to the Modern Web App!}")
    private lateinit var message: String

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greetingRepo: GreetingHistoryRepository 

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
    fun `should return home page with default message`() {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", equalTo(message)))
            .andExpect(model().attribute("name", equalTo("")))
    }
    
    @Test
    fun `should return home page with personalized message`() {
        mockMvc.perform(get("/").param("name", "Developer"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", equalTo(getGreeting("Developer"))))
            .andExpect(model().attribute("name", equalTo("Developer")))
    }
    
    @Test
    fun `should return API response as JSON`() {
        mockMvc.perform(get("/api/hello").param("name", "Test"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", equalTo(getGreeting("Test"))))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}

