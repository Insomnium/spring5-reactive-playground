package net.ins.reactiveapp.routes

import net.ins.reactiveapp.handlers.UsersHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.router

/**
 *
 *  @author Igor_Petrov@epam.com
 *      Created at 1/22/2018
 */
@Configuration
@EnableWebFlux
open class LearningRouter(private val handler: UsersHandler) {

    @Bean
    open fun apiRouter() = router {
        (accept(APPLICATION_JSON) and "/api").nest {
            "/users".nest {
                GET("/", handler::getAll)
                GET("/stream", handler::streamAll)
                GET("/{id}", handler::findById)
            }
        }
    }
}