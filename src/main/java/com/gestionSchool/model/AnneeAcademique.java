package com.gestionSchool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "annee_academique")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnneeAcademique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDate dateOuverture;

    @Column(name = "date_debut_inscription", nullable = false)
    private LocalDate dateDebutInscription;

    @Column(name = "date_fin_inscription", nullable = false)
    private LocalDate dateFinInscription;

    @Column(name = "date_fermeture", nullable = false)
    private LocalDate dateFermeture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatutAnneeAcademique statut;

    @PrePersist
    public void prePersist() {
        if (dateFermeture == null) {
            dateFermeture = dateFinInscription;
        }
        appliquerCodeAuto();
        if (statut == null) {
            statut = StatutAnneeAcademique.BROUILLON;
        }
    }

    @PreUpdate
    public void preUpdate() {
        appliquerCodeAuto();
    }

    private void appliquerCodeAuto() {
        if (dateDebutInscription != null && dateFinInscription != null) {
            int anneeDebut = dateDebutInscription.getYear();
            int anneeFin = dateFinInscription.getYear();
            code = anneeDebut + "-" + anneeFin;
        }
    }

    @Override
    public String toString() {
        return "AnneeAcademique{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", dateOuverture=" + dateOuverture +
                ", dateDebutInscription=" + dateDebutInscription +
                ", dateFinInscription=" + dateFinInscription +
                ", statut=" + statut +
                '}';
    }
}
