package com.epam.webflux.learningapp.domain

data class Todo(val userId: String,
           val id: String,
           val title: String,
           val completed: Boolean)