package com.gestionSchool.service;


import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.repository.AnneeAcademiqueJpaRepo;
import com.gestionSchool.repository.AnneeAcademiqueRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnneeAcademiqueCreationService {

    private final AnneeAcademiqueWorkflowService workflowService;
    private final AnneeAcademiqueRepo anneeAcademiqueJpaRepo;

    public AnneeAcademique creer(AnneeAcademique annee) {
        valider(annee);
        annee.setCode(genererCode(annee));
        if (anneeAcademiqueJpaRepo.existsByCode(annee.getCode())) {
            throw new IllegalArgumentException("Ce code d'annee academique existe deja.");
        }
        AnneeAcademique anneeEnBrouillon = workflowService.creerNouvelleAnnee(annee);
        appliquerOuvertureAuto(anneeEnBrouillon);
        return anneeAcademiqueJpaRepo.save(anneeEnBrouillon);
    }

    private void valider(AnneeAcademique annee) {
        if (annee == null) {
            throw new IllegalArgumentException("L'annee academique est obligatoire.");
        }
        if (annee.getDateOuverture() == null || annee.getDateDebutInscription() == null || annee.getDateFinInscription() == null) {
            throw new IllegalArgumentException("Les dates d'ouverture et d'inscription sont obligatoires.");
        }
        if (annee.getDateDebutInscription().isAfter(annee.getDateFinInscription())) {
            throw new IllegalArgumentException("La date de debut des inscriptions doit etre avant la date de fin.");
        }
        if (annee.getDateOuverture().isAfter(annee.getDateDebutInscription())) {
            throw new IllegalArgumentException("La date d'ouverture doit etre avant la date de debut des inscriptions.");
        }
    }

    private String genererCode(AnneeAcademique annee) {
        int anneeDebut = annee.getDateDebutInscription().getYear();
        int anneeFin = annee.getDateFinInscription().getYear();
        return anneeDebut + "-" + anneeFin;
    }

    private void appliquerOuvertureAuto(AnneeAcademique annee) {
        if (annee.getDateOuverture() != null
                && annee.getDateDebutInscription() != null
                && annee.getDateOuverture().isEqual(java.time.LocalDate.now())
                && annee.getDateDebutInscription().isEqual(java.time.LocalDate.now())) {
            workflowService.publier(annee);
            workflowService.ouvrirInscriptions(annee);
        }
    }
}
