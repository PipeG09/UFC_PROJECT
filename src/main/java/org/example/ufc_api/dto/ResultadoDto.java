package org.example.ufc_api.dto;

public class ResultadoDto {
    private Long id;
    private Long peleaId;
    private Long ganadorId;
    private String metodo;
    private Integer roundFinal;
    private String tiempoFinal;

    // ——— GETTERS Y SETTERS ———

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

    public Long getGanadorId() {
        return ganadorId;
    }
    public void setGanadorId(Long ganadorId) {
        this.ganadorId = ganadorId;
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
