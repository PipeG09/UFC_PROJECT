package org.example.ufc_api.repository;

import org.example.ufc_api.model.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {
    List<Estadistica> findByPeleaIdAndLuchadorIdOrderByTimestampDesc(Long peleaId, Long luchadorId);
    List<Estadistica> findByPeleaId(Long peleaId);
}
