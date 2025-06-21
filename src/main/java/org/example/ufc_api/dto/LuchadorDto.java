package org.example.ufc_api.dto;

public class LuchadorDto {
    private Long id;
    private String nombre;
    private String nacionalidad;
    private Integer edad;
    private String categoriaPeso;
    private String historial; // NUEVO CAMPO

    // ——— GETTERS Y SETTERS ———

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Integer getEdad() {
        return edad;
    }
    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getCategoriaPeso() {
        return categoriaPeso;
    }
    public void setCategoriaPeso(String categoriaPeso) {
        this.categoriaPeso = categoriaPeso;
    }

    public String getHistorial() {
        return historial;
    }
    public void setHistorial(String historial) {
        this.historial = historial;
    }
}