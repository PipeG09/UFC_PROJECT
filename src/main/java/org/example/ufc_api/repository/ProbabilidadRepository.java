package org.example.ufc_api.repository;

import org.example.ufc_api.model.Probabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProbabilidadRepository extends JpaRepository<Probabilidad, Long> {

    /**
     * Buscar la probabilidad más reciente por pelea y luchador
     */
    @Query("SELECT p FROM Probabilidad p WHERE p.pelea.id = :peleaId AND p.luchador.id = :luchadorId ORDER BY p.timestamp DESC LIMIT 1")
    Probabilidad findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(
            @Param("peleaId") Long peleaId,
            @Param("luchadorId") Long luchadorId);

    /**
     * Buscar todas las probabilidades de una pelea ordenadas por timestamp
     */
    @Query("SELECT p FROM Probabilidad p WHERE p.pelea.id = :peleaId ORDER BY p.timestamp DESC")
    List<Probabilidad> findByPeleaIdOrderByTimestampDesc(@Param("peleaId") Long peleaId);

    /**
     * Buscar probabilidades más recientes de ambos luchadores en una pelea
     */
    @Query("SELECT p FROM Probabilidad p WHERE p.pelea.id = :peleaId " +
            "AND p.id IN (SELECT MAX(p2.id) FROM Probabilidad p2 WHERE p2.pelea.id = :peleaId GROUP BY p2.luchador.id) " +
            "ORDER BY p.luchador.id")
    List<Probabilidad> findLatestProbabilitiesByPeleaId(@Param("peleaId") Long peleaId);

    /**
     * Buscar todas las probabilidades de un luchador en una pelea
     */
    @Query("SELECT p FROM Probabilidad p WHERE p.pelea.id = :peleaId AND p.luchador.id = :luchadorId ORDER BY p.timestamp ASC")
    List<Probabilidad> findByPeleaIdAndLuchadorIdOrderByTimestamp(
            @Param("peleaId") Long peleaId,
            @Param("luchadorId") Long luchadorId);
}