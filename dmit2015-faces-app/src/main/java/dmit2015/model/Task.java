package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString()
public class Task {

    @Getter @Setter
    @NotBlank(message = "Task description is required")
    @Size(min=3, max=150,
            message = "Task description must contain {min} and {max} characters in length.")
    private String description;

}
