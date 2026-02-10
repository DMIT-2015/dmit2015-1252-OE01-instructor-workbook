package dmit2015.service;

import dmit2015.model.Student;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import net.datafaker.Faker;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Named("memoryStudentService")
@ApplicationScoped
public class MemoryStudentService implements StudentService {

    private final List<Student> students = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {

        var faker = new Faker();
        for (int counter = 1; counter <= 5; counter++) {
            var currentStudent = Student.of(faker);
            students.add(currentStudent);
        }

    }

    @Override
    public Student createStudent(Student student) {
        Objects.requireNonNull(student, "Student to create must not be null");

        // Assign a fresh id on create to ensure uniqueness (ignore any incoming id)
        Student stored = Student.copyOf(student);
        stored.setId(UUID.randomUUID().toString());
        students.add(stored);

        // Return a defensive copy
        return Student.copyOf(stored);
    }

    @Override
    public Optional<Student> getStudentById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        return students.stream()
                .filter(currentStudent -> currentStudent.getId().equals(id))
                .findFirst()
                .map(Student::copyOf); // return a copy to avoid external mutation

    }

    @Override
    public List<Student> getAllStudents() {
        // Unmodifiable snapshot of copies
        return students.stream().map(Student::copyOf).toList();
    }

    @Override
    public Student updateStudent(Student student) {
        Objects.requireNonNull(student, "Student to update must not be null");
        Objects.requireNonNull(student.getId(), "Student id must not be null");

        // Find index of existing task by id
        int index = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(student.getId())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new NoSuchElementException("Could not find Task with id: " + student.getId());
        }

        // Replace stored item with a copy (preserve id)
        Student stored = Student.copyOf(student);
        students.set(index, stored);

        return Student.copyOf(stored);
    }

    @Override
    public void deleteStudentById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        boolean removed = students.removeIf(currentStudent -> id.equals(currentStudent.getId()));
        if (!removed) {
            throw new NoSuchElementException("Could not find Task with id: " + id);
        }
    }
}
