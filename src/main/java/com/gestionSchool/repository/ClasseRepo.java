package com.gestionSchool.repository;

import com.gestionSchool.model.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClasseRepo extends JpaRepository<Classe,Long> {
    Optional<Classe> findTopByOrderByCodeDesc();
    List<Classe> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);
    List<Classe> findByCodeContainingIgnoreCaseOrderByCodeAsc(String code);
}
