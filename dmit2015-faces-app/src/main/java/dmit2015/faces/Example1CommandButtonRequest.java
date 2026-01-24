package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.omnifaces.util.Messages;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Request-scoped backing bean: new instance per HTTP request.
 * Use for simple actions/data that don't need to persist after the response.
 */
@Named("example1CommandButtonRequest")
@RequestScoped // New instance per HTTP request; no Serializable required
public class Example1CommandButtonRequest {

    private static final Logger LOG = Logger.getLogger(Example1CommandButtonRequest.class.getName());

    public void onSubmitActionListener() {
        try {
            Messages.addGlobalInfo("ActionListener button clicked.");
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
    }

    public String onSubmitAction() {
        try {
            Messages.addGlobalInfo("Action button clicked.");
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
        return null;
    }

    public String onNavigateToHelloWorldForward() {
        return "/helloWorld";
    }

    public String onNavigateToHelloWorldRedirect() {
        return "/helloWorld?faces-redirect=true";
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
            // No FacesContext available; skip UI notification safely.
        }
    }
}
