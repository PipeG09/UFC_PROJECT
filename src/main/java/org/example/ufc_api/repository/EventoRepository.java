package org.example.ufc_api.repository;

import org.example.ufc_api.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}

