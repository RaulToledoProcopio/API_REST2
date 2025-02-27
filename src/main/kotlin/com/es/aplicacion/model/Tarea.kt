package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("Tarea")
data class Tarea(
    @BsonId
    val id: String? = null,
    val titulo: String,
    val descripcion: String,
    val usuarioId: String,
    val completada: Boolean = false,
    val fechaCreacion: LocalDateTime = LocalDateTime.now()
)
