package com.gestionSchool.service;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.model.StatutAnneeAcademique;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnneeAcademiqueWorkflowService {

    public AnneeAcademique creerNouvelleAnnee(AnneeAcademique annee) {
        if (annee == null) {
            throw new IllegalArgumentException("L'annee academique est obligatoire.");
        }
        annee.setStatut(StatutAnneeAcademique.BROUILLON);
        return annee;
    }

    public void publier(AnneeAcademique annee) {
        verifierTransition(annee, StatutAnneeAcademique.BROUILLON, "Publication impossible");
        annee.setStatut(StatutAnneeAcademique.PUBLIEE);
    }

    public void ouvrirInscriptions(AnneeAcademique annee) {
        verifierTransition(annee, StatutAnneeAcademique.PUBLIEE, "Ouverture des inscriptions impossible");
        annee.setStatut(StatutAnneeAcademique.INSCRIPTIONS_OUVERTES);
    }

    public void suspendreInscriptions(AnneeAcademique annee) {
        verifierTransition(annee, StatutAnneeAcademique.INSCRIPTIONS_OUVERTES, "Suspension des inscriptions impossible");
        annee.setStatut(StatutAnneeAcademique.INSCRIPTIONS_SUSPENDUES);
    }

    public void reouvrirInscriptions(AnneeAcademique annee) {
        verifierTransition(annee, StatutAnneeAcademique.INSCRIPTIONS_SUSPENDUES, "Reouverture des inscriptions impossible");
        annee.setStatut(StatutAnneeAcademique.INSCRIPTIONS_OUVERTES);
    }

    public void cloturer(AnneeAcademique annee) {
        verifierAnnee(annee);
        if (annee.getStatut() != StatutAnneeAcademique.INSCRIPTIONS_OUVERTES
                && annee.getStatut() != StatutAnneeAcademique.INSCRIPTIONS_SUSPENDUES) {
            throw new IllegalStateException("Cloture impossible (statut actuel: " + annee.getStatut() + ").");
        }
        annee.setStatut(StatutAnneeAcademique.ANNEE_CLOTUREE);
    }

    public boolean modificationAutorisee(AnneeAcademique annee) {
        verifierAnnee(annee);
        return annee.getStatut() == StatutAnneeAcademique.BROUILLON;
    }

    public void verifierModificationAutorisee(AnneeAcademique annee) {
        if (!modificationAutorisee(annee)) {
            throw new IllegalStateException("Modification interdite: l'annee est figee apres publication.");
        }
    }

    private void verifierTransition(AnneeAcademique annee, StatutAnneeAcademique statutAttendu, String message) {
        verifierAnnee(annee);
        if (annee.getStatut() != statutAttendu) {
            throw new IllegalStateException(message + " (statut actuel: " + annee.getStatut() + ").");
        }
    }

    private void verifierAnnee(AnneeAcademique annee) {
        if (annee == null) {
            throw new IllegalArgumentException("L'annee academique est obligatoire.");
        }
        if (annee.getStatut() == null) {
            throw new IllegalStateException("Le statut de l'annee academique est obligatoire.");
        }
    }
}
