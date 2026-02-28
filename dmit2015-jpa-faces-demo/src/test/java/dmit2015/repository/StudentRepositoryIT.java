package dmit2015.repository;

import dmit2015.config.ApplicationConfig;
import dmit2015.entity.Student;
import dmit2015.entity.StudentInitializer;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ArquillianTest
public class StudentRepositoryIT {

    @Deployment
    static WebArchive createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
/*
   <dependency>
            <groupId>net.datafaker</groupId>
            <artifactId>datafaker</artifactId>
            <version>2.5.3</version>
        </dependency>
 */
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsLibraries(pom.resolve("com.h2database:h2:2.3.232").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.oracle.database.jdbc:ojdbc11:23.26.0.0.0").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.postgresql:postgresql:42.7.8").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.mariadb.jdbc:mariadb-java-client:3.5.3").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.hamcrest:hamcrest:3.0").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("net.datafaker:datafaker:2.5.3").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Student.class, StudentInitializer.class, StudentRepository.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Inject
    private StudentRepository studentRepository;

    @Resource
    private UserTransaction userTransaction;

    @Test
    void findAll_whenSeeded_returnsStudentsInExpectedOrder() {
        // Arrange and Act
        List<Student> students = studentRepository.findAll();
        // Assert
        assertEquals(3, students.size());
        Student first = students.getFirst();
        assertAll("first student",
                () -> assertEquals("Nazor", first.getFirstName()),
                () -> assertEquals("Bilinskyi", first.getLastName())
        );
        Student last = students.getLast();
        assertAll("last student",
                () -> assertEquals("Priyanka", last.getFirstName()),
                () -> assertEquals("Pawar", last.getLastName())
        );
    }

    @Test
    void add_whenValidData_persistsAndSetsCreateTime() throws SystemException, NotSupportedException {
        userTransaction.begin();
        try {
            // Arrange
            Student newStudent = new Student();
            newStudent.setFirstName("Dylan");
            newStudent.setLastName("Polo");

            // Act
            studentRepository.add(newStudent);

            // Assert
            Student savedStudent = studentRepository.findById(newStudent.getId());
            assertAll("saved student",
                    () -> assertEquals(newStudent.getFirstName(), savedStudent.getFirstName()),
                    () -> assertEquals(newStudent.getLastName(), savedStudent.getLastName()),
                    () -> assertNotNull(savedStudent.getCreateTime()),
                    ()-> assertNull(savedStudent.getUpdateTime())
            );

            long secondsSinceCreate = savedStudent.getCreateTime().until(LocalDateTime.now(), ChronoUnit.SECONDS);
            assertEquals(0, secondsSinceCreate);

        } finally {
            userTransaction.rollback();
        }
    }
}
