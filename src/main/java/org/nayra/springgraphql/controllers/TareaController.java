package org.nayra.springgraphql.controllers;

import org.nayra.springgraphql.entities.Tarea;
import org.nayra.springgraphql.repository.TareaRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

//El controller se encarga de comunicar el schema con el repository
@Controller
public class TareaController {

        //Declaramos el respository
        private final TareaRepository repo;

        //Inyeccion del repository por constructor
        public TareaController(TareaRepository repo) {
            this.repo = repo;
        }

        //Mapeamos las querys del schema con los métodos del repository
        @QueryMapping
        public Tarea tareaById(@Argument String id) {
            return repo.findById(id);
        }

        @QueryMapping
        public List<Tarea> allTareas() {
            return repo.findAll();
        }

        //Mapeamos las mutations del schema con las mutations del repository
        @MutationMapping
        public Tarea addTarea(@Argument String titulo, @Argument String descripcion) {
            return repo.save(new Tarea(null, titulo, descripcion, false));
        }

        @MutationMapping
        public Tarea marcarTareaHecha(@Argument String id) {
            Tarea t = repo.findById(id);
            if (t != null) {
                t.setHecha(true);
                repo.save(t);
            }
            return t;
        }
    }
