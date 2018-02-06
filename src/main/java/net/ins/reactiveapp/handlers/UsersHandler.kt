package net.ins.reactiveapp.handlers

import net.ins.reactiveapp.repository.ReactiveUserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToServerSentEvents
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

/**
 *
 *  @author Ins137@gmail.com
 *      Created at 1/22/2018
 */
@Service
class UsersHandler(private val reactiveUserRepository: ReactiveUserRepository) {

    val logger: Logger = LoggerFactory.getLogger(UsersHandler::class.java)

    fun findById(req: ServerRequest) = Mono.just(req.pathVariable("id"))
            .map (String::toLong)
            .flatMap { ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyToServerSentEvents(reactiveUserRepository.findByUserId(it))
            }

    fun getAll(req: ServerRequest) = ServerResponse.ok().bodyToServerSentEvents(reactiveUserRepository.findAll(Sort.by("id")))

    fun streamAll(req: ServerRequest) = ServerResponse.ok()
            .contentType(MediaType.APPLICATION_STREAM_JSON)
            .bodyToServerSentEvents(Flux.interval(Duration.ZERO, Duration.ofSeconds(1))
                    .zipWith(reactiveUserRepository.findAll(Sort.by("id")))
                    .map { it.t2 }
            )
}