package org.example.ufc_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "probabilidad")
public class Probabilidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pelea_id", nullable = false)
    private Pelea pelea;

    @ManyToOne(optional = false)
    @JoinColumn(name = "luchador_id", nullable = false)
    private Luchador luchador;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal probabilidad;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pelea getPelea() { return pelea; }
    public void setPelea(Pelea pelea) { this.pelea = pelea; }

    public Luchador getLuchador() { return luchador; }
    public void setLuchador(Luchador luchador) { this.luchador = luchador; }

    public BigDecimal getProbabilidad() { return probabilidad; }
    public void setProbabilidad(BigDecimal probabilidad) { this.probabilidad = probabilidad; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
