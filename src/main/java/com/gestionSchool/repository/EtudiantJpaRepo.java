package com.gestionSchool.repository;


public class EtudiantJpaRepo {

   /* public Optional<Etudiant> findById(Long id) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(Etudiant.class, id));
        } finally {
            entityManager.close();
        }
    }

    public Optional<Etudiant> findByMatricule(String matricule) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery(
                            "select e from Etudiant e where e.matricule = :matricule", Etudiant.class)
                    .setParameter("matricule", matricule)
                    .getResultStream()
                    .findFirst();
        } finally {
            entityManager.close();
        }
    }*/
}
