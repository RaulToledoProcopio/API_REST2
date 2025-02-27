package com.es.aplicacion.controller

import com.es.aplicacion.dto.ActualizarTareaDTO
import com.es.aplicacion.dto.NuevaTareaDTO
import com.es.aplicacion.dto.TareaResumenDTO
import com.es.aplicacion.service.TareaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tareas")
class TareaController(
    private val tareaService: TareaService
) {

    data class ErrorResponse(
        val message: String
    )

    @PostMapping("/crearTarea")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun crearTarea(
        @RequestBody nuevaTareaDTO: NuevaTareaDTO
    ): ResponseEntity<Any> {
        val usernameAutenticado = SecurityContextHolder.getContext().authentication.name
        val isAdmin = SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isAdmin && nuevaTareaDTO.username != usernameAutenticado) {
            val errorResponse = ErrorResponse("No tiene permisos para asignar tareas a otro usuario")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
        }

        val tarea = tareaService.crearTarea(nuevaTareaDTO, nuevaTareaDTO.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(tarea)
    }

    @DeleteMapping("/eliminarTarea")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun eliminarTarea(
        @RequestParam titulo: String,
        @RequestParam username: String
    ): ResponseEntity<Any> {
        val usernameAutenticado = SecurityContextHolder.getContext().authentication.name
        val isAdmin = SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isAdmin && username != usernameAutenticado) {
            val errorResponse = ErrorResponse("No tiene permisos para eliminar tareas de otro usuario")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
        }

        val tareaEliminada = tareaService.eliminarTareaPorTitulo(titulo, username)
        return ResponseEntity.status(HttpStatus.OK).body(tareaEliminada)
    }


    @PutMapping("/actualizarTarea")
    @PreAuthorize("hasRole('USER')")
    fun actualizarTarea(
        @RequestParam titulo: String,
        @RequestBody actualizarTareaDTO: ActualizarTareaDTO
    ): ResponseEntity<Any> {
        val usernameAutenticado = SecurityContextHolder.getContext().authentication.name
        val tareaActualizada = tareaService.actualizarTareaPorTitulo(titulo, usernameAutenticado, actualizarTareaDTO)
        return ResponseEntity.status(HttpStatus.OK).body(tareaActualizada)
    }


    @GetMapping("/verTareas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun verTareas(): ResponseEntity<List<TareaResumenDTO>> {
        val usernameAutenticado = SecurityContextHolder.getContext().authentication.name
        val tareas = tareaService.obtenerTareas(usernameAutenticado)
        return ResponseEntity.ok(tareas)
    }
}