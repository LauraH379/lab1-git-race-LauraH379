package es.unizar.webeng.hello.stats

import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.kotlin.whenever
import es.unizar.webeng.hello.controller.StatsController


@WebMvcTest(StatsController::class)
class StatsMVCTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greetingRepo: GreetingHistoryRepository

    @Test
    fun `should return statistics per user correctly`() {
        // Datos simulados
        val greetings = listOf(
            GreetingHistory(username = "Alice", message = "Buenos días, Alice"),
            GreetingHistory(username = "Alice", message = "Buenas tardes, Alice"),
            GreetingHistory(username = "Bob", message = "Buenas noches, Bob"),
            GreetingHistory(username = "Alice", message = "Buenas noches, Alice")
        )

        // Mockear el repositorio
        whenever(greetingRepo.findAll()).thenReturn(greetings)

        // Realizar petición GET a /api/stats
        mockMvc.perform(get("/api/stats"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2))) // 2 usuarios: Alice y Bob
            // Comprobaciones de Alice
            .andExpect(jsonPath("$[?(@.username == 'Alice')].total", contains(3)))
            .andExpect(jsonPath("$[?(@.username == 'Alice')].buenosDias", contains(1)))
            .andExpect(jsonPath("$[?(@.username == 'Alice')].buenasTardes", contains(1)))
            .andExpect(jsonPath("$[?(@.username == 'Alice')].buenasNoches", contains(1)))
            // Comprobaciones de Bob
            .andExpect(jsonPath("$[?(@.username == 'Bob')].total", contains(1)))
            .andExpect(jsonPath("$[?(@.username == 'Bob')].buenosDias", contains(0)))
            .andExpect(jsonPath("$[?(@.username == 'Bob')].buenasTardes", contains(0)))
            .andExpect(jsonPath("$[?(@.username == 'Bob')].buenasNoches", contains(1)))
    }
}
