package dmit2015.repository;

import dmit2015.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MovieRepository {

    @Inject
    private SecurityContext securityContext;

    @PersistenceContext //(unitName = "mssql-dmit2015-jpa-pu") // unitName is optional if persistence.xml contains only one persistence-unit
    private EntityManager em;


    private void requiresAuthentication() {
        String username = securityContext.getCallerPrincipal().getName();
        // The username anonymous is returned if user is not authenticated
        if (username.equalsIgnoreCase("anonymous")) {
            throw new RuntimeException("Access denied. Authentication is required.");
        }
    }
    private void requiresSalesRole() {
        requiresAuthentication();
        boolean hasRequiredRole = securityContext.isCallerInRole("Sales");
        if (!hasRequiredRole) {
            throw new RuntimeException("Access denied. Your role does not have permission to perform this task");
        }
    }

    @Transactional
    public void add(Movie newMovie) {
        // Only the Sales role are allowed to add movies
        requiresSalesRole();

        // Assign ownership of the movie
        String username = securityContext.getCallerPrincipal().getName();
        newMovie.setUsername(username);

        em.persist(newMovie);
    }

    @Transactional
    public void update(Movie updatedMovie) {
        // Only the Sales role are allowed to update movies
        requiresSalesRole();

        Optional<Movie> optionalMovie = findOptionalById(updatedMovie.getId());
        if (optionalMovie.isPresent()) {
            Movie existingMovie = optionalMovie.orElseThrow();
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setGenre(updatedMovie.getGenre());
            existingMovie.setPrice(updatedMovie.getPrice());
            existingMovie.setRating(updatedMovie.getRating());
            existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
            em.merge(existingMovie);
        }
    }

    @Transactional
    public void delete(Movie existingMovie) {
        if (!em.contains(existingMovie)) {
            existingMovie = em.merge(existingMovie);
        }
        em.remove(existingMovie);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<Movie> optionalMovie = findOptionalById(id);
        if (optionalMovie.isPresent()) {
            Movie existingMovie = optionalMovie.orElseThrow();
            em.remove(existingMovie);
        }
    }

    public Movie findById(Long id) {
        return em.find(Movie.class, id);
    }

    public Optional<Movie> findOptionalById(Long id) {
        try {
            Movie querySingleResult = findById(id);
            return Optional.of(querySingleResult);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public List<Movie> findAll() {
        // Restrict access to the Sales, Administration, and Human Resources roles
        // Sales role have access to their own movies
        // Administration and Human Resources can access all movies
        requiresAuthentication();
        boolean hasRequiredRoles = securityContext.isCallerInRole("Sales")
                || securityContext.isCallerInRole("Administration")
                || securityContext.isCallerInRole("Human Resources");
        if (!hasRequiredRoles) {
            throw new RuntimeException("Access denied. Your role does not have permission.");
        }
        boolean hasSalesRole = securityContext.isCallerInRole("Sales");
        if (hasSalesRole) {
            String username = securityContext.getCallerPrincipal().getName();
            return em.createQuery("SELECT m FROM Movie m WHERE m.username = :uname ", Movie.class)
                    .setParameter("uname", username)
                    .getResultList();
        }
        return em.createQuery("SELECT m FROM Movie m ", Movie.class)
                .getResultList();
    }

    public List<Movie> findAllOrderByTitle() {
        return em.createQuery("SELECT m FROM Movie m ORDER BY m.title", Movie.class)
                .getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(m) FROM Movie m", Long.class).getSingleResult().longValue();
    }

    @Transactional
    public void deleteAll() {
        em.createQuery("DELETE FROM Movie").executeUpdate();
    }

}

