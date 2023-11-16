package com.example.plugins

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val appModule= module {
    single {
        HttpClient(CIO){
            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys=true
                })
            }
        }
    }
}

fun Application.configureDI(){
    install(Koin){
        modules(appModule)
    }
}