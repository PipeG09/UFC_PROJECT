package org.example.ufc_api.dto;

import java.time.LocalDateTime;

public class PeleaDto {
    private Long id;
    private Long eventoId;
    private Long luchadorAzulId;
    private Long luchadorRojoId;
    private LocalDateTime fecha;
    private Boolean finalizada;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getLuchadorAzulId() {
        return luchadorAzulId;
    }
    public void setLuchadorAzulId(Long luchadorAzulId) {
        this.luchadorAzulId = luchadorAzulId;
    }

    public Long getLuchadorRojoId() {
        return luchadorRojoId;
    }
    public void setLuchadorRojoId(Long luchadorRojoId) {
        this.luchadorRojoId = luchadorRojoId;
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
