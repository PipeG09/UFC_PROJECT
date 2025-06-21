package org.example.ufc_api.repository;

import org.example.ufc_api.model.Pelea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PeleaRepository extends JpaRepository<Pelea, Long> {
    List<Pelea> findByFinalizadaFalse();
    List<Pelea> findByFinalizadaTrue();
}
