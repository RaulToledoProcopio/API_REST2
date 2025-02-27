package com.es.aplicacion.dto

data class TareaDTO(
    val id: String?,
    val titulo: String,
    val descripcion: String,
    val username: String,
    val completada: Boolean
)