package com.gestionSchool.service;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.repository.AnneeAcademiqueRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnneeAcademiqueManagementService {

    private final AnneeAcademiqueRepo anneeAcademiqueRepo;
    private final AnneeAcademiqueWorkflowService workflowService;

    public List<AnneeAcademique> lister() {
        return anneeAcademiqueRepo.findAll();
    }

    public AnneeAcademique publier(Long id) {
        AnneeAcademique annee = anneeAcademiqueRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annee academique introuvable."));

        workflowService.publier(annee);
        return updateStatut(annee, "Publication impossible: mise a jour non effectuee.");
    }

    public AnneeAcademique ouvrirInscriptions(Long id) {
        AnneeAcademique annee = anneeAcademiqueRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annee academique introuvable."));

        workflowService.ouvrirInscriptions(annee);
        return updateStatut(annee, "Ouverture impossible: mise a jour non effectuee.");
    }

    public AnneeAcademique suspendreInscriptions(Long id) {
        AnneeAcademique annee = anneeAcademiqueRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annee academique introuvable."));

        workflowService.suspendreInscriptions(annee);
        return updateStatut(annee, "Suspension impossible: mise a jour non effectuee.");
    }

    public AnneeAcademique reouvrirInscriptions(Long id) {
        AnneeAcademique annee = anneeAcademiqueRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annee academique introuvable."));

        workflowService.reouvrirInscriptions(annee);
        return updateStatut(annee, "Reouverture impossible: mise a jour non effectuee.");
    }

    public AnneeAcademique cloturer(Long id) {
        AnneeAcademique annee = anneeAcademiqueRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annee academique introuvable."));

        workflowService.cloturer(annee);
        return updateStatut(annee, "Cloture impossible: mise a jour non effectuee.");
    }

    private AnneeAcademique updateStatut(AnneeAcademique annee, String message) {
        if (annee.getStatut() == null) {
            throw new IllegalStateException("Le statut de l'annee academique est obligatoire.");
        }
        try {
            return anneeAcademiqueRepo.save(annee);
        } catch (Exception e) {
            throw new IllegalStateException(message, e);
        }
    }
}
