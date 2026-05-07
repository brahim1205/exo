package com.gestionSchool.repository;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.model.Etudiant;
import com.gestionSchool.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InscriptionRepo extends JpaRepository<Inscription,Long> {
    Optional<Inscription> findByEtudiantAndAnneeAcademique(Etudiant etudiant, AnneeAcademique anneeAcademique);
}
