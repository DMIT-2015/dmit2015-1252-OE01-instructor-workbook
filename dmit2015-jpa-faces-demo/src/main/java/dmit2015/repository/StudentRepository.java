package dmit2015.repository;

import dmit2015.entity.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class StudentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void add(Student newStudent) {
        entityManager.persist(newStudent);
    }

    public Student findById(Integer id) {
        return entityManager.find(Student.class, id);
    }

    public List<Student> findAll() {
        return entityManager
                .createQuery("select s from Student s order by s.lastName, s.firstName", Student.class)
                .getResultList();
    }

    public long count() {
        return entityManager
                .createQuery("select count(s) from Student s", Long.class)
                .getSingleResult();
    }
}
