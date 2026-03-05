package dmit2015.entity;

import dmit2015.repository.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StudentInitializer {

    @Inject
    private StudentRepository studentRepository;

    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {

        long studentCount = studentRepository.count();
        if (studentCount == 0) {
            // Create and seed the database with 3 sample records
            Seed("Nazor","Bilinskyi");
            Seed("Na Eun","Chin");
            Seed("Priyanka","Pawar");
        }

    }

    @Transactional
    private void Seed(String firstName, String lastName) {
        var currentStudent = new Student();
        currentStudent.setFirstName(firstName);
        currentStudent.setLastName(lastName);
        studentRepository.add(currentStudent);
    }
}
