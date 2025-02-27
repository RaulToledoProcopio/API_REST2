package com.es.aplicacion.repository

import com.es.aplicacion.model.Tarea
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TareaRepository : MongoRepository<Tarea, String> {
    fun findByUsername(username: String): List<Tarea> // Cambia "usuarioId" por "username"
    fun findByTitulo(titulo: String): Tarea?
}
