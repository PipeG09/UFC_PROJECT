package org.example.ufc_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProbabilidadDto {
    private Long id;
    private Long peleaId;
    private Long luchadorId;
    private BigDecimal probabilidad;
    private LocalDateTime timestamp;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPeleaId() { return peleaId; }
    public void setPeleaId(Long peleaId) { this.peleaId = peleaId; }

    public Long getLuchadorId() { return luchadorId; }
    public void setLuchadorId(Long luchadorId) { this.luchadorId = luchadorId; }

    public BigDecimal getProbabilidad() { return probabilidad; }
    public void setProbabilidad(BigDecimal probabilidad) { this.probabilidad = probabilidad; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

