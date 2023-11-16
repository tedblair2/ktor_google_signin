package com.example.plugins

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.koin.ktor.ext.get

fun Application.configureOAuth(httpClient: HttpClient=get()){

}