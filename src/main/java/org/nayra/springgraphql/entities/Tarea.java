package org.nayra.springgraphql.entities;

//Esta clase hace referencia a la entidad de Tarea

public class Tarea {
        private String id;
        private String titulo;
        private String descripcion;
        private boolean hecha;

    //Constructores
    public Tarea() {
    }

    public Tarea(String id, String titulo, String descripcion, boolean hecha) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.hecha = hecha;
    }

    //Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isHecha() {
        return hecha;
    }

    public void setHecha(boolean hecha) {
        this.hecha = hecha;
    }
}
