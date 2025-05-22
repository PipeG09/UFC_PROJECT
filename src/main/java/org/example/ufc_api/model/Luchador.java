package org.example.ufc_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Luchador")
public class Luchador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String nacionalidad;

    private Integer edad;

    @Column(name = "categoria_peso", length = 30)
    private String categoriaPeso;

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
}

