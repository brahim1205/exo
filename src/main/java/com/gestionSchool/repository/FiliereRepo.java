package com.gestionSchool.repository;

import com.gestionSchool.model.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FiliereRepo extends JpaRepository<Filiere,Long> {
    Optional<Filiere> findTopByOrderByCodeDesc();
}
