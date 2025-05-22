package org.example.ufc_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pelea")
public class Pelea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "luchador_azul_id", nullable = false)
    private Luchador azul;

    @ManyToOne(optional = false)
    @JoinColumn(name = "luchador_rojo_id", nullable = false)
    private Luchador rojo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Boolean finalizada = false;

    // ——— GETTERS Y SETTERS ———

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }
    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Luchador getAzul() {
        return azul;
    }
    public void setAzul(Luchador azul) {
        this.azul = azul;
    }

    public Luchador getRojo() {
        return rojo;
    }
    public void setRojo(Luchador rojo) {
        this.rojo = rojo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Boolean getFinalizada() {
        return finalizada;
    }
    public void setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
    }
}
