package es.unizar.webeng.hello.services

import java.time.LocalDateTime

// Función que genera un saludo personalizado dependiendo de la hora actual.
public fun getGreeting(name: String = ""): String {
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
