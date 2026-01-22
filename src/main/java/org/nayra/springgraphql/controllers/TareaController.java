package org.nayra.springgraphql.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.nayra.springgraphql.entities.Tarea;
import org.nayra.springgraphql.repository.TareaRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

// Esta clase actua como resolver
@Controller
public class TareaController {

    // Lista que guarda las tareas en local, alternativa a la bbdd
    private List<Tarea> tareasLocales = new ArrayList<>();

    private final TareaRepository tareaRepository;

    // Esto sirve para enviar tareas en tiempo real
    // many() --> muchos eventos
    // replay().all() --> siempre vivo, repite historial a reconexiones
    // GraphiQL para/arranca = funciona siempre, no hace falta cerrar y abrir la app
    private final Sinks.Many<Tarea> tareaSink = Sinks.many()
            .replay()
            .all();

    // Inyección del repositorio por constructor
    public TareaController(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    // QUERIES
    @QueryMapping
    // Obtener todas las tareas (con filtro de estado)
    public List<Tarea> allTareas(@Argument String estado, @Argument boolean desdeBBDD) {
        if (desdeBBDD) { // Desde la bbdd
            System.out.println("Consultando MySQL " + (estado != null ? "estado=" + estado : "TODAS"));
            return estado == null ? tareaRepository.findAll() // Devuelve todas las tareas
                    : tareaRepository.findByEstado(estado); //Devuelve solo las tareas con ese estado
        } else { // O desde la memoria local
            // Si el estado es null, muestra todas las tareas
            // Sino, muestra las tareas con X estado
            System.out.println("Consultando local " + (estado != null ? "estado=" + estado : "TODAS") + " (" + tareasLocales.size() + ")");
            if (estado == null) return new ArrayList<>(tareasLocales);
            // Filtramos por estado para encontrar las que coincidan con el estado que buscamos
            return tareasLocales.stream()
                    .filter(t -> t.getEstado().equals(estado))
                    .collect(Collectors.toList());
        }
    }

    @QueryMapping
    // Buscar tarea por id
    public Tarea tareaById(@Argument Long id, @Argument boolean desdeBBDD) {
        if (desdeBBDD) { // Desde la bbdd
            Optional<Tarea> tarea = tareaRepository.findById(id);
            System.out.println("Buscando ID " + id + " en MySQL: " + (tarea.isPresent() ? "ENCONTRADA" : "NO ENCONTRADA"));
            return tarea.orElse(null);
        } else { // O desde la memoria local
            // Filtramos por ID para encontrar la primera tarea que coincida
            Optional<Tarea> tarea = tareasLocales.stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst();
            System.out.println("Buscando ID " + id + " en local: " + (tarea.isPresent() ? "ENCONTRADA" : "NO ENCONTRADA"));
            return tarea.orElse(null);
        }
    }

    // MUTATIONS
    @MutationMapping
    // Añadir una nueva tarea
    public Tarea addTarea(@Argument String titulo, @Argument String descripcion, @Argument boolean guardarEnBBDD) {
        //Datos de la tarea
        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setTitulo(titulo);
        nuevaTarea.setDescripcion(descripcion != null ? descripcion : "");
        nuevaTarea.setEstado("PENDIENTE");

        Tarea tareaGuardada;

        if (guardarEnBBDD) { // Se guarda en la bbdd
            System.out.println("GUARDANDO en MySQL: " + titulo);
            tareaGuardada = tareaRepository.save(nuevaTarea);
        } else { // Se guarda en la memoria local
            nuevaTarea.setId((long) (tareasLocales.size() + 1));
            tareasLocales.add(nuevaTarea);
            tareaGuardada = nuevaTarea;
            System.out.println("GUARDADO en lista local: " + titulo);
        }

        // DISPARAR SUBSCRIPTION
        // Envía la tarea en tiempo real a todos los clientes suscritos
        System.out.println("SUBSCRIPTION: Enviando tarea '" + titulo + "' a clientes conectados");
        tareaSink.tryEmitNext(tareaGuardada);

        return tareaGuardada;
    }

    @MutationMapping
    // Cambiar el estado de una tarea
    public Tarea cambiarEstado(@Argument Long id, @Argument String estado, @Argument boolean enBBDD) {
        if (enBBDD) { // En la bbdd
            Optional<Tarea> optTarea = tareaRepository.findById(id);
            if (optTarea.isPresent()) {
                Tarea tarea = optTarea.get();
                tarea.setEstado(estado);
                System.out.println("MySQL: Cambiado estado ID " + id + " a '" + estado + "'");
                return tareaRepository.save(tarea);
            }
        } else { // O en memoria local
            Optional<Tarea> optTarea = tareasLocales.stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst();
            if (optTarea.isPresent()) {
                Tarea tarea = optTarea.get();
                tarea.setEstado(estado);
                System.out.println("Local: Cambiado estado ID " + id + " a '" + estado + "'");
                return tarea;
            }
        }
        // Salta mensaje de error si la tarea no existe
        System.out.println("ERROR: Tarea ID " + id + " no encontrada");
        return null;
    }

    @MutationMapping
    // Eliminar tarea por ID
    public Boolean eliminarTarea(@Argument Long id, @Argument boolean enBBDD) {
        if (enBBDD) { // En la bbdd
            // Si existe se elimina
            if (tareaRepository.existsById(id)) {
                tareaRepository.deleteById(id);
                System.out.println("MySQL: Eliminada ID " + id);
                return true;
            }
        } else { // O en memoria local
            boolean borrada = tareasLocales.removeIf(t -> t.getId().equals(id));
            if (borrada) System.out.println("Local: Eliminada ID " + id);
            return borrada;
        }
        // Mensaje si no se encuentra la tarea que se quiere borrar
        System.out.println("ID " + id + " no encontrada");
        return false;
    }


    // SUBSCRIPTION (WebSocket)
    @SubscriptionMapping

    public Flux<Tarea> tareaAnadida() {
        System.out.println("SUBSCRIPTION: Nuevo cliente conectado!");
        // Se convierte en flujo reactivo, cada nueva tarea se envía al cliente
        return tareaSink.asFlux();
    }
}
