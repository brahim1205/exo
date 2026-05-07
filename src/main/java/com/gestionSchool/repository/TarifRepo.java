package com.gestionSchool.repository;

import com.gestionSchool.model.Classe;
import com.gestionSchool.model.Tarif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifRepo extends JpaRepository<Tarif, Long> {
    Optional<Tarif> findByClasse(Classe classe);
}
