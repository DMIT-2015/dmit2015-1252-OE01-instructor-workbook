package dmit2015.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class StudentInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {

        long studentCount = entityManager
                .createQuery("select count(s) from Student s", Long.class)
                .getSingleResult();
        if (studentCount == 0) {
            // Create and seed the database with 3 sample records
            var student1 = new Student();
            student1.setFirstName("First1");
            student1.setLastName("Last1");
            entityManager.persist(student1);

            var student2 = new Student();
            student2.setFirstName("First2");
            student2.setLastName("Last2");
            entityManager.persist(student2);

            var student3 = new Student();
            student3.setFirstName("First3");
            student3.setLastName("Last3");
            entityManager.persist(student3);
        }

    }
}
