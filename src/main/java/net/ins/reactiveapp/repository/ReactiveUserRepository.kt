package net.ins.reactiveapp.repository

import net.ins.reactiveapp.domain.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

/**
 *
 *  @author Ins137@gmail.com
 *      Created at 1/24/2018
 */
interface ReactiveUserRepository : ReactiveMongoRepository<User, Long> {
    fun findByUserId(userId: Long) : Mono<User>
}