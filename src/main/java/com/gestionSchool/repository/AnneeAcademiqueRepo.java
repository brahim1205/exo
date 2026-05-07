package com.gestionSchool.repository;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.model.StatutAnneeAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnneeAcademiqueRepo extends JpaRepository<AnneeAcademique,Long>  {
    boolean existsByCode(String code);

    @Query("select a from AnneeAcademique a " +
            "where a.statut = :statut " +
            "and a.dateOuverture <= :date " +
            "and :date between a.dateDebutInscription and a.dateFinInscription " +
            "order by a.dateDebutInscription desc")
    List<AnneeAcademique> findOpenForDate(@Param("date") LocalDate date,
                                          @Param("statut") StatutAnneeAcademique statut);

    List<AnneeAcademique> findByStatut(StatutAnneeAcademique statut);
}
