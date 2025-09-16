package es.unizar.webeng.hello.bd

import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

/**
 * Test unitario de la base de datos usando GreetingHistoryRepository
 */
@DataJpaTest
class BdUnitTests {

    @Autowired
    private lateinit var repository: GreetingHistoryRepository

    @Test
    fun `should save a greeting and generate id`() {
        val greeting = GreetingHistory(username = "Alice", message = "Hola Alice")
        val saved = repository.save(greeting)

        // El ID se genera automáticamente
        assertThat(saved.id).isNotNull
        assertThat(saved.username).isEqualTo("Alice")
        assertThat(saved.message).isEqualTo("Hola Alice")
    }

    @Test
    fun `should find all greetings`() {
        repository.save(GreetingHistory(username = "Alice", message = "Hola Alice"))
        repository.save(GreetingHistory(username = "Bob", message = "Hola Bob"))

        val all = repository.findAll()
        assertThat(all).hasSize(2)
        assertThat(all.map { it.username }).containsExactlyInAnyOrder("Alice", "Bob")
    }

    @Test
    fun `should find greetings by username`() {
        repository.save(GreetingHistory(username = "Alice", message = "Hola Alice"))
        repository.save(GreetingHistory(username = "Bob", message = "Hola Bob"))

        val aliceGreetings = repository.findByUsername("Alice")
        assertThat(aliceGreetings).hasSize(1)
        assertThat(aliceGreetings[0].username).isEqualTo("Alice")
        assertThat(aliceGreetings[0].message).isEqualTo("Hola Alice")
    }

    @Test
    fun `should return empty list for non-existing username`() {
        val result = repository.findByUsername("NonExistent")
        assertThat(result).isEmpty()
    }
}
