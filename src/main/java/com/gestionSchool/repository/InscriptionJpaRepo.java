package com.gestionSchool.repository;


public class InscriptionJpaRepo {
/*

    public boolean existsByEtudiantAndAnnee(Long etudiantId, Long anneeId) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            Long count = entityManager.createQuery(
                            "select count(i) from Inscription i " +
                                    "where i.etudiant.id = :etudiantId and i.anneeAcademique.id = :anneeId",
                            Long.class)
                    .setParameter("etudiantId", etudiantId)
                    .setParameter("anneeId", anneeId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            entityManager.close();
        }
    }

    public Inscription save(LocalDate dateInscription, Long etudiantId, Long classeId, Long anneeId) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Etudiant etudiant = entityManager.find(Etudiant.class, etudiantId);
            Classe classe = entityManager.find(Classe.class, classeId);
            AnneeAcademique annee = entityManager.find(AnneeAcademique.class, anneeId);

            if (etudiant == null) {
                throw new IllegalArgumentException("Etudiant introuvable: " + etudiantId);
            }
            if (classe == null) {
                throw new IllegalArgumentException("Classe introuvable: " + classeId);
            }
            if (annee == null) {
                throw new IllegalArgumentException("Annee academique introuvable: " + anneeId);
            }

            Inscription inscription = new Inscription();
            inscription.setDateInscription(dateInscription);
            inscription.setEtudiant(etudiant);
            inscription.setClasse(classe);
            inscription.setAnneeAcademique(annee);

            entityManager.persist(inscription);
            transaction.commit();
            return inscription;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }
*/
}
