package org.nayra.springgraphql.repository;

import org.nayra.springgraphql.entities.Tarea;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//Este es el repositorio en el que guardamos
//los datos en memoria, ya que no tenemos bbdd
@Repository
public class TareaRepository {

        //Almacenamos los datos en memoria porque no tenemos bbdd
        //ConcurrentHashMap evita problemas si varios usuarios acceden a la vez a las tareas
        private final Map<String, Tarea> tareas = new ConcurrentHashMap<>();


        public TareaRepository() {
            //Crea dos tareas
            save(new Tarea(null, "Estudiar GraphQL", "Repasar esquema y queries", false));
            save(new Tarea(null, "Preparar presentación", "Diapositivas de Spring", false));
        }


        //Método para buscar tareas por su id
        public Tarea findById(String id) {
            return tareas.get(id);
        }

        //Método para listar todas las tareas
        public List<Tarea> findAll() {
            return new ArrayList<>(tareas.values());
        }

        //Método para guaradar una tarea
        public Tarea save(Tarea tarea) {
            //Si la tarea no tiene id, significa que es una tara nueva
            if (tarea.getId() == null) {
                //Así que genera un id
                tarea.setId(UUID.randomUUID().toString());
            }
            //Guarda la tarea
            tareas.put(tarea.getId(), tarea);
            return tarea;
        }
}
