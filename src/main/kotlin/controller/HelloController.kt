package es.unizar.webeng.hello.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.Locale
import java.time.format.DateTimeFormatter;
import es.unizar.webeng.hello.repositories.*
import es.unizar.webeng.hello.entities.*

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


@Controller
class HelloController(

    @param:Value("\${app.message:Hello World}") 
    private val message: String,
    private val greetingRepo: GreetingHistoryRepository
) {
    
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String
    ): String {

        //Si hay nombre, devolvemos saludo personalizado. Sino, el mensaje predeterminado.
        val greeting = if (name.isNotBlank()) getGreeting(name) else message
        
        // Guardar saludo en la base de datos si hay nombre
        if (name.isNotBlank()) {
            greetingRepo.save(GreetingHistory(username = name, message = greeting))
        }

        model.addAttribute("message", greeting)
        model.addAttribute("name", name)
        return "welcome"
    }
}

@RestController
class HelloApiController (
    private val greetingRepo: GreetingHistoryRepository
) {
    
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(@RequestParam(defaultValue = "World") name: String): Map<String, String> {

        //Obtenemos el saludo personalizado.
        val greeting = getGreeting(name)

        // Guardar saludo en la BD
        greetingRepo.save(GreetingHistory(username = name, message = greeting))

        return mapOf(
            "message" to "$greeting",
            "timestamp" to java.time.Instant.now().toString()
        )
    }
}

