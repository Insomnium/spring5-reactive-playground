package com.epam.webflux.learningapp.handlers

import com.epam.webflux.learningapp.domain.User
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.Collections.emptyList

/**
 *
 *  @author Igor_Petrov@epam.com
 *      Created at 1/22/2018
 */
@Service
class UsersHandler {

    val users: List<User> = listOf(
            User("0", 0L, "Ivan", "Ivanov", "IvanInvanov@corpmail.com", emptyList()),
            User("1", 1L, "Petr", "Petrov", "PetrPetrov@corpmail.com", emptyList()),
            User("2", 2L, "John", "Doe", "JohnDoe@corpmail.com", emptyList())
    )

    fun findById(req: ServerRequest) = Mono.just(req.pathVariable("id"))
            .map (String::toLong)
            .flatMap { ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(users[0]))
            }

    fun getAll(req: ServerRequest) = ServerResponse.ok().body(fromObject(users))
}