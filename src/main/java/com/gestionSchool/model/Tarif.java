package com.gestionSchool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tarif")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tarif {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "classe_id", nullable = false, unique = true)
    private Classe classe;

    @Column(nullable = false)
    private BigDecimal mensualite;

    @Column(nullable = false)
    private BigDecimal autresFrais;

    @Override
    public String toString() {
        return "Tarif{" +
                "id=" + id +
                ", classe=" + (classe != null ? classe.getId() : null) +
                ", mensualite=" + mensualite +
                ", autresFrais=" + autresFrais +
                '}';
    }
}
