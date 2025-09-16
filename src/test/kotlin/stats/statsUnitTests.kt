package es.unizar.webeng.hello.stats

import es.unizar.webeng.hello.controller.StatsController
import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.assertj.core.api.Assertions.assertThat

class StatsUnitTests {

    private lateinit var mockRepo: GreetingHistoryRepository
    private lateinit var statsController: StatsController

    @BeforeEach
    fun setup() {
        mockRepo = mock()
        statsController = StatsController(mockRepo)
    }

    @Test
    fun `should calculate correct stats for multiple users`() {
        // Preparamos datos simulados
        val greetings = listOf(
            GreetingHistory(username = "Alice", message = "Buenos días, Alice"),
            GreetingHistory(username = "Alice", message = "Buenas tardes, Alice"),
            GreetingHistory(username = "Alice", message = "Buenas noches, Alice"),
            GreetingHistory(username = "Bob", message = "Buenos días, Bob"),
            GreetingHistory(username = "Bob", message = "Buenos días, Bob")
        )

        whenever(mockRepo.findAll()).thenReturn(greetings)

        val stats = statsController.getStats()

        // Comprobamos Alice
        val aliceStats = stats.find { it["username"] == "Alice" }!!
        assertThat(aliceStats["total"]).isEqualTo(3)
        assertThat(aliceStats["buenosDias"]).isEqualTo(1)
        assertThat(aliceStats["buenasTardes"]).isEqualTo(1)
        assertThat(aliceStats["buenasNoches"]).isEqualTo(1)

        // Comprobamos Bob
        val bobStats = stats.find { it["username"] == "Bob" }!!
        assertThat(bobStats["total"]).isEqualTo(2)
        assertThat(bobStats["buenosDias"]).isEqualTo(2)
        assertThat(bobStats["buenasTardes"]).isEqualTo(0)
        assertThat(bobStats["buenasNoches"]).isEqualTo(0)
    }

    @Test
    fun `should return empty list if no greetings`() {
        whenever(mockRepo.findAll()).thenReturn(emptyList())

        val stats = statsController.getStats()

        assertThat(stats).isEmpty()
    }
}
