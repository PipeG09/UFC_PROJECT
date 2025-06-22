package org.example.ufc_api.repository;

import org.example.ufc_api.model.Pelea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PeleaRepository extends JpaRepository<Pelea, Long> {

    // Peleas no finalizadas (incluye futuras y en vivo)
    List<Pelea> findByFinalizadaFalse();

    // Peleas finalizadas
    List<Pelea> findByFinalizadaTrue();

    // NUEVO: Peleas realmente EN VIVO (no finalizadas Y fecha <= ahora)
    @Query("SELECT p FROM Pelea p WHERE p.finalizada = false AND p.fecha <= :now")
    List<Pelea> findLiveFights(@Param("now") LocalDateTime now);

    // NUEVO: Peleas FUTURAS (no finalizadas Y fecha > ahora)
    @Query("SELECT p FROM Pelea p WHERE p.finalizada = false AND p.fecha > :now")
    List<Pelea> findUpcomingFights(@Param("now") LocalDateTime now);

    // NUEVO: Peleas por evento
    @Query("SELECT p FROM Pelea p WHERE p.evento.id = :eventoId ORDER BY p.fecha ASC")
    List<Pelea> findByEventoId(@Param("eventoId") Long eventoId);
}