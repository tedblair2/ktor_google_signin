package com.example.plugins

import com.example.model.User
import com.example.model.UserSession
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.get

fun Application.configureRouting(httpClient: HttpClient=get()) {
    val redirects= mutableMapOf<String,String>()
    install(Authentication){
        oauth("google-oauth") {
            urlProvider = {"http://localhost:8100/callback"}
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("CLIENT_ID"),
                    clientSecret = System.getenv("CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call,state->
                        redirects[state]=call.request.queryParameters["redirectUrl"]!!
                    }
                )
            }
            client=httpClient
        }
    }

    routing {
        authenticate("google-oauth"){
            get("/login"){
                //automatically redirects to authorizeUrl from above
            }
            get("/callback"){
                val principal:OAuthAccessTokenResponse.OAuth2?=call.principal()
                call.sessions.set(UserSession(principal!!.state!!,principal.accessToken))
                val redirect=redirects[principal.state!!]
                call.respondRedirect(redirect!!)
            }
        }
        get("/home") {
            val userSession:UserSession?=call.sessions.get()
            if (userSession != null){
                val user=httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo"){
                    headers {
                        append(HttpHeaders.Authorization,"Bearer ${userSession.token}")
                    }
                }.body<User>()
                call.respond(user)
            }else{
                val redirectUrl = URLBuilder("http://0.0.0.0:8100/login").run {
                    parameters.append("redirectUrl", call.request.uri)
                    build()
                }
                call.respondRedirect(redirectUrl)
            }
        }
        get("/test"){
            val userSession:UserSession?=call.sessions.get()
            if (userSession != null){
                call.respond("${userSession.state}, ${userSession.token}")
            }else{
                call.respond("Not found!!")
            }
        }
        get("/logout"){
            call.sessions.clear<UserSession>()
            call.respond("Cleared!!")
        }
    }
}
