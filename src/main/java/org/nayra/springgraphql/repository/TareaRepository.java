package org.nayra.springgraphql.repository;

import org.nayra.springgraphql.entities.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // No necesitas escribir nada aquí, JpaRepository ya trae findAll(), save(), etc.

    // Filtro que permite buscar tareas por estado (PENDIENTE, COMPLETADO...)
    List<Tarea> findByEstado(String estado);
}
