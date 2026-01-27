package dmit2015.faces;

import dmit2015.model.Task;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View-scoped backing bean: lives across postbacks on the SAME view.
 * Destroyed when navigating away to a different view.
 */
@Named("example3InputTextDataTableAndCommandButtonView")
@ViewScoped // Survives postbacks (including AJAX) on this view; Serializable required
public class Example3InputTextDataTableAndCommandButtonView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(Example3InputTextDataTableAndCommandButtonView.class.getName());

    @Getter
    private List<Task> tasks = new ArrayList<>();   // The list of tasks added

    @Getter @Setter
    private Task currentTask = new Task();  // The task to add

    public void onSubmitAddTask() {
        try {
            // Add the currentTask to our list of tasks
            tasks.add(currentTask);
            // Add a GlobalInfo success message
            Messages.addGlobalInfo("Added task {0}", currentTask);
            // Create a new Task to add
            currentTask = new Task();
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
    }

    public void onClear() {
        // Remove all tasks
       tasks.clear();
    }

    public void onRemoveTask(Task selectedTask) {
        tasks.remove(selectedTask);
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