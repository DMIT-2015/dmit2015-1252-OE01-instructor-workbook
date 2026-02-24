package dmit2015.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StudentInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {

        long studentCount = entityManager
                .createQuery("select count(s) from Student s", Long.class)
                .getSingleResult();
        if (studentCount == 0) {
            // Create and seed the database with 3 sample records
            var student1 = new Student();
            student1.setFirstName("Jon");
            student1.setLastName("Snow");
            entityManager.persist(student1);

            var student2 = new Student();
            student2.setFirstName("Bart");
            student2.setLastName("GlassFish");
            entityManager.persist(student2);

            var student3 = new Student();
            student3.setFirstName("Amanda");
            student3.setLastName("Tapping");
            entityManager.persist(student3);
        }

    }
}
