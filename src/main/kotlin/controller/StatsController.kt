package es.unizar.webeng.hello.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository
import org.springframework.beans.factory.annotation.Autowired

@RestController
class StatsController @Autowired constructor(
    private val greetingRepo: GreetingHistoryRepository
) {

    @GetMapping("/api/stats")
    fun getStats(): List<Map<String, Any>> {
        val greetings = greetingRepo.findAll()

        // Agrupar por usuario
        val stats = greetings.groupBy { it.username }.map { (username, entries) ->
            val buenosDias = entries.count { it.message.startsWith("Buenos días") }
            val buenasTardes = entries.count { it.message.startsWith("Buenas tardes") }
            val buenasNoches = entries.count { it.message.startsWith("Buenas noches") }
            mapOf(
                "username" to username,
                "total" to entries.size,
                "buenosDias" to buenosDias,
                "buenasTardes" to buenasTardes,
                "buenasNoches" to buenasNoches
            )
        }

        return stats
    }
}
