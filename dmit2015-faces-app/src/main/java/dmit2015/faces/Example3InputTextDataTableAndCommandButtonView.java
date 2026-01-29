package dmit2015.faces;

import dmit2015.model.Task;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;

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

    @Getter
    private List<Task> completedTasks = new ArrayList<>();

    @Getter @Setter
    private Task currentTask = new Task();  // The task to add

    @PostConstruct
    public void init() {
        // Seed tasks with 5 records
        var faker = new Faker();
        String[] priorities = {"Low","Medium","High"};
        var randomGenerator = RandomGenerator.getDefault();
        for (int counter = 1; counter <= 5; counter++) {
            // Crate a new Task
            Task currentTask = new Task();
            // Assigned a random description using a location from the Fallout provider
            currentTask.setDescription("Nuke " + faker.fallout().location());
            // Assign a random priority (Low,Medium,High)
            int randomIndex = randomGenerator.nextInt(0, priorities.length);
            String randomPriority = priorities[randomIndex];
            currentTask.setPriority(randomPriority);
            // Assign a random done status (true/false)
            currentTask.setDone(randomGenerator.nextBoolean());
            // Add the currentTask to our list of tasks
            if (currentTask.isDone()) {
                completedTasks.add(currentTask);
            } else {
                tasks.add(currentTask);
            }

        }
    }

    public void onTaskDone(Task selectedTask) {
        selectedTask.setDone(true);
        completedTasks.add(selectedTask);
        tasks.remove(selectedTask);
        Messages.addGlobalInfo("Task {0} is done", selectedTask);
    }

    public void onTaskUndone(Task selectedTask) {
        selectedTask.setDone(false);
        tasks.add(selectedTask);
        completedTasks.remove(selectedTask);
        Messages.addGlobalInfo("Task {0} is undone", selectedTask);
    }

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
        completedTasks.remove(selectedTask);
        Messages.addGlobalInfo("Removed task {0}", selectedTask);
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