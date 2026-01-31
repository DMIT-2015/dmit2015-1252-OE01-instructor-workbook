package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import java.util.UUID;
import java.util.random.RandomGenerator;

//@ToString()
//@Getter @Setter
@Data   // includes @Getter, @Setter, @ToString, and more
@NoArgsConstructor
public class Task {

    private String id;  // unique identifier

    @NotBlank(message = "Task description is required")
    @Size(min=3, max=150,
            message = "Task description must contain {min} and {max} characters in length.")
    private String description;

    @NotBlank(message = "Priority must be Low, Medium, or High")
    @Pattern(regexp = "(?i)Low|Medium|High", message = "Priority must be Low, Medium, or High")
    private String priority;    // Low, Medium, High

    private boolean done;

    public Task(Task other) {
        this.id = other.getId();
        this.description = other.getDescription();
        this.priority = other.getPriority();
        this.done = other.isDone();
    }

    public static Task copyOf(Task other) {
        return new Task(other);
    }


    public static Task of(Faker faker) {
        Task currentTask = new Task();
        currentTask.setId(UUID.randomUUID().toString());
        currentTask.setDescription("Nuke " + faker.fallout().location());
        // Define a array of possible values
        String[] possiblePriorities = {"Low","Medium","High"};
        // Generate an index to pick from the array
        int randomIndex = RandomGenerator.getDefault().nextInt(0, possiblePriorities.length);
        currentTask.setPriority(possiblePriorities[randomIndex]);
        currentTask.setDone(RandomGenerator.getDefault().nextBoolean());
        return currentTask;
    }

}
