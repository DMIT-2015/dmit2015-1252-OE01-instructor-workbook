package dmit2015.repository;

import dmit2015.config.ApplicationConfig;
import dmit2015.entity.Movie;
import dmit2015.entity.Student;
import dmit2015.entity.StudentInitializer;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void findAll_whenSeeded_returnsStudentsInExpectedOrder() {
        // Arrange and Act
        List<Student> students = studentRepository.findAll();
        // Assert
        assertEquals(3, students.size());
    }
}
