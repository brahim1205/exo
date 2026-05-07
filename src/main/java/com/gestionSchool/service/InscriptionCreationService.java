package com.gestionSchool.service;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.model.Classe;
import com.gestionSchool.model.Etudiant;
import com.gestionSchool.model.Inscription;
import com.gestionSchool.model.Tarif;
import com.gestionSchool.repository.AnneeAcademiqueRepo;
import com.gestionSchool.repository.ClasseRepo;
import com.gestionSchool.repository.EtudiantRepo;
import com.gestionSchool.repository.InscriptionRepo;
import com.gestionSchool.repository.TarifRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class InscriptionCreationService {

    private final AnneeAcademiqueRepo anneeAcademiqueRepo;
    private final InscriptionRepo inscriptionRepo;
    private final EtudiantRepo etudiantRepo;
    private final ClasseRepo classeRepo;
    private final TarifRepo tarifRepo;

    public Inscription creer(LocalDate dateInscription, Long etudiantId, Long classeId, AnneeAcademique annee) {
        if (etudiantId == null || classeId == null || annee == null || annee.getId() == null) {
            throw new IllegalArgumentException("Parametres manquants.");
        }

        Etudiant etudiant = etudiantRepo.findById(etudiantId).orElse(null);
        Classe classe = classeRepo.findById(classeId).orElse(null);
        AnneeAcademique anneeAcademique = anneeAcademiqueRepo.findById(annee.getId()).orElse(null);

        if (etudiant == null) {
            throw new IllegalArgumentException("Etudiant introuvable.");
        }
        if (classe == null) {
            throw new IllegalArgumentException("Classe introuvable.");
        }
        if (anneeAcademique == null) {
            throw new IllegalArgumentException("Annee academique introuvable.");
        }

        Tarif tarif = tarifRepo.findByClasse(classe).orElse(null);
        if (tarif == null) {
            tarif = new Tarif();
            tarif.setClasse(classe);
            tarif.setMensualite(BigDecimal.ZERO);
            tarif.setAutresFrais(BigDecimal.ZERO);
            tarif = tarifRepo.save(tarif);
        }

        Inscription inscription = new Inscription();
        inscription.setDateInscription(dateInscription != null ? dateInscription : LocalDate.now());
        inscription.setEtudiant(etudiant);
        inscription.setClasse(classe);
        inscription.setAnneeAcademique(anneeAcademique);
        inscription.setTarif(tarif);

        return inscriptionRepo.save(inscription);
    }
}