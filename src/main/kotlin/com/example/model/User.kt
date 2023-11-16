package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val family_name: String,
    val given_name: String,
    val locale: String,
    val name: String,
    val picture: String,
    val id: String
)