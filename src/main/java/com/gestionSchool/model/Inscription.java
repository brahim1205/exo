package com.gestionSchool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "inscription")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tarif_id", nullable = false)
    private Tarif tarif;

    @Override
    public String toString() {
        return "Inscription{" +
                "id=" + id +
                ", dateInscription=" + dateInscription +
                ", etudiant=" + (etudiant != null ? etudiant.getId() : null) +
                ", classe=" + (classe != null ? classe.getId() : null) +
                ", anneeAcademique=" + (anneeAcademique != null ? anneeAcademique.getId() : null) +
                ", tarif=" + (tarif != null ? tarif.getId() : null) +
                '}';
    }
}
