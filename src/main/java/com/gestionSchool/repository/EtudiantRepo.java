package com.gestionSchool.repository;

import com.gestionSchool.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EtudiantRepo extends JpaRepository<Etudiant,Long> {
    Optional<Etudiant> findTopByOrderByMatriculeDesc();
    Optional<Etudiant> findByMatricule(String matricule);
    List<Etudiant> findByMatriculeContainingIgnoreCaseOrderByMatriculeAsc(String matricule);
}
