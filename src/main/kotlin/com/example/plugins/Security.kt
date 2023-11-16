package com.example.plugins

import com.example.model.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    install(Sessions){
        cookie<UserSession>("user_session")
    }
}
