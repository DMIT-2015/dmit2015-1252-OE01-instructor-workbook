package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//@ToString()
//@Getter @Setter
@Data   // includes @Getter, @Setter, @ToString, and more
public class Task {

    @NotBlank(message = "Task description is required")
    @Size(min=3, max=150,
            message = "Task description must contain {min} and {max} characters in length.")
    private String description;

    @NotBlank(message = "Priority must be Low, Medium, or High")
    @Pattern(regexp = "(?i)Low|Medium|High", message = "Priority must be Low, Medium, or High")
    private String priority;    // Low, Medium, High

    private boolean done;

}
