package org.example.ufc_api.repository;

import org.example.ufc_api.model.Pelea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeleaRepository extends JpaRepository<Pelea, Long> {
    List<Pelea> findByFinalizadaFalse();
    List<Pelea> findByFinalizadaTrue();

    // NUEVO: Buscar peleas por evento ID
    @Query("SELECT p FROM Pelea p WHERE p.evento.id = :eventoId")
    List<Pelea> findByEventoId(@Param("eventoId") Long eventoId);

    // NUEVO: Buscar peleas por evento ID ordenadas por fecha
    @Query("SELECT p FROM Pelea p WHERE p.evento.id = :eventoId ORDER BY p.fecha ASC")
    List<Pelea> findByEventoIdOrderByFecha(@Param("eventoId") Long eventoId);
}