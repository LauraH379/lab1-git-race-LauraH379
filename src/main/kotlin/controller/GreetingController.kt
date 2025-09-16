package es.unizar.webeng.hello.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import es.unizar.webeng.hello.repositories.GreetingHistoryRepository

@RestController
class GreetingController(
    private val repository: GreetingHistoryRepository
) {

    @GetMapping("/api/history")
    fun getHistory(): List<Any> {
        return repository.findAll()
    }
}
