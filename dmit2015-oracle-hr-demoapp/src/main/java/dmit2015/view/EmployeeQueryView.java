package dmit2015.view;

import dmit2015.entity.Department;
import dmit2015.entity.Employee;
import dmit2015.repository.HumanResourcesRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.SecondaryTable;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

/**
 * View-scoped backing bean: lives across postbacks on the SAME view.
 * Destroyed when navigating away to a different view.
 */
@Named("employeeQueryView")
@ViewScoped // Survives postbacks (including AJAX) on this view; Serializable required
public class EmployeeQueryView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(EmployeeQueryView.class.getName());

    @Getter @Setter
    private Department selectedDepartment;  // Department selected from <p:autoComplete>

    @Inject
    private transient HumanResourcesRepository hrRepository;

    public List<Department> completeDepartment(String query) {
        return hrRepository.departmentsBy("%" + query + "%");
    }

    @Getter
    private List<Employee> queryResults;

    public void onSearchByDepartment() {
        try {
            queryResults = hrRepository.employeesByDepartmentId(selectedDepartment.getId());
            Messages.addGlobalInfo("Query returned {0} results.", queryResults.size());
        } catch (Exception ex) {
            handleException(ex,"Unable to process search request");
        }
    }

    public void onClear() {
        selectedDepartment = null;
        queryResults = null;
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