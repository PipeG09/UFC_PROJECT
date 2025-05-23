package org.example.ufc_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Estadistica")
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pelea_id", nullable = false)
    private Pelea pelea;

    @ManyToOne(optional = false)
    @JoinColumn(name = "luchador_id", nullable = false)
    private Luchador luchador;

    @Column(name = "round", nullable = false)
    private Integer round;

    @Column(name = "golpes_conectados", nullable = false)
    private Integer golpesConectados = 0;

    @Column(name = "derribos", nullable = false)
    private Integer derribos = 0;

    @Column(name = "control_jaula_segundos", nullable = false)
    private Integer controlJaulaSegundos = 0;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Pelea getPelea() {
        return pelea;
    }
    public void setPelea(Pelea pelea) {
        this.pelea = pelea;
    }

    public Luchador getLuchador() {
        return luchador;
    }
    public void setLuchador(Luchador luchador) {
        this.luchador = luchador;
    }

    public Integer getRound() {
        return round;
    }
    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getGolpesConectados() {
        return golpesConectados;
    }
    public void setGolpesConectados(Integer golpesConectados) {
        this.golpesConectados = golpesConectados;
    }

    public Integer getDerribos() {
        return derribos;
    }
    public void setDerribos(Integer derribos) {
        this.derribos = derribos;
    }

    public Integer getControlJaulaSegundos() {
        return controlJaulaSegundos;
    }
    public void setControlJaulaSegundos(Integer controlJaulaSegundos) {
        this.controlJaulaSegundos = controlJaulaSegundos;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
