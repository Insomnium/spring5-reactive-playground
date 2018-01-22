package com.epam.webflux.learningapp.domain

//import org.springframework.data.annotation.Id
//import org.springframework.data.mongodb.core.mapping.Document

//@Document(collection = "users")
data class User(
//        @Id
        val id: String?,
        val userId: Long,
        val firstName: String,
        val lastName: String,
        val email: String,
        val todos: List<Todo>)