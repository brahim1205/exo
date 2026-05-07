package com.gestionSchool.repository;


public class AnneeAcademiqueJpaRepo {
/*

    public AnneeAcademique save(AnneeAcademique annee) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(annee);
            transaction.commit();
            return annee;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public boolean existsByCode(String code) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            Long count = entityManager.createQuery(
                            "select count(a) from AnneeAcademique a where a.code = :code", Long.class)
                    .setParameter("code", code)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            entityManager.close();
        }
    }

    public List<AnneeAcademique> findAll() {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery(
                            "select a from AnneeAcademique a order by a.id desc", AnneeAcademique.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Optional<AnneeAcademique> findById(Long id) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(AnneeAcademique.class, id));
        } finally {
            entityManager.close();
        }
    }

    public boolean updateStatut(Long id, StatutAnneeAcademique statut) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            AnneeAcademique annee = entityManager.find(AnneeAcademique.class, id);
            if (annee == null) {
                transaction.rollback();
                return false;
            }
            annee.setStatut(statut);
            transaction.commit();
            return true;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public List<AnneeAcademique> findOpenForDate(LocalDate date) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery(
                            "select a from AnneeAcademique a " +
                                    "where a.statut = :statut " +
                                    "and a.dateOuverture <= :date " +
                                    "and :date between a.dateDebutInscription and a.dateFinInscription " +
                                    "order by a.dateDebutInscription desc", AnneeAcademique.class)
                    .setParameter("statut", StatutAnneeAcademique.INSCRIPTIONS_OUVERTES)
                    .setParameter("date", date)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }
*/
}
