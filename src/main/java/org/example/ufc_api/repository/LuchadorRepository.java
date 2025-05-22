package org.example.ufc_api.repository;

import org.example.ufc_api.model.Luchador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuchadorRepository extends JpaRepository<Luchador, Long> {
}
