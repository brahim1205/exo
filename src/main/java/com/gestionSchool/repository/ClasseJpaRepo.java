package com.gestionSchool.repository;

public class ClasseJpaRepo {

   /* public Optional<Classe> findById(Long id) {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return Optional.ofNullable(entityManager.find(Classe.class, id));
        } finally {
            entityManager.close();
        }
    }

    public List<Classe> findAll() {
        EntityManager entityManager = JpaEntityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery(
                            "select c from Classe c order by c.code asc", Classe.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }*/
}
