package es.unizar.webeng.hello.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import es.unizar.webeng.hello.entities.*


//Repositorio de acceso a datos para la entidad GreetingHistory.
@Repository
interface GreetingHistoryRepository : JpaRepository<GreetingHistory, Long> {
    
    //Consulta personalizada que obtiene todos los saludos asociados a un nombre de usuario concreto.
    fun findByUsername(username: String): List<GreetingHistory>
}
