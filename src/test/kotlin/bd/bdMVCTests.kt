package es.unizar.webeng.hello.bd

import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import es.unizar.webeng.hello.services.getGreeting

@WebMvcTest
class BdMVCTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greetingRepo: GreetingHistoryRepository

    

    @Test
    fun `should return history with mocked database`() {
        // Preparamos datos simulados en el repositorio
        val mockHistory = listOf(
            GreetingHistory(id = 1, username = "Alice", message = getGreeting("Alice")),
            GreetingHistory(id = 2, username = "Bob", message = getGreeting("Bob"))
        )

        whenever(greetingRepo.findAll()).thenReturn(mockHistory)

        // Hacemos la petición al endpoint de history
        mockMvc.perform(get("/api/history").accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].username", equalTo("Alice")))
            .andExpect(jsonPath("$[0].message", equalTo(getGreeting("Alice"))))
            .andExpect(jsonPath("$[1].username", equalTo("Bob")))
            .andExpect(jsonPath("$[1].message", equalTo(getGreeting("Bob"))))
    }
}
