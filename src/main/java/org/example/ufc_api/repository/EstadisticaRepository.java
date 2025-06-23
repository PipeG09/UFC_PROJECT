package org.example.ufc_api.repository;

import org.example.ufc_api.model.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {

    /**
     * Buscar estadísticas por pelea y luchador, ordenadas por timestamp descendente
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pelea.id = :peleaId AND e.luchador.id = :luchadorId ORDER BY e.timestamp DESC")
    List<Estadistica> findByPeleaIdAndLuchadorIdOrderByTimestampDesc(
            @Param("peleaId") Long peleaId,
            @Param("luchadorId") Long luchadorId);

    /**
     * Buscar todas las estadísticas de una pelea
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pelea.id = :peleaId ORDER BY e.timestamp DESC")
    List<Estadistica> findByPeleaId(@Param("peleaId") Long peleaId);

    /**
     * Buscar estadísticas más recientes por pelea y luchador (solo la última entrada)
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pelea.id = :peleaId AND e.luchador.id = :luchadorId ORDER BY e.timestamp DESC LIMIT 1")
    Estadistica findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(
            @Param("peleaId") Long peleaId,
            @Param("luchadorId") Long luchadorId);

    /**
     * Buscar estadísticas por round específico
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pelea.id = :peleaId AND e.round = :round ORDER BY e.timestamp DESC")
    List<Estadistica> findByPeleaIdAndRoundOrderByTimestampDesc(
            @Param("peleaId") Long peleaId,
            @Param("round") Integer round);

    /**
     * Buscar estadísticas totales por luchador en una pelea (sumadas)
     */
    @Query("SELECT COALESCE(SUM(e.golpesConectados), 0) as golpes, " +
            "COALESCE(SUM(e.derribos), 0) as derribos, " +
            "COALESCE(SUM(e.controlJaulaSegundos), 0) as control, " +
            "COALESCE(MAX(e.round), 1) as maxRound " +
            "FROM Estadistica e WHERE e.pelea.id = :peleaId AND e.luchador.id = :luchadorId")
    Object[] findTotalesByPeleaIdAndLuchadorId(
            @Param("peleaId") Long peleaId,
            @Param("luchadorId") Long luchadorId);
}