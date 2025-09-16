package es.unizar.webeng.hello.entities

import jakarta.persistence.*
import java.time.LocalDateTime

// Clase de entidad JPA que representa un registro de saludo en la base de datos. 
@Entity
data class GreetingHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    //Nombre de usuario asociado al saludo
    val username: String = "",
    //Mensaje de saludo enviado por el usuario
    val message: String = "",
    //Momento en el que se generó el saludo
    val timestamp: LocalDateTime = LocalDateTime.now()
)
