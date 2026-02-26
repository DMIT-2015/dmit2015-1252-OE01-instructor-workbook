package dmit2015.view;

import dmit2015.entity.Student;
import dmit2015.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import org.omnifaces.util.Messages;

/**
 * View-scoped backing bean: lives across postbacks on the SAME view.
 * Destroyed when navigating away to a different view.
 */
@Named("manageStudentsView")
@ViewScoped // Survives postbacks (including AJAX) on this view; Serializable required
public class ManageStudentsView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ManageStudentsView.class.getName());

    @Inject
    private StudentRepository studentRepository;

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @Getter
    private Student currentStudent = new Student();

    public void onSubmit() {
        try {
            // Add the currentStudent to the database
            studentRepository.add(currentStudent);
            // Send a success feedback message
            Messages.addGlobalInfo("Successfully added with ID {0}", currentStudent.getId());
            // Create another student
            currentStudent = new Student();
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
    }

    public void onClear() {
        // Reset view state
        currentStudent = new Student();
    }

    /**
     * Log server-side and show a concise root-cause chain in the UI.
     * Assumes the page includes <p:messages id="error" />.
     */
    protected void handleException(Throwable ex, String userMessage) {
        LOG.log(Level.SEVERE, userMessage != null ? userMessage : "Unhandled error", ex);

        StringBuilder details = new StringBuilder();
        Throwable t = ex;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null && !msg.isBlank()) {
                details.append(t.getClass().getSimpleName())
                        .append(": ")
                        .append(msg);
                if (t.getCause() != null) details.append("  Caused by: ");
            }
            t = t.getCause();
        }

        try {
            Messages.create(userMessage != null ? userMessage : "An unexpected error occurred.")
                    .detail(details.toString())
                    .error()
                    .add("messages");
        } catch (Throwable ignored) {
            // No FacesContext available; skip UI message safely.
        }
    }
}