package org.example.ufc_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Resultado")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pelea_id", unique = true, nullable = false)
    private Pelea pelea;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ganador_id", nullable = false)
    private Luchador ganador;

    @Column(nullable = false, length = 30)
    private String metodo;

    @Column(name = "round_final")
    private Integer roundFinal;

    @Column(name = "tiempo_final", length = 10)
    private String tiempoFinal;

    // ——— GETTERS Y SETTERS ———

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

    public Luchador getGanador() {
        return ganador;
    }
    public void setGanador(Luchador ganador) {
        this.ganador = ganador;
    }

    public String getMetodo() {
        return metodo;
    }
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public Integer getRoundFinal() {
        return roundFinal;
    }
    public void setRoundFinal(Integer roundFinal) {
        this.roundFinal = roundFinal;
    }

    public String getTiempoFinal() {
        return tiempoFinal;
    }
    public void setTiempoFinal(String tiempoFinal) {
        this.tiempoFinal = tiempoFinal;
    }
}
