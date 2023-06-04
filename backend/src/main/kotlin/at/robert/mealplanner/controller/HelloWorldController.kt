package at.robert.mealplanner.controller

import at.robert.mealplanner.data.HelloWorldDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @GetMapping("hello")
    fun helloWorld(): HelloWorldDto {
        return HelloWorldDto("Hello Robert")
    }
}
