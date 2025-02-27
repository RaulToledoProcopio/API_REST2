package com.es.aplicacion.service

import com.es.aplicacion.dto.ActualizarTareaDTO
import com.es.aplicacion.dto.NuevaTareaDTO
import com.es.aplicacion.dto.TareaDTO
import com.es.aplicacion.dto.TareaResumenDTO
import com.es.aplicacion.model.Tarea
import com.es.aplicacion.repository.TareaRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class TareaService(
    private val tareaRepository: TareaRepository
) {

    // Obtener el usuario autenticado
    private fun getUsuarioActual(): String {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return authentication.name // El username es el ID del usuario
    }

    // Convertir Tarea a DTO
    private fun tareaToDTO(tarea: Tarea): TareaDTO {
        return TareaDTO(
            id = tarea.id,
            titulo = tarea.titulo,
            descripcion = tarea.descripcion,
            username = tarea.username,
            completada = tarea.completada
        )
    }

    // **3. Crear una tarea (Usuario solo para sí mismo, ADMIN para cualquiera)**
    fun crearTarea(nuevaTareaDTO: NuevaTareaDTO, username: String? = null): TareaDTO {
        val usuarioActual = getUsuarioActual()

        // Si el usuario es un ADMIN, validamos que el username esté registrado en la base de datos
        val isAdmin = SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        // Si es un ADMIN, aseguramos que el username no sea nulo
        if (isAdmin && username == null) {
            throw AccessDeniedException("El nombre de usuario es obligatorio para un administrador")
        }

        // Si el usuario es un ADMIN, verificamos que el username esté registrado
        if (isAdmin) {
            val usuarioExistente = tareaRepository.findByUsername(username!!)
            if (usuarioExistente == null) {
                throw AccessDeniedException("El usuario especificado no existe")
            }
        }

        // Si el usuario es un USER, debe ser él mismo el que esté creando la tarea
        if (!isAdmin && username != usuarioActual) {
            throw AccessDeniedException("No puedes asignar tareas a otros usuarios")
        }

        // Crear la tarea
        val tarea = Tarea(
            titulo = nuevaTareaDTO.titulo,
            descripcion = nuevaTareaDTO.descripcion,
            username = username ?: usuarioActual  // Si es un USER, asigna su propio username
        )

        return tareaToDTO(tareaRepository.save(tarea))
    }


    fun eliminarTareaPorTitulo(titulo: String, username: String): TareaDTO {
        val usuarioActual = getUsuarioActual()

        // Si el usuario es un ADMIN, puede eliminar cualquier tarea
        val isAdmin = SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        // Si no es ADMIN, solo puede eliminar sus propias tareas
        if (!isAdmin) {
            val tareaExistente = tareaRepository.findByTitulo(titulo)
            if (tareaExistente == null || tareaExistente.username != username) {
                throw AccessDeniedException("No puedes eliminar tareas de otros usuarios")
            }
        }

        // Eliminar la tarea
        val tarea = tareaRepository.findByTitulo(titulo)
            ?: throw AccessDeniedException("Tarea no encontrada o no tienes permisos para eliminarla")

        tareaRepository.delete(tarea)

        return tareaToDTO(tarea)  // Convertir la tarea eliminada a DTO para devolverla
    }


    fun actualizarTareaPorTitulo(titulo: String, username: String, actualizarTareaDTO: ActualizarTareaDTO): TareaDTO {
        val tareaExistente = tareaRepository.findByTitulo(titulo)
            ?: throw AccessDeniedException("Tarea no encontrada o no tienes permisos para actualizarla")

        // Validamos que el usuario sea el propietario de la tarea
        if (tareaExistente.username != username) {
            throw AccessDeniedException("No puedes actualizar tareas de otros usuarios")
        }

        // Actualizar el estado de la tarea (marcar como completada)
        tareaExistente.completada = actualizarTareaDTO.completada

        // Guardar la tarea actualizada
        val tareaGuardada = tareaRepository.save(tareaExistente)

        return tareaToDTO(tareaGuardada)  // Convertir la tarea actualizada a DTO para devolverla
    }


    // **Obtener todas las tareas del usuario (filtradas por rol)**
    fun obtenerTareas(username: String): List<TareaResumenDTO> {
        val isAdmin = SecurityContextHolder.getContext().authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        return if (isAdmin) {
            // Si el usuario es ADMIN, devuelve todas las tareas
            tareaRepository.findAll().map { tarea ->
                TareaResumenDTO(
                    titulo = tarea.titulo,
                    descripcion = tarea.descripcion,
                    username = tarea.username
                )
            }
        } else {
            // Si el usuario es USER, solo puede ver sus propias tareas
            tareaRepository.findByUsername(username).map { tarea ->
                TareaResumenDTO(
                    titulo = tarea.titulo,
                    descripcion = tarea.descripcion,
                    username = tarea.username
                )
            }
        }
    }
}