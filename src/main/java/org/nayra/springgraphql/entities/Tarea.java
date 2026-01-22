package org.nayra.springgraphql.entities;

import jakarta.persistence.Entity; // <--- ESTE IMPORT ES EL QUE FALTA
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // <--- ESTA ANOTACIÓN LE DICE A SPRING: "ESTO ES UNA TABLA"
public class Tarea {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incremental
    private Long id;

    private String titulo;
    private String descripcion;
    private String estado; 

    // Constructor vacío y Getters/Setters
    public Tarea() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}