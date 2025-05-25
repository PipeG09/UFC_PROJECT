package org.example.ufc_api.dto;

import java.time.LocalDateTime;

public class EstadisticaDto {
    private Long id;
    private Long peleaId;
    private Long luchadorId;
    private Integer round;
    private Integer golpesConectados;
    private Integer derribos;
    private Integer controlJaulaSegundos;
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeleaId() {
        return peleaId;
    }
    public void setPeleaId(Long peleaId) {
        this.peleaId = peleaId;
    }

    public Long getLuchadorId() {
        return luchadorId;
    }
    public void setLuchadorId(Long luchadorId) {
        this.luchadorId = luchadorId;
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
