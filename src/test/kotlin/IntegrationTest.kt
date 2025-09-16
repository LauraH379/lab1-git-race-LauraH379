package es.unizar.webeng.hello

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.time.LocalDateTime
import java.util.Locale
import java.time.format.DateTimeFormatter;
import es.unizar.webeng.hello.entities.GreetingHistory
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import es.unizar.webeng.hello.services.getGreeting
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import java.util.UUID

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var greetingRepo: GreetingHistoryRepository



    @Test
    fun `should return home page with modern title and client-side HTTP debug`() {
        val response = restTemplate.getForEntity("http://localhost:$port", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("<title>Modern Web App</title>")
        assertThat(response.body).contains("Welcome to Modern Web App")
        assertThat(response.body).contains("Interactive HTTP Testing & Debug")
        assertThat(response.body).contains("Client-Side Educational Tool")
    }

    @Test
    fun `should return personalized greeting when name is provided`() {
        val name = "Developer"
        val expectedGreeting = getGreeting(name)
        val response = restTemplate.getForEntity("http://localhost:$port?name=Developer", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains(expectedGreeting)
    }

    @Test
    fun `should return API response with timestamp`() {
        val name = "Test"
        val expectedGreeting = getGreeting(name)
        val response = restTemplate.getForEntity("http://localhost:$port/api/hello?name=Test", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)
        assertThat(response.body).contains(expectedGreeting)
        assertThat(response.body).contains("timestamp")
    }

    @Test
    fun `should serve Bootstrap CSS correctly`() {
        val response = restTemplate.getForEntity("http://localhost:$port/webjars/bootstrap/5.3.3/css/bootstrap.min.css", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("body")
        assertThat(response.headers.contentType).isEqualTo(MediaType.valueOf("text/css"))
    }

    @Test
    fun `should expose actuator health endpoint`() {
        val response = restTemplate.getForEntity("http://localhost:$port/actuator/health", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("UP")
    }
    
    @Test
    fun `should display client-side HTTP debug interface`() {
        val response = restTemplate.getForEntity("http://localhost:$port?name=Student", String::class.java)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Interactive HTTP Testing & Debug")
        assertThat(response.body).contains("Client-Side Educational Tool")
        assertThat(response.body).contains("Web Page Greeting")
        assertThat(response.body).contains("API Endpoint")
        assertThat(response.body).contains("Health Check")
        assertThat(response.body).contains("Learning Notes:")
    }

    @Test
    fun `should save greeting in database when calling API`() {
        val name = "IntegrationUser"
        val expectedMessage = getGreeting(name)

        // Llamamos al endpoint de API que guarda en BD
        val response = restTemplate.getForEntity("http://localhost:$port/api/hello?name=$name", String::class.java)
        assertThat(response.statusCode).isEqualTo(org.springframework.http.HttpStatus.OK)

        // Verificamos que se ha guardado en la BD
        val greetingsInDb: List<GreetingHistory> = greetingRepo.findByUsername(name)
        assertThat(greetingsInDb).isNotEmpty
        assertThat(greetingsInDb[0].username).isEqualTo(name)
        assertThat(greetingsInDb[0].message).isEqualTo(expectedMessage)
    }

    @Test
    fun `should return correct statistics for saved greetings`() {
        val uniqueId = UUID.randomUUID().toString()
        val name1 = "Alice-$uniqueId"
        val name2 = "Bob-$uniqueId"

        // Insertamos solo registros nuevos, con nombres únicos
        greetingRepo.save(GreetingHistory(username = name1, message = "Buenos días, $name1"))
        greetingRepo.save(GreetingHistory(username = name1, message = "Buenas tardes, $name1"))
        greetingRepo.save(GreetingHistory(username = name1, message = "Buenas noches, $name1"))

        greetingRepo.save(GreetingHistory(username = name2, message = "Buenos días, $name2"))
        greetingRepo.save(GreetingHistory(username = name2, message = "Buenos días, $name2"))

        val typeRef = object : ParameterizedTypeReference<List<Map<String, Any>>>() {}
        val response: ResponseEntity<List<Map<String, Any>>> = restTemplate.exchange(
            "http://localhost:$port/api/stats",
            HttpMethod.GET,
            null,
            typeRef
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val stats = response.body!!

        // Solo buscamos las estadísticas de nuestros nombres únicos
        val aliceStats = stats.find { it["username"] == name1 }!!
        assertThat(aliceStats["total"]).isEqualTo(3)
        assertThat(aliceStats["buenosDias"]).isEqualTo(1)
        assertThat(aliceStats["buenasTardes"]).isEqualTo(1)
        assertThat(aliceStats["buenasNoches"]).isEqualTo(1)

        val bobStats = stats.find { it["username"] == name2 }!!
        assertThat(bobStats["total"]).isEqualTo(2)
        assertThat(bobStats["buenosDias"]).isEqualTo(2)
        assertThat(bobStats["buenasTardes"]).isEqualTo(0)
        assertThat(bobStats["buenasNoches"]).isEqualTo(0)
    }

}
